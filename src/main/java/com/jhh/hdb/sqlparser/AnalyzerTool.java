package com.jhh.hdb.sqlparser;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.plan.CreateTableDesc;
import org.apache.hadoop.hive.ql.session.SessionState;
//
//import com.mysql.jdbc.DatabaseMetaData.TableType;


/**
 * Implementation of the semantic analyzer. It generates the query plan.
 * There are other specific semantic analyzers for some hive operations such as
 * DDLSemanticAnalyzer for ddl operations.
 */

public class AnalyzerTool  {

  public static final String DUMMY_DATABASE = "_dummy_database";
  public static final String DUMMY_TABLE = "_dummy_table";
  public static final String SUBQUERY_TAG_1 = "-subquery1";
  public static final String SUBQUERY_TAG_2 = "-subquery2";

  // Max characters when auto generating the column name with func name
  private static final int AUTOGEN_COLALIAS_PRFX_MAXLENGTH = 20;

  private static final String VALUES_TMP_TABLE_NAME_PREFIX = "Values__Tmp__Table__";
  public AnalyzerTool() throws SemanticException {}

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

  public static String unescapeIdentifier(String val) {
	    if (val == null) {
	      return null;
	    }
	    if (val.charAt(0) == '`' && val.charAt(val.length() - 1) == '`') {
	      val = val.substring(1, val.length() - 1);
	    }
	    return val;
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

 


  private void warn(String msg) {
    SessionState.getConsole().printInfo(
        String.format("Warning: %s", msg));
  }
}
