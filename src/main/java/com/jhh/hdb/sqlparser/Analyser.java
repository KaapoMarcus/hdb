package com.jhh.hdb.sqlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.hadoop.hive.ql.ErrorMsg;
import org.apache.hadoop.hive.ql.exec.AbstractMapJoinOperator;
import org.apache.hadoop.hive.ql.exec.Operator;
import org.apache.hadoop.hive.ql.exec.TableScanOperator;
import org.apache.hadoop.hive.ql.optimizer.unionproc.UnionProcContext;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.plan.CreateTableDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDescUtils;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.apache.hadoop.hive.ql.plan.FilterDesc.SampleDesc;
import org.apache.hadoop.hive.ql.plan.MapJoinDesc;
import org.apache.hadoop.hive.ql.plan.PlanUtils;
import org.apache.hadoop.hive.ql.session.SessionState;

import com.test.stats.sql.Phase1Ctx;
import com.test.stats.sql.PlannerContext;


public class Analyser extends BaseSemanticAnalyzer {

  public static final String DUMMY_DATABASE = "_dummy_database";
  public static final String DUMMY_TABLE = "_dummy_table";
  public static final String SUBQUERY_TAG_1 = "-subquery1";
  public static final String SUBQUERY_TAG_2 = "-subquery2";

  // Max characters when auto generating the column name with func name
  private static final int AUTOGEN_COLALIAS_PRFX_MAXLENGTH = 20;

  private static final String VALUES_TMP_TABLE_NAME_PREFIX = "Values__Tmp__Table__";

  private QB qb;
  private ASTNode ast;
  private int destTableId;
  private UnionProcContext uCtx;
  List<AbstractMapJoinOperator<? extends MapJoinDesc>> listMapJoinOpsNoReducer;
  private HashMap<TableScanOperator, SampleDesc> opToSamplePruner;

  // flag for no scan during analyze ... compute statistics
  protected boolean noscan;

  //flag for partial scan during analyze ... compute statistics
  protected boolean partialscan;

  protected volatile boolean disableJoinMerge = false;

  /*
   * Capture the CTE definitions in a Query.
   */
//  private final Map<String, ASTNode> aliasToCTEs;
  /*
   * Used to check recursive CTE invocations. Similar to viewsExpanded
   */
  private ArrayList<String> ctesExpanded;

//  protected AnalyzeRewriteContext analyzeRewrite;
  private CreateTableDesc tableDesc;

/*  static class Phase1Ctx {
    String dest;
    int nextNum;
  }*/

  public Analyser() throws SemanticException {}


  @SuppressWarnings("nls")
  public void doPhase1QBExpr(ASTNode ast, QBExpr qbexpr, String id, String alias)
      throws SemanticException {

    assert (ast.getToken() != null);
    switch (ast.getToken().getType()) {
    case HiveParser.TOK_QUERY: {
      QB qb = new QB(id, alias, true);
      Phase1Ctx ctx_1 = initPhase1Ctx();
      analyze(ast, qb, ctx_1);

      qbexpr.setOpcode(QBExpr.Opcode.NULLOP);
      qbexpr.setQB(qb);
    }
      break;
    case HiveParser.TOK_UNIONALL: {
      qbexpr.setOpcode(QBExpr.Opcode.UNION);
      // query 1
      assert (ast.getChild(0) != null);
      QBExpr qbexpr1 = new QBExpr(alias + SUBQUERY_TAG_1);
      doPhase1QBExpr((ASTNode) ast.getChild(0), qbexpr1, id + SUBQUERY_TAG_1,
          alias + SUBQUERY_TAG_1);
      qbexpr.setQBExpr1(qbexpr1);

      // query 2
      assert (ast.getChild(1) != null);
      QBExpr qbexpr2 = new QBExpr(alias + SUBQUERY_TAG_2);
      doPhase1QBExpr((ASTNode) ast.getChild(1), qbexpr2, id + SUBQUERY_TAG_2,
          alias + SUBQUERY_TAG_2);
      qbexpr.setQBExpr2(qbexpr2);
    }
      break;
    }
  }

  private LinkedHashMap<String, ASTNode> doPhase1GetAggregationsFromSelect(
      ASTNode selExpr, QB qb, String dest) throws SemanticException {

    // Iterate over the selects search for aggregation Trees.
    // Use String as keys to eliminate duplicate trees.
    LinkedHashMap<String, ASTNode> aggregationTrees = new LinkedHashMap<String, ASTNode>();
    List<ASTNode> wdwFns = new ArrayList<ASTNode>();
    for (int i = 0; i < selExpr.getChildCount(); ++i) {
      ASTNode function = (ASTNode) selExpr.getChild(i);
      if (function.getType() == HiveParser.TOK_SELEXPR ||
          function.getType() == HiveParser.TOK_SUBQUERY_EXPR) {
        function = (ASTNode)function.getChild(0);
      }
      doPhase1GetAllAggregations(function, aggregationTrees, wdwFns);
    }

    // window based aggregations are handled differently
/*    for (ASTNode wdwFn : wdwFns) {
      WindowingSpec spec = qb.getWindowingSpec(dest);
      if(spec == null) {
        queryProperties.setHasWindowing(true);
        spec = new WindowingSpec();
        qb.addDestToWindowingSpec(dest, spec);
      }
      HashMap<String, ASTNode> wExprsInDest = qb.getParseInfo().getWindowingExprsForClause(dest);
      int wColIdx = spec.getWindowExpressions() == null ? 0 : spec.getWindowExpressions().size();
      WindowFunctionSpec wFnSpec = processWindowFunction(wdwFn,
        (ASTNode)wdwFn.getChild(wdwFn.getChildCount()-1));
      // If this is a duplicate invocation of a function; don't add to WindowingSpec.
      if ( wExprsInDest != null &&
          wExprsInDest.containsKey(wFnSpec.getExpression().toStringTree())) {
        continue;
      }
      wFnSpec.setAlias(wFnSpec.getName() + "_window_" + wColIdx);
      spec.addWindowFunction(wFnSpec);
      qb.getParseInfo().addWindowingExprToClause(dest, wFnSpec.getExpression());
    }*/

    return aggregationTrees;
  }

  private void doPhase1GetColumnAliasesFromSelect(
      ASTNode selectExpr, QBParseInfo qbp) {
    for (int i = 0; i < selectExpr.getChildCount(); ++i) {
      ASTNode selExpr = (ASTNode) selectExpr.getChild(i);
      if ((selExpr.getToken().getType() == HiveParser.TOK_SELEXPR)
          && (selExpr.getChildCount() == 2)) {
        String columnAlias = unescapeIdentifier(selExpr.getChild(1).getText());
        qbp.setExprToColumnAlias((ASTNode) selExpr.getChild(0), columnAlias);
      }
    }
  }

  /**
   * DFS-scan the expressionTree to find all aggregation subtrees and put them
   * in aggregations.
   *
   * @param expressionTree
   * @param aggregations
   *          the key to the HashTable is the toStringTree() representation of
   *          the aggregation subtree.
   * @throws SemanticException
   */
  private void doPhase1GetAllAggregations(ASTNode expressionTree,
      HashMap<String, ASTNode> aggregations, List<ASTNode> wdwFns) throws SemanticException {
    int exprTokenType = expressionTree.getToken().getType();
    if (exprTokenType == HiveParser.TOK_FUNCTION
        || exprTokenType == HiveParser.TOK_FUNCTIONDI
        || exprTokenType == HiveParser.TOK_FUNCTIONSTAR) {
      assert (expressionTree.getChildCount() != 0);
      if (expressionTree.getChild(expressionTree.getChildCount()-1).getType()
          == HiveParser.TOK_WINDOWSPEC) {
        wdwFns.add(expressionTree);
        return;
      }
      if (expressionTree.getChild(0).getType() == HiveParser.Identifier) {
        String functionName = unescapeIdentifier(expressionTree.getChild(0)
            .getText());
        
          aggregations.put(expressionTree.toStringTree().toLowerCase(), expressionTree);

          return;
        
      }
    }
    for (int i = 0; i < expressionTree.getChildCount(); i++) {
      doPhase1GetAllAggregations((ASTNode) expressionTree.getChild(i),
          aggregations, wdwFns);
    }
  }

  private List<ASTNode> doPhase1GetDistinctFuncExprs(
      HashMap<String, ASTNode> aggregationTrees) throws SemanticException {
    List<ASTNode> exprs = new ArrayList<ASTNode>();
    for (Map.Entry<String, ASTNode> entry : aggregationTrees.entrySet()) {
      ASTNode value = entry.getValue();
      assert (value != null);
      if (value.getToken().getType() == HiveParser.TOK_FUNCTIONDI) {
        exprs.add(value);
      }
    }
    return exprs;
  }

  public static String generateErrorMessage(ASTNode ast, String message) {
    StringBuilder sb = new StringBuilder();
    if (ast == null) {
      sb.append(message).append(". Cannot tell the position of null AST.");
      return sb.toString();
    }
    sb.append(ast.getLine());
    sb.append(":");
    sb.append(ast.getCharPositionInLine());
    sb.append(" ");
    sb.append(message);
    sb.append(". Error encountered near token '");
//    sb.append(ErrorMsg.getText(ast));
    sb.append("'");
    return sb.toString();
  }

  ASTNode getAST() {
    return this.ast;
  }

  protected void setAST(ASTNode newAST) {
    this.ast = newAST;
  }

  /**
   * Goes though the tabref tree and finds the alias for the table. Once found,
   * it records the table name-> alias association in aliasToTabs. It also makes
   * an association from the alias to the table AST in parse info.
   *
   * @return the alias of the table
   */
  private String processTable(QB qb, ASTNode tabref) throws SemanticException {
    // For each table reference get the table name
    // and the alias (if alias is not present, the table name
    // is used as an alias)
    int aliasIndex = 0;
    for (int index = 1; index < tabref.getChildCount(); index++) {
      ASTNode ct = (ASTNode) tabref.getChild(index);
        aliasIndex = index;
    }

    ASTNode tableTree = (ASTNode) (tabref.getChild(0));

    String tabIdName = getUnescapedName(tableTree);

    String alias;
    if (aliasIndex != 0) {
      alias = unescapeIdentifier(tabref.getChild(aliasIndex).getText());
    }
    else {
      alias = getUnescapedUnqualifiedTableName(tableTree);
    }


    return alias;
  }



  // Take an expression in the values clause and turn it back into a string.  This is far from
  // comprehensive.  At the moment it only supports:
  // * literals (all types)
  // * unary negatives
  // * true/false
  private String unparseExprForValuesClause(ASTNode expr) throws SemanticException {
    switch (expr.getToken().getType()) {
      case HiveParser.Number:
        return expr.getText();

      case HiveParser.StringLiteral:
        return PlanUtils.stripQuotes(expr.getText());

      case HiveParser.KW_FALSE:
        // UDFToBoolean casts any non-empty string to true, so set this to false
        return "";

      case HiveParser.KW_TRUE:
        return "TRUE";

      case HiveParser.MINUS:
        return "-" + unparseExprForValuesClause((ASTNode)expr.getChildren().get(0));

      case HiveParser.TOK_NULL:
        // Hive's text input will translate this as a null
        return "\\N";

      default:
        throw new SemanticException("Expression of type " + expr.getText() +
            " not supported in insert/values");
    }

  }

/*  private void assertCombineInputFormat(Tree numerator, String message) throws SemanticException {
    String inputFormat = conf.getVar(HiveConf.ConfVars.HIVE_EXECUTION_ENGINE).equals("tez") ?
      HiveConf.getVar(conf, HiveConf.ConfVars.HIVETEZINPUTFORMAT):
      HiveConf.getVar(conf, HiveConf.ConfVars.HIVEINPUTFORMAT);
    if (!inputFormat.equals(CombineHiveInputFormat.class.getName())) {
      throw new SemanticException(generateErrorMessage((ASTNode) numerator,
          message + " sampling is not supported in " + inputFormat));
    }
  }*/

  private String processSubQuery(QB qb, ASTNode subq) throws SemanticException {

    // This is a subquery and must have an alias
    if (subq.getChildCount() != 2) {
      throw new SemanticException(ErrorMsg.NO_SUBQUERY_ALIAS.getMsg(subq));
    }
    ASTNode subqref = (ASTNode) subq.getChild(0);
    String alias = unescapeIdentifier(subq.getChild(1).getText());

    // Recursively do the first phase of semantic analysis for the subquery
    QBExpr qbexpr = new QBExpr(alias);

    doPhase1QBExpr(subqref, qbexpr, qb.getId(), alias);

    // If the alias is already there then we have a conflict
    if (qb.exists(alias)) {
      throw new SemanticException(ErrorMsg.AMBIGUOUS_TABLE_ALIAS.getMsg(subq
          .getChild(1)));
    }
    // Insert this map into the stats
    qb.setSubqAlias(alias, qbexpr);
    qb.addAlias(alias);
//
//    unparseTranslator.addIdentifierTranslation((ASTNode) subq.getChild(1));

    return alias;
  }


  static boolean isJoinToken(ASTNode node) {
    if ((node.getToken().getType() == HiveParser.TOK_JOIN)
        || (node.getToken().getType() == HiveParser.TOK_CROSSJOIN)
        || isOuterJoinToken(node)
        || (node.getToken().getType() == HiveParser.TOK_LEFTSEMIJOIN)
        || (node.getToken().getType() == HiveParser.TOK_UNIQUEJOIN)) {
      return true;
    }

    return false;
  }

  static private boolean isOuterJoinToken(ASTNode node) {
    return (node.getToken().getType() == HiveParser.TOK_LEFTOUTERJOIN)
      || (node.getToken().getType() == HiveParser.TOK_RIGHTOUTERJOIN)
      || (node.getToken().getType() == HiveParser.TOK_FULLOUTERJOIN);
  }

  /**
   * Given the AST with TOK_JOIN as the root, get all the aliases for the tables
   * or subqueries in the join.
   *
   * @param qb
   * @param join
   * @throws SemanticException
   */
  @SuppressWarnings("nls")
  private void processJoin(QB qb, ASTNode join) throws SemanticException {
    int numChildren = join.getChildCount();
    if ((numChildren != 2) && (numChildren != 3)
        && join.getToken().getType() != HiveParser.TOK_UNIQUEJOIN) {
      throw new SemanticException(generateErrorMessage(join,
          "Join with multiple children"));
    }


    for (int num = 0; num < numChildren; num++) {
      ASTNode child = (ASTNode) join.getChild(num);
      if (child.getToken().getType() == HiveParser.TOK_TABREF) {
        processTable(qb, child);
      } else if (child.getToken().getType() == HiveParser.TOK_SUBQUERY) {
        processSubQuery(qb, child);
      } else if (isJoinToken(child)) {
        processJoin(qb, child);
      }
    }
  }
/*
 * 
 * 
 */
  @SuppressWarnings({"fallthrough", "nls"})
  public boolean analyze(ASTNode ast, QB qb, Phase1Ctx ctx_1)
      throws SemanticException {

    boolean phase1Result = true;
    QBParseInfo qbp = qb.getParseInfo();
    boolean skipRecursion = false;

    if (ast.getToken() != null) {
      skipRecursion = true;
      switch (ast.getToken().getType()) {
      
      case HiveParser.TOK_SELECT:
        
        qbp.setSelExprForClause(ctx_1.dest, ast);
		if(qb.qbProp.have_group){
		    LinkedHashMap<String, ASTNode> aggregations = doPhase1GetAggregationsFromSelect(ast,
		            qb, ctx_1.dest);
		        doPhase1GetColumnAliasesFromSelect(ast, qbp);
		        qbp.setAggregationExprsForClause(ctx_1.dest, aggregations);
		        qbp.setDistinctFuncExprsForClause(ctx_1.dest,
		        doPhase1GetDistinctFuncExprs(aggregations));
		}

        break;

      case HiveParser.TOK_WHERE:
        qbp.setWhrExprForClause(ctx_1.dest, ast);
        if (!SubQueryUtils.findSubQueries((ASTNode) ast.getChild(0)).isEmpty())
        	qb.qbProp.setFilterWithSubQuery(true);
        break;

      case HiveParser.TOK_FROM:
        int child_count = ast.getChildCount();
        if (child_count != 1) {
          throw new SemanticException(generateErrorMessage(ast,
              "Multiple Children " + child_count));
        }
        // Check if this is a subquery / lateral view
        ASTNode frm = (ASTNode) ast.getChild(0);
        if (frm.getToken().getType() == HiveParser.TOK_TABREF) {
          processTable(qb, frm);
        } else if (frm.getToken().getType() == HiveParser.TOK_SUBQUERY) {
          processSubQuery(qb, frm);
          qb.qbProp.have_from_subquery=true;
        }  else if (isJoinToken(frm)) {
          processJoin(qb, frm);
          qbp.setJoinExpr(frm);
          qb.qbProp.have_from_join=true;
        }
        break;

      case HiveParser.TOK_ORDERBY:

        qbp.setOrderByExprForClause(ctx_1.dest, ast);
        if (qbp.getClusterByForClause(ctx_1.dest) != null) {
          throw new SemanticException(generateErrorMessage(ast,
              ErrorMsg.CLUSTERBY_ORDERBY_CONFLICT.getMsg()));
        }
        qb.qbProp.have_order=true;
        break;

      case HiveParser.TOK_GROUPBY:
      case HiveParser.TOK_ROLLUP_GROUPBY:
      case HiveParser.TOK_CUBE_GROUPBY:
      case HiveParser.TOK_GROUPING_SETS:
        // Get the groupby aliases - these are aliased to the entries in the
        // select list
    	  qb.qbProp.setHasGroupBy(true);
        if (qbp.getJoinExpr() != null) {
        	qb.qbProp.setHasJoinFollowedByGroupBy(true);
        }
        if (qbp.getSelForClause(ctx_1.dest).getToken().getType() == HiveParser.TOK_SELECTDI) {
          throw new SemanticException(generateErrorMessage(ast,
              ErrorMsg.SELECT_DISTINCT_WITH_GROUPBY.getMsg()));
        }
        qbp.setGroupByExprForClause(ctx_1.dest, ast);
        skipRecursion = true;
        qb.qbProp.have_group=true;
        break;

      case HiveParser.TOK_HAVING:
        qbp.setHavingExprForClause(ctx_1.dest, ast);
        qbp.addAggregationExprsForClause(ctx_1.dest,
            doPhase1GetAggregationsFromSelect(ast, qb, ctx_1.dest));
        qb.qbProp.have_having=true;
        break;



      case HiveParser.TOK_LIMIT:
        qbp.setDestLimit(ctx_1.dest, new Integer(ast.getChild(0).getText()));
        qb.qbProp.have_limit=true;
        break;

      case HiveParser.TOK_UNIONDISTINCT:
    	  
          skipRecursion = false;
          qb.qbProp.have_union=true;
          break;
          
      case HiveParser.TOK_UNIONALL:
        if (!qbp.getIsSubQ()) {
          // this shouldn't happen. The parser should have converted the union to be
          // contained in a subquery. Just in case, we keep the error as a fallback.
          throw new SemanticException(generateErrorMessage(ast,
              ErrorMsg.UNION_NOTIN_SUBQ.getMsg()));
        }
        qb.qbProp.have_unionall=true;
        skipRecursion = false;
        break;

      default:
        skipRecursion = false;
        break;
      }
    }

    if (!skipRecursion) {
      // Iterate over the rest of the children
      int child_count = ast.getChildCount();
      for (int child_pos = 0; child_pos < child_count && phase1Result; ++child_pos) {
        // Recurse
        phase1Result = phase1Result && analyze(
            (ASTNode)ast.getChild(child_pos), qb, ctx_1);
      }
    }
    return phase1Result;
  }

  private boolean isPresent(String[] list, String elem) {
    for (String s : list) {
      if (s.toLowerCase().equals(elem)) {
        return true;
      }
    }

    return false;
  }

  /*
   * This method is invoked for unqualified column references in join conditions.
   * This is passed in the Alias to Operator mapping in the QueryBlock so far.
   * We try to resolve the unqualified column against each of the Operator Row Resolvers.
   * - if the column is present in only one RowResolver, we treat this as a reference to
   *   that Operator.
   * - if the column resolves with more than one RowResolver, we treat it as an Ambiguous
   *   reference.
   * - if the column doesn't resolve with any RowResolver, we treat this as an Invalid
   *   reference.
   */
  @SuppressWarnings("rawtypes")
  private String findAlias(ASTNode columnRef,
      Map<String, Operator> aliasToOpInfo) throws SemanticException {
	  
	  return "" ;
	  /*
    String colName = unescapeIdentifier(columnRef.getChild(0).getText()
        .toLowerCase());
    String tabAlias = null;
    if ( aliasToOpInfo != null ) {
      for (Map.Entry<String, Operator> opEntry : aliasToOpInfo.entrySet()) {
        Operator op = opEntry.getValue();
        RowResolver rr = opParseCtx.get(op).getRowResolver();
        ColumnInfo colInfo = rr.get(null, colName);
        if (colInfo != null) {
          if (tabAlias == null) {
            tabAlias = opEntry.getKey();
          } else {
            throw new SemanticException(
                ErrorMsg.AMBIGUOUS_TABLE_ALIAS.getMsg(columnRef.getChild(0)));
          }
        }
      }
    }
    if ( tabAlias == null ) {
      throw new SemanticException(ErrorMsg.INVALID_TABLE_ALIAS.getMsg(columnRef
          .getChild(0)));
    }
    return tabAlias;
  */}

  @SuppressWarnings("nls")
  void parseJoinCondPopulateAlias(QBJoinTree joinTree, ASTNode condn,
      ArrayList<String> leftAliases, ArrayList<String> rightAliases,
      ArrayList<String> fields,
      Map<String, Operator> aliasToOpInfo) throws SemanticException {
    // String[] allAliases = joinTree.getAllAliases();
    switch (condn.getToken().getType()) {
    case HiveParser.TOK_TABLE_OR_COL:
      String tableOrCol = unescapeIdentifier(condn.getChild(0).getText()
          .toLowerCase());
//      unparseTranslator.addIdentifierTranslation((ASTNode) condn.getChild(0));
      if (isPresent(joinTree.getLeftAliases(), tableOrCol)) {
        if (!leftAliases.contains(tableOrCol)) {
          leftAliases.add(tableOrCol);
        }
      } else if (isPresent(joinTree.getRightAliases(), tableOrCol)) {
        if (!rightAliases.contains(tableOrCol)) {
          rightAliases.add(tableOrCol);
        }
      } else {
        tableOrCol = findAlias(condn, aliasToOpInfo);
        if (isPresent(joinTree.getLeftAliases(), tableOrCol)) {
          if (!leftAliases.contains(tableOrCol)) {
            leftAliases.add(tableOrCol);
          }
        } else  {
          if (!rightAliases.contains(tableOrCol)) {
            rightAliases.add(tableOrCol);
          }
        }
      }
      break;

    case HiveParser.Identifier:
      // it may be a field name, return the identifier and let the caller decide
      // whether it is or not
      if (fields != null) {
        fields
            .add(unescapeIdentifier(condn.getToken().getText().toLowerCase()));
      }
//      unparseTranslator.addIdentifierTranslation(condn);
      break;
    case HiveParser.Number:
    case HiveParser.StringLiteral:
    case HiveParser.BigintLiteral:
    case HiveParser.SmallintLiteral:
    case HiveParser.TinyintLiteral:
    case HiveParser.DecimalLiteral:
    case HiveParser.TOK_STRINGLITERALSEQUENCE:
    case HiveParser.TOK_CHARSETLITERAL:
    case HiveParser.KW_TRUE:
    case HiveParser.KW_FALSE:
      break;

    case HiveParser.TOK_FUNCTION:
      // check all the arguments
      for (int i = 1; i < condn.getChildCount(); i++) {
        parseJoinCondPopulateAlias(joinTree, (ASTNode) condn.getChild(i),
            leftAliases, rightAliases, null, aliasToOpInfo);
      }
      break;

    default:
      // This is an operator - so check whether it is unary or binary operator
      if (condn.getChildCount() == 1) {
        parseJoinCondPopulateAlias(joinTree, (ASTNode) condn.getChild(0),
            leftAliases, rightAliases, null, aliasToOpInfo);
      } else if (condn.getChildCount() == 2) {

        ArrayList<String> fields1 = null;
        // if it is a dot operator, remember the field name of the rhs of the
        // left semijoin
        if (joinTree.getNoSemiJoin() == false
            && condn.getToken().getType() == HiveParser.DOT) {
          // get the semijoin rhs table name and field name
          fields1 = new ArrayList<String>();
          int rhssize = rightAliases.size();
          parseJoinCondPopulateAlias(joinTree, (ASTNode) condn.getChild(0),
              leftAliases, rightAliases, null, aliasToOpInfo);
          String rhsAlias = null;

          if (rightAliases.size() > rhssize) { // the new table is rhs table
            rhsAlias = rightAliases.get(rightAliases.size() - 1);
          }

          parseJoinCondPopulateAlias(joinTree, (ASTNode) condn.getChild(1),
              leftAliases, rightAliases, fields1, aliasToOpInfo);
          if (rhsAlias != null && fields1.size() > 0) {
//            joinTree.addRHSSemijoinColumns(rhsAlias, condn);
          }
        } else {
          parseJoinCondPopulateAlias(joinTree, (ASTNode) condn.getChild(0),
              leftAliases, rightAliases, null, aliasToOpInfo);
          parseJoinCondPopulateAlias(joinTree, (ASTNode) condn.getChild(1),
              leftAliases, rightAliases, fields1, aliasToOpInfo);
        }
      } else {
        throw new SemanticException(condn.toStringTree() + " encountered with "
            + condn.getChildCount() + " children");
      }
      break;
    }
  }

  private void populateAliases(List<String> leftAliases,
      List<String> rightAliases, ASTNode condn, QBJoinTree joinTree,
      List<String> leftSrc) throws SemanticException {/*
    if ((leftAliases.size() != 0) && (rightAliases.size() != 0)) {
      throw new SemanticException(ErrorMsg.INVALID_JOIN_CONDITION_1
          .getMsg(condn));
    }

    if (rightAliases.size() != 0) {
      assert rightAliases.size() == 1;
      joinTree.getExpressions().get(1).add(condn);
    } else if (leftAliases.size() != 0) {
      joinTree.getExpressions().get(0).add(condn);
      for (String s : leftAliases) {
        if (!leftSrc.contains(s)) {
          leftSrc.add(s);
        }
      }
    } else {
      throw new SemanticException(ErrorMsg.INVALID_JOIN_CONDITION_2
          .getMsg(condn));
    }
  */}

  /*
   * refactored out of the Equality case of parseJoinCondition
   * so that this can be recursively called on its left tree in the case when
   * only left sources are referenced in a Predicate
   */
  void applyEqualityPredicateToQBJoinTree(QBJoinTree joinTree,
      JoinType type,
      List<String> leftSrc,
      ASTNode joinCond,
      ASTNode leftCondn,
      ASTNode rightCondn,
      List<String> leftCondAl1,
      List<String> leftCondAl2,
      List<String> rightCondAl1,
      List<String> rightCondAl2) throws SemanticException {}

  @SuppressWarnings("rawtypes")
  private void parseJoinCondition(QBJoinTree joinTree, ASTNode joinCond, List<String> leftSrc,
      Map<String, Operator> aliasToOpInfo)
      throws SemanticException {/*
    if (joinCond == null) {
      return;
    }
    JoinCond cond = joinTree.getJoinCond()[0];

    JoinType type = cond.getJoinType();
    parseJoinCondition(joinTree, joinCond, leftSrc, type, aliasToOpInfo);

    List<ArrayList<ASTNode>> filters = joinTree.getFilters();
    if (type == JoinType.LEFTOUTER || type == JoinType.FULLOUTER) {
      joinTree.addFilterMapping(cond.getLeft(), cond.getRight(), filters.get(0).size());
    }
    if (type == JoinType.RIGHTOUTER || type == JoinType.FULLOUTER) {
      joinTree.addFilterMapping(cond.getRight(), cond.getLeft(), filters.get(1).size());
    }
  */}

  /**
   * Parse the join condition. If the condition is a join condition, throw an
   * error if it is not an equality. Otherwise, break it into left and right
   * expressions and store in the join tree. If the condition is a join filter,
   * add it to the filter list of join tree. The join condition can contains
   * conditions on both the left and tree trees and filters on either.
   * Currently, we only support equi-joins, so we throw an error if the
   * condition involves both subtrees and is not a equality. Also, we only
   * support AND i.e ORs are not supported currently as their semantics are not
   * very clear, may lead to data explosion and there is no usecase.
   *
   * @param joinTree
   *          jointree to be populated
   * @param joinCond
   *          join condition
   * @param leftSrc
   *          left sources
   * @throws SemanticException
   */
  @SuppressWarnings("rawtypes")
  private void parseJoinCondition(QBJoinTree joinTree, ASTNode joinCond,
      List<String> leftSrc, JoinType type,
      Map<String, Operator> aliasToOpInfo) throws SemanticException {
    if (joinCond == null) {
      return;
    }

    switch (joinCond.getToken().getType()) {
    case HiveParser.KW_OR:
      throw new SemanticException(ErrorMsg.INVALID_JOIN_CONDITION_3
          .getMsg(joinCond));

    case HiveParser.KW_AND:
      parseJoinCondition(joinTree, (ASTNode) joinCond.getChild(0), leftSrc, type, aliasToOpInfo);
      parseJoinCondition(joinTree, (ASTNode) joinCond.getChild(1), leftSrc, type, aliasToOpInfo);
      break;

    case HiveParser.EQUAL_NS:
    case HiveParser.EQUAL:
      ASTNode leftCondn = (ASTNode) joinCond.getChild(0);
      ArrayList<String> leftCondAl1 = new ArrayList<String>();
      ArrayList<String> leftCondAl2 = new ArrayList<String>();
      parseJoinCondPopulateAlias(joinTree, leftCondn, leftCondAl1, leftCondAl2,
          null, aliasToOpInfo);

      ASTNode rightCondn = (ASTNode) joinCond.getChild(1);
      ArrayList<String> rightCondAl1 = new ArrayList<String>();
      ArrayList<String> rightCondAl2 = new ArrayList<String>();
      parseJoinCondPopulateAlias(joinTree, rightCondn, rightCondAl1,
          rightCondAl2, null, aliasToOpInfo);

      // is it a filter or a join condition
      // if it is filter see if it can be pushed above the join
      // filter cannot be pushed if
      // * join is full outer or
      // * join is left outer and filter is on left alias or
      // * join is right outer and filter is on right alias
      if (((leftCondAl1.size() != 0) && (leftCondAl2.size() != 0))
          || ((rightCondAl1.size() != 0) && (rightCondAl2.size() != 0))) {
        throw new SemanticException(ErrorMsg.INVALID_JOIN_CONDITION_1
            .getMsg(joinCond));
      }

      applyEqualityPredicateToQBJoinTree(joinTree, type, leftSrc,
          joinCond, leftCondn, rightCondn,
          leftCondAl1, leftCondAl2,
          rightCondAl1, rightCondAl2);

      break;

    default:/*
      boolean isFunction = (joinCond.getType() == HiveParser.TOK_FUNCTION);

      // Create all children
      int childrenBegin = (isFunction ? 1 : 0);
      ArrayList<ArrayList<String>> leftAlias = new ArrayList<ArrayList<String>>(
          joinCond.getChildCount() - childrenBegin);
      ArrayList<ArrayList<String>> rightAlias = new ArrayList<ArrayList<String>>(
          joinCond.getChildCount() - childrenBegin);
      for (int ci = 0; ci < joinCond.getChildCount() - childrenBegin; ci++) {
        ArrayList<String> left = new ArrayList<String>();
        ArrayList<String> right = new ArrayList<String>();
        leftAlias.add(left);
        rightAlias.add(right);
      }

      for (int ci = childrenBegin; ci < joinCond.getChildCount(); ci++) {
        parseJoinCondPopulateAlias(joinTree, (ASTNode) joinCond.getChild(ci),
            leftAlias.get(ci - childrenBegin), rightAlias.get(ci
                - childrenBegin), null, aliasToOpInfo);
      }

      boolean leftAliasNull = true;
      for (ArrayList<String> left : leftAlias) {
        if (left.size() != 0) {
          leftAliasNull = false;
          break;
        }
      }

      boolean rightAliasNull = true;
      for (ArrayList<String> right : rightAlias) {
        if (right.size() != 0) {
          rightAliasNull = false;
          break;
        }
      }

      if (!leftAliasNull && !rightAliasNull) {
        throw new SemanticException(ErrorMsg.INVALID_JOIN_CONDITION_1
            .getMsg(joinCond));
      }

      if (!leftAliasNull) {
        if (type.equals(JoinType.LEFTOUTER)
            || type.equals(JoinType.FULLOUTER)) {
          if (conf.getBoolVar(HiveConf.ConfVars.HIVEOUTERJOINSUPPORTSFILTERS)) {
            joinTree.getFilters().get(0).add(joinCond);
          } else {
            LOG.warn(ErrorMsg.OUTERJOIN_USES_FILTERS);
            joinTree.getFiltersForPushing().get(0).add(joinCond);
          }
        } else {
          joinTree.getFiltersForPushing().get(0).add(joinCond);
        }
      } else {
        if (type.equals(JoinType.RIGHTOUTER)
            || type.equals(JoinType.FULLOUTER)) {
          if (conf.getBoolVar(HiveConf.ConfVars.HIVEOUTERJOINSUPPORTSFILTERS)) {
            joinTree.getFilters().get(1).add(joinCond);
          } else {
            LOG.warn(ErrorMsg.OUTERJOIN_USES_FILTERS);
            joinTree.getFiltersForPushing().get(1).add(joinCond);
          }
        } else {
          joinTree.getFiltersForPushing().get(1).add(joinCond);
        }
      }
*/
      break;
    }
  }

  @SuppressWarnings("rawtypes")
  private void extractJoinCondsFromWhereClause(QBJoinTree joinTree, QB qb, String dest, ASTNode predicate,
      Map<String, Operator> aliasToOpInfo) throws SemanticException {

    switch (predicate.getType()) {
    case HiveParser.KW_AND:
      extractJoinCondsFromWhereClause(joinTree, qb, dest,
          (ASTNode) predicate.getChild(0), aliasToOpInfo);
      extractJoinCondsFromWhereClause(joinTree, qb, dest,
          (ASTNode) predicate.getChild(1), aliasToOpInfo);
      break;
    case HiveParser.EQUAL_NS:
    case HiveParser.EQUAL:

      ASTNode leftCondn = (ASTNode) predicate.getChild(0);
      ArrayList<String> leftCondAl1 = new ArrayList<String>();
      ArrayList<String> leftCondAl2 = new ArrayList<String>();
      try {
        parseJoinCondPopulateAlias(joinTree, leftCondn, leftCondAl1, leftCondAl2,
          null, aliasToOpInfo);
      } catch(SemanticException se) {
        // suppress here; if it is a real issue will get caught in where clause handling.
        return;
      }

      ASTNode rightCondn = (ASTNode) predicate.getChild(1);
      ArrayList<String> rightCondAl1 = new ArrayList<String>();
      ArrayList<String> rightCondAl2 = new ArrayList<String>();
      try {
        parseJoinCondPopulateAlias(joinTree, rightCondn, rightCondAl1,
            rightCondAl2, null, aliasToOpInfo);
      } catch(SemanticException se) {
        // suppress here; if it is a real issue will get caught in where clause handling.
        return;
      }

      if (((leftCondAl1.size() != 0) && (leftCondAl2.size() != 0))
          || ((rightCondAl1.size() != 0) && (rightCondAl2.size() != 0))) {
        // this is not a join condition.
        return;
      }

      if (((leftCondAl1.size() == 0) && (leftCondAl2.size() == 0))
          || ((rightCondAl1.size() == 0) && (rightCondAl2.size() == 0))) {
        // this is not a join condition. Will get handled by predicate pushdown.
        return;
      }

      List<String> leftSrc = new ArrayList<String>();
      JoinCond cond = joinTree.getJoinCond()[0];
      JoinType type = cond.getJoinType();
      applyEqualityPredicateToQBJoinTree(joinTree, type, leftSrc,
          predicate, leftCondn, rightCondn,
          leftCondAl1, leftCondAl2,
          rightCondAl1, rightCondAl2);
      if (leftSrc.size() == 1) {
        joinTree.setLeftAlias(leftSrc.get(0));
      }

      // todo: hold onto this predicate, so that we don't add it to the Filter Operator.

      break;
    default:
      return;
    }
  }


  protected List<Integer> getGroupingSetsForRollup(int size) {
    List<Integer> groupingSetKeys = new ArrayList<Integer>();
    for (int i = 0; i <= size; i++) {
      groupingSetKeys.add((1 << i) - 1);
    }
    return groupingSetKeys;
  }

  protected List<Integer> getGroupingSetsForCube(int size) {
    int count = 1 << size;
    List<Integer> results = new ArrayList<Integer>(count);
    for (int i = 0; i < count; ++i) {
      results.add(i);
    }
    return results;
  }



  protected List<Integer> getGroupingSets(List<ASTNode> groupByExpr, QBParseInfo parseInfo,
      String dest) throws SemanticException {
    Map<String, Integer> exprPos = new HashMap<String, Integer>();
    for (int i = 0; i < groupByExpr.size(); ++i) {
      ASTNode node = groupByExpr.get(i);
      exprPos.put(node.toStringTree(), i);
    }

    ASTNode root = parseInfo.getGroupByForClause(dest);
    List<Integer> result = new ArrayList<Integer>(root == null ? 0 : root.getChildCount());
    if (root != null) {
      for (int i = 0; i < root.getChildCount(); ++i) {
        ASTNode child = (ASTNode) root.getChild(i);
        if (child.getType() != HiveParser.TOK_GROUPING_SETS_EXPRESSION) {
          continue;
        }
        int bitmap = 0;
        for (int j = 0; j < child.getChildCount(); ++j) {
          String treeAsString = child.getChild(j).toStringTree();
          Integer pos = exprPos.get(treeAsString);
          if (pos == null) {
            throw new SemanticException(
                generateErrorMessage((ASTNode) child.getChild(j),
                    ErrorMsg.HIVE_GROUPING_SETS_EXPR_NOT_IN_GROUPBY.getErrorCodedMsg()));
          }
          bitmap = setBit(bitmap, pos);
        }
        result.add(bitmap);
      }
    }
    if (checkForNoAggr(result)) {
      throw new SemanticException(
          ErrorMsg.HIVE_GROUPING_SETS_AGGR_NOFUNC.getMsg());
    }
    return result;
  }

  private boolean checkForNoAggr(List<Integer> bitmaps) {
    boolean ret = true;
    for (int mask : bitmaps) {
      ret &= mask == 0;
    }
    return ret;
  }

  public static int setBit(int bitmap, int bitIdx) {
    return bitmap | (1 << bitIdx);
  }

  /**
   * This function is a wrapper of parseInfo.getGroupByForClause which
   * automatically translates SELECT DISTINCT a,b,c to SELECT a,b,c GROUP BY
   * a,b,c.
   */
  static List<ASTNode> getGroupByForClause(QBParseInfo parseInfo, String dest) {
    if (parseInfo.getSelForClause(dest).getToken().getType() == HiveParser.TOK_SELECTDI) {
      ASTNode selectExprs = parseInfo.getSelForClause(dest);
      List<ASTNode> result = new ArrayList<ASTNode>(selectExprs == null ? 0
          : selectExprs.getChildCount());
      if (selectExprs != null) {
        HashMap<String, ASTNode> windowingExprs = parseInfo.getWindowingExprsForClause(dest);

        for (int i = 0; i < selectExprs.getChildCount(); ++i) {
          if (((ASTNode) selectExprs.getChild(i)).getToken().getType() == HiveParser.TOK_HINTLIST) {
            continue;
          }
          // table.column AS alias
          ASTNode grpbyExpr = (ASTNode) selectExprs.getChild(i).getChild(0);
          /*
           * If this is handled by Windowing then ignore it.
           */
          if (windowingExprs != null && windowingExprs.containsKey(grpbyExpr.toStringTree())) {
            continue;
          }
          result.add(grpbyExpr);
        }
      }
      return result;
    } else {
      ASTNode grpByExprs = parseInfo.getGroupByForClause(dest);
      List<ASTNode> result = new ArrayList<ASTNode>(grpByExprs == null ? 0
          : grpByExprs.getChildCount());
      if (grpByExprs != null) {
        for (int i = 0; i < grpByExprs.getChildCount(); ++i) {
          ASTNode grpbyExpr = (ASTNode) grpByExprs.getChild(i);
          if (grpbyExpr.getType() != HiveParser.TOK_GROUPING_SETS_EXPRESSION) {
            result.add(grpbyExpr);
          }
        }
      }
      return result;
    }
  }


  /**
   * Returns whether the pattern is a regex expression (instead of a normal
   * string). Normal string is a string with all alphabets/digits and "_".
   */
  static boolean isRegex(String pattern) {

    for (int i = 0; i < pattern.length(); i++) {
      if (!Character.isLetterOrDigit(pattern.charAt(i))
          && pattern.charAt(i) != '_') {
        return true;
      }
    }
    return false;
  }


  // Remove expression node descriptor and children of it for a given predicate
  // from mapping if it's already on RS keys.
  // Remaining column expressions would be a candidate for an RS value
  private void removeMappingForKeys(ASTNode predicate, Map<ASTNode, ExprNodeDesc> mapping,
      List<ExprNodeDesc> keys) {
    ExprNodeDesc expr = mapping.get(predicate);
    if (expr != null && ExprNodeDescUtils.indexOf(expr, keys) >= 0) {
      removeRecursively(predicate, mapping);
    } else {
      for (int i = 0; i < predicate.getChildCount(); i++) {
        removeMappingForKeys((ASTNode) predicate.getChild(i), mapping, keys);
      }
    }
  }

  // Remove expression node desc and all children of it from mapping
  private void removeRecursively(ASTNode current, Map<ASTNode, ExprNodeDesc> mapping) {
    mapping.remove(current);
    for (int i = 0; i < current.getChildCount(); i++) {
      removeRecursively((ASTNode) current.getChild(i), mapping);
    }
  }


  private boolean optimizeMapAggrGroupBy(String dest, QB qb) {
    List<ASTNode> grpByExprs = getGroupByForClause(qb.getParseInfo(), dest);
    if ((grpByExprs != null) && !grpByExprs.isEmpty()) {
      return false;
    }

    if (!qb.getParseInfo().getDistinctFuncExprsForClause(dest).isEmpty()) {
      return false;
    }

    return true;
  }

  static private void extractColumns(Set<String> colNamesExprs,
      ExprNodeDesc exprNode) throws SemanticException {
    if (exprNode instanceof ExprNodeColumnDesc) {
      colNamesExprs.add(((ExprNodeColumnDesc) exprNode).getColumn());
      return;
    }

    if (exprNode instanceof ExprNodeGenericFuncDesc) {
      ExprNodeGenericFuncDesc funcDesc = (ExprNodeGenericFuncDesc) exprNode;
      for (ExprNodeDesc childExpr : funcDesc.getChildren()) {
        extractColumns(colNamesExprs, childExpr);
      }
    }
  }

  static private boolean hasCommonElement(Set<String> set1, Set<String> set2) {
    for (String elem1 : set1) {
      if (set2.contains(elem1)) {
        return true;
      }
    }

    return false;
  }


  // The join alias is modified before being inserted for consumption by sort-merge
  // join queries. If the join is part of a sub-query the alias is modified to include
  // the sub-query alias.
  private String getModifiedAlias(QB qb, String alias) {
    return QB.getAppendedAliasFromId(qb.getId(), alias);
  }

  private String extractJoinAlias(ASTNode node, String tableName) {
    // ptf node form is:
    // ^(TOK_PTBLFUNCTION $name $alias? partitionTableFunctionSource partitioningSpec? expression*)
    // guaranteed to have an alias here: check done in processJoin
    if (node.getType() == HiveParser.TOK_PTBLFUNCTION) {
      return unescapeIdentifier(node.getChild(1).getText().toLowerCase());
    }
    if (node.getChildCount() == 1) {
      return tableName;
    }
    for (int i = node.getChildCount() - 1; i >= 1; i--) {
      if (node.getChild(i).getType() == HiveParser.Identifier) {
        return unescapeIdentifier(node.getChild(i).getText().toLowerCase());
      }
    }
    return tableName;
  }



  /**
   * Merges node to target
   */
  private void mergeJoins(QB qb, QBJoinTree node, QBJoinTree target, int pos, int[] tgtToNodeExprMap) {/*
    String[] nodeRightAliases = node.getRightAliases();
    String[] trgtRightAliases = target.getRightAliases();
    String[] rightAliases = new String[nodeRightAliases.length
        + trgtRightAliases.length];

    for (int i = 0; i < trgtRightAliases.length; i++) {
      rightAliases[i] = trgtRightAliases[i];
    }
    for (int i = 0; i < nodeRightAliases.length; i++) {
      rightAliases[i + trgtRightAliases.length] = nodeRightAliases[i];
    }
    target.setRightAliases(rightAliases);
    target.getAliasToOpInfo().putAll(node.getAliasToOpInfo());

    String[] nodeBaseSrc = node.getBaseSrc();
    String[] trgtBaseSrc = target.getBaseSrc();
    String[] baseSrc = new String[nodeBaseSrc.length + trgtBaseSrc.length - 1];

    for (int i = 0; i < trgtBaseSrc.length; i++) {
      baseSrc[i] = trgtBaseSrc[i];
    }
    for (int i = 1; i < nodeBaseSrc.length; i++) {
      baseSrc[i + trgtBaseSrc.length - 1] = nodeBaseSrc[i];
    }
    target.setBaseSrc(baseSrc);

    ArrayList<ArrayList<ASTNode>> expr = target.getExpressions();
    for (int i = 0; i < nodeRightAliases.length; i++) {
      List<ASTNode> nodeConds = node.getExpressions().get(i + 1);
      ArrayList<ASTNode> reordereNodeConds = new ArrayList<ASTNode>();
      for(int k=0; k < tgtToNodeExprMap.length; k++) {
        reordereNodeConds.add(nodeConds.get(tgtToNodeExprMap[k]));
      }
      expr.add(reordereNodeConds);
    }

    ArrayList<Boolean> nns = node.getNullSafes();
    ArrayList<Boolean> tns = target.getNullSafes();
    for (int i = 0; i < tns.size(); i++) {
      tns.set(i, tns.get(i) & nns.get(i)); // any of condition contains non-NS, non-NS
    }

    ArrayList<ArrayList<ASTNode>> filters = target.getFilters();
    for (int i = 0; i < nodeRightAliases.length; i++) {
      filters.add(node.getFilters().get(i + 1));
    }

    if (node.getFilters().get(0).size() != 0) {
      ArrayList<ASTNode> filterPos = filters.get(pos);
      filterPos.addAll(node.getFilters().get(0));
    }

    int[][] nmap = node.getFilterMap();
    int[][] tmap = target.getFilterMap();
    int[][] newmap = new int[tmap.length + nmap.length - 1][];

    for (int[] mapping : nmap) {
      if (mapping != null) {
        for (int i = 0; i < mapping.length; i += 2) {
          if (pos > 0 || mapping[i] > 0) {
            mapping[i] += trgtRightAliases.length;
          }
        }
      }
    }
    if (nmap[0] != null) {
      if (tmap[pos] == null) {
        tmap[pos] = nmap[0];
      } else {
        int[] appended = new int[tmap[pos].length + nmap[0].length];
        System.arraycopy(tmap[pos], 0, appended, 0, tmap[pos].length);
        System.arraycopy(nmap[0], 0, appended, tmap[pos].length, nmap[0].length);
        tmap[pos] = appended;
      }
    }
    System.arraycopy(tmap, 0, newmap, 0, tmap.length);
    System.arraycopy(nmap, 1, newmap, tmap.length, nmap.length - 1);
    target.setFilterMap(newmap);

    ArrayList<ArrayList<ASTNode>> filter = target.getFiltersForPushing();
    for (int i = 0; i < nodeRightAliases.length; i++) {
      filter.add(node.getFiltersForPushing().get(i + 1));
    }

    if (node.getFiltersForPushing().get(0).size() != 0) {
      
       * for each predicate:
       * - does it refer to one or many aliases
       * - if one: add it to the filterForPushing list of that alias
       * - if many: add as a filter from merging trees.
       

      for(ASTNode nodeFilter : node.getFiltersForPushing().get(0) ) {
        int fPos = ParseUtils.checkJoinFilterRefersOneAlias(target.getBaseSrc(), nodeFilter);

        if ( fPos != - 1 ) {
          filter.get(fPos).add(nodeFilter);
        } else {
          target.addPostJoinFilter(nodeFilter);
        }
      }
     }

    if (node.getNoOuterJoin() && target.getNoOuterJoin()) {
      target.setNoOuterJoin(true);
    } else {
      target.setNoOuterJoin(false);
    }

    if (node.getNoSemiJoin() && target.getNoSemiJoin()) {
      target.setNoSemiJoin(true);
    } else {
      target.setNoSemiJoin(false);
    }

    target.mergeRHSSemijoin(node);

    JoinCond[] nodeCondns = node.getJoinCond();
    int nodeCondnsSize = nodeCondns.length;
    JoinCond[] targetCondns = target.getJoinCond();
    int targetCondnsSize = targetCondns.length;
    JoinCond[] newCondns = new JoinCond[nodeCondnsSize + targetCondnsSize];
    for (int i = 0; i < targetCondnsSize; i++) {
      newCondns[i] = targetCondns[i];
    }

    for (int i = 0; i < nodeCondnsSize; i++) {
      JoinCond nodeCondn = nodeCondns[i];
      if (nodeCondn.getLeft() == 0) {
        nodeCondn.setLeft(pos);
      } else {
        nodeCondn.setLeft(nodeCondn.getLeft() + targetCondnsSize);
      }
      nodeCondn.setRight(nodeCondn.getRight() + targetCondnsSize);
      newCondns[targetCondnsSize + i] = nodeCondn;
    }

    target.setJoinCond(newCondns);
    if (target.isMapSideJoin()) {
      assert node.isMapSideJoin();
      List<String> mapAliases = target.getMapAliases();
      for (String mapTbl : node.getMapAliases()) {
        if (!mapAliases.contains(mapTbl)) {
          mapAliases.add(mapTbl);
        }
      }
      target.setMapAliases(mapAliases);
    }
  */}
  boolean continueJoinMerge() {
    return true;
  }


  // Join types should be all the same for merging (or returns null)
  private JoinType getType(JoinCond[] conds) {
    JoinType type = conds[0].getJoinType();
    for (int k = 1; k < conds.length; k++) {
      if (type != conds[k].getJoinType()) {
        return null;
      }
    }
    return type;
  }


  private void combineExprNodeLists(List<ExprNodeDesc> list, List<ExprNodeDesc> list2,
      List<ExprNodeDesc> combinedList) {
    combinedList.addAll(list);
    for (ExprNodeDesc elem : list2) {
      if (!combinedList.contains(elem)) {
        combinedList.add(elem);
      }
    }
  }

  // Returns whether or not two lists contain the same elements independent of order
  private boolean matchExprLists(List<ExprNodeDesc> list1, List<ExprNodeDesc> list2) {

    if (list1.size() != list2.size()) {
      return false;
    }
    for (ExprNodeDesc exprNodeDesc : list1) {
      if (ExprNodeDescUtils.indexOf(exprNodeDesc, list2) < 0) {
        return false;
      }
    }

    return true;
  }

  // see if there are any distinct expressions
  protected static boolean distinctExprsExists(QB qb) {
    QBParseInfo qbp = qb.getParseInfo();

    TreeSet<String> ks = new TreeSet<String>();
    ks.addAll(qbp.getClauseNames());

    for (String dest : ks) {
      List<ASTNode> list = qbp.getDistinctFuncExprsForClause(dest);
      if (!list.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  protected String getAliasId(String alias, QB qb) {
    return (qb.getId() == null ? alias : qb.getId() + ":" + alias).toLowerCase();
  }

/*  static boolean isSkewedCol(String alias, QB qb, String colName) {
    boolean isSkewedCol = false;
    List<String> skewedCols = qb.getSkewedColumnNames(alias);
    for (String skewedCol : skewedCols) {
      if (skewedCol.equalsIgnoreCase(colName)) {
        isSkewedCol = true;
      }
    }
    return isSkewedCol;
  }
*/
  



  /**
   * Generates the operator DAG needed to implement lateral views and attaches
   * it to the TS operator.
   *
   * @param aliasToOpInfo
   *          A mapping from a table alias to the TS operator. This function
   *          replaces the operator mapping as necessary
   * @param qb
   * @throws SemanticException
   */


  /**
   * A helper function that gets all the columns and respective aliases in the
   * source and puts them into dest. It renames the internal names of the
   * columns based on getColumnInternalName(position).
   *
   * Note that this helper method relies on RowResolver.getColumnInfos()
   * returning the columns in the same order as they will be passed in the
   * operator DAG.
   *
   * @param source
   * @param dest
   * @param outputColNames
   *          - a list to which the new internal column names will be added, in
   *          the same order as in the dest row resolver
   */


  @SuppressWarnings("nls")
  public Phase1Ctx initPhase1Ctx() {

    Phase1Ctx ctx_1 = new Phase1Ctx();
    ctx_1.nextNum = 0;
    ctx_1.dest = "reduce";

    return ctx_1;
  }

  @Override
  public void init(boolean clearPartsCache) {
    // clear most members
//    reset(clearPartsCache);

    // init
    QB qb = new QB(null, null, false);
    this.qb = qb;
  }


  /**
   * Planner specific stuff goen in here.
   */
/*  static class PlannerContext {
    protected ASTNode   child;
    protected Phase1Ctx ctx_1;

    void setParseTreeAttr(ASTNode child, Phase1Ctx ctx_1) {
      this.child = child;
      this.ctx_1 = ctx_1;
    }

    void setCTASToken(ASTNode child) {
    }

    void setInsertToken(ASTNode ast, boolean isTmpFileDest) {
    }
  }*/
  void analyzeInternal(ASTNode ast, PlannerContext plannerCtx) throws SemanticException {}


  /**
   * Add default properties for table property. If a default parameter exists
   * in the tblProp, the value in tblProp will be kept.
   *
   * @param table
   *          property map
   * @return Modified table property map
   */
  private Map<String, String> addDefaultProperties(Map<String, String> tblProp) {
    Map<String, String> retValue =null;
/*    if (tblProp == null) {
      retValue = new HashMap<String, String>();
    } else {
      retValue = tblProp;
    }
    String paraString = HiveConf.getVar(conf, ConfVars.NEWTABLEDEFAULTPARA);
    if (paraString != null && !paraString.isEmpty()) {
      for (String keyValuePair : paraString.split(",")) {
        String[] keyValue = keyValuePair.split("=", 2);
        if (keyValue.length != 2) {
          continue;
        }
        if (!retValue.containsKey(keyValue[0])) {
          retValue.put(keyValue[0], keyValue[1]);
        }
      }
    }*/
    return retValue;
  }

  /**
   * Analyze the create table command. If it is a regular create-table or
   * create-table-like statements, we create a DDLWork and return true. If it is
   * a create-table-as-select, we get the necessary info such as the SerDe and
   * Storage Format and put it in QB, and return false, indicating the rest of
   * the semantic analyzer need to deal with the select statement with respect
   * to the SerDe and Storage Format.
   */
 
  // Process the position alias in GROUPBY and ORDERBY
  private void processPositionAlias(ASTNode ast) throws SemanticException {
    boolean isByPos = false;
/*    if (HiveConf.getBoolVar(conf,
          HiveConf.ConfVars.HIVE_GROUPBY_ORDERBY_POSITION_ALIAS) == true) {
      isByPos = true;
    }*/

    if (ast.getChildCount()  == 0) {
      return;
    }

    boolean isAllCol;
    ASTNode selectNode = null;
    ASTNode groupbyNode = null;
    ASTNode orderbyNode = null;

    // get node type
    int child_count = ast.getChildCount();
    for (int child_pos = 0; child_pos < child_count; ++child_pos) {
      ASTNode node = (ASTNode) ast.getChild(child_pos);
      int type = node.getToken().getType();
      if (type == HiveParser.TOK_SELECT) {
        selectNode = node;
      } else if (type == HiveParser.TOK_GROUPBY) {
        groupbyNode = node;
      } else if (type == HiveParser.TOK_ORDERBY) {
        orderbyNode = node;
      }
    }

    if (selectNode != null) {
      int selectExpCnt = selectNode.getChildCount();

      // replace each of the position alias in GROUPBY with the actual column name
      if (groupbyNode != null) {
        for (int child_pos = 0; child_pos < groupbyNode.getChildCount(); ++child_pos) {
          ASTNode node = (ASTNode) groupbyNode.getChild(child_pos);
          if (node.getToken().getType() == HiveParser.Number) {
            if (isByPos) {
              int pos = Integer.parseInt(node.getText());
              if (pos > 0 && pos <= selectExpCnt) {
                groupbyNode.setChild(child_pos,
                  selectNode.getChild(pos - 1).getChild(0));
              } else {
                throw new SemanticException(
                  ErrorMsg.INVALID_POSITION_ALIAS_IN_GROUPBY.getMsg(
                  "Position alias: " + pos + " does not exist\n" +
                  "The Select List is indexed from 1 to " + selectExpCnt));
              }
            } else {
              warn("Using constant number  " + node.getText() +
                " in group by. If you try to use position alias when hive.groupby.orderby.position.alias is false, the position alias will be ignored.");
            }
          }
        }
      }

      // replace each of the position alias in ORDERBY with the actual column name
      if (orderbyNode != null) {
        isAllCol = false;
        for (int child_pos = 0; child_pos < selectNode.getChildCount(); ++child_pos) {
          ASTNode node = (ASTNode) selectNode.getChild(child_pos).getChild(0);
          if (node.getToken().getType() == HiveParser.TOK_ALLCOLREF) {
            isAllCol = true;
          }
        }
        for (int child_pos = 0; child_pos < orderbyNode.getChildCount(); ++child_pos) {
          ASTNode colNode = (ASTNode) orderbyNode.getChild(child_pos);
          ASTNode node = (ASTNode) colNode.getChild(0);
          if (node.getToken().getType() == HiveParser.Number) {
            if( isByPos ) {
              if (!isAllCol) {
                int pos = Integer.parseInt(node.getText());
                if (pos > 0 && pos <= selectExpCnt) {
                  colNode.setChild(0, selectNode.getChild(pos - 1).getChild(0));
                } else {
                  throw new SemanticException(
                    ErrorMsg.INVALID_POSITION_ALIAS_IN_ORDERBY.getMsg(
                    "Position alias: " + pos + " does not exist\n" +
                    "The Select List is indexed from 1 to " + selectExpCnt));
                }
              } else {
                throw new SemanticException(
                  ErrorMsg.NO_SUPPORTED_ORDERBY_ALLCOLREF_POS.getMsg());
              }
            } else { //if not using position alias and it is a number.
              warn("Using constant number " + node.getText() +
                " in order by. If you try to use position alias when hive.groupby.orderby.position.alias is false, the position alias will be ignored.");
            }
          }
        }
      }
    }

    // Recursively process through the children ASTNodes
    for (int child_pos = 0; child_pos < child_count; ++child_pos) {
      processPositionAlias((ASTNode) ast.getChild(child_pos));
    }
    return;
  }


  public QB getQB() {
    return qb;
  }

  public void setQB(QB qb) {
    this.qb = qb;
  }

  private void warn(String msg) {
    SessionState.getConsole().printInfo(
        String.format("Warning: %s", msg));
  }
}