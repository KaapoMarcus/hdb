package com.jhh.hdb.sqlparser2;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableHint;
import gudusoft.gsqlparser.nodes.mssql.TQueryHint;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import gudusoft.gsqlparser.stmt.TCreateIndexSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlSetRowCount;
import gudusoft.gsqlparser.stmt.mysql.TMySQLCreateFunction;
import gudusoft.gsqlparser.stmt.mysql.TMySQLDeclare;
import gudusoft.gsqlparser.stmt.mysql.TMySQLIfStmt;
import gudusoft.gsqlparser.stmt.mysql.TMySQLReturn;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreatePackage;

public class test {
   public static void main(String args[])
    {
    long t = System.currentTimeMillis();




    TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);

    sqlparser.sqlfilename  = "/kuaipan/self/temp2.txt";

    int ret = sqlparser.parse();
    if (ret == 0){
        for(int i=0;i<sqlparser.sqlstatements.size();i++){
            analyzeStmt(sqlparser.sqlstatements.get(i));
            System.out.println("");
        }
    }else{
        System.out.println(sqlparser.getErrormessage());
    }

    System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }
   

   protected static void analyzeStmt(TCustomSqlStatement stmt){

       switch(stmt.sqlstatementtype){
           case sstselect:
               analyzeSelectStmt((TSelectSqlStatement)stmt);
               break;

           default:
               System.out.println(stmt.sqlstatementtype.toString());
               System.out.println(stmt.toString());
       }
   }
   

   protected static void analyzeSelectStmt(TSelectSqlStatement pStmt){
       System.out.println("\nSelect:");
       if (pStmt.isCombinedQuery()){
           String setstr="";
           switch (pStmt.getSetOperator()){
               case 1: setstr = "union";break;
               case 2: setstr = "union all";break;
               case 3: setstr = "intersect";break;
               case 4: setstr = "intersect all";break;
               case 5: setstr = "minus";break;
               case 6: setstr = "minus all";break;
               case 7: setstr = "except";break;
               case 8: setstr = "except all";break;
           }
           System.out.printf("set type: %s\n",setstr);
           System.out.println("left select:");
           
           analyzeSelectStmt(pStmt.getLeftStmt());
           System.out.println("right select:");
           analyzeSelectStmt(pStmt.getRightStmt());
           if (pStmt.getOrderbyClause() != null){
               System.out.printf("order by clause %s\n",pStmt.getOrderbyClause().toString());
           }
       }else{
           //select list
           for(int i=0; i < pStmt.getResultColumnList().size();i++){
               TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);
               System.out.printf("Column: %s, Alias: %s\n",resultColumn.getExpr().toString(), (resultColumn.getAliasClause() == null)?"":resultColumn.getAliasClause().toString());
           }

           //from clause, check this document for detailed information
           //http://www.sqlparser.com/sql-parser-query-join-table.php
           for(int i=0;i<pStmt.joins.size();i++){
               TJoin join = pStmt.joins.getJoin(i);
               switch (join.getKind()){
                   case TBaseType.join_source_fake:
                       analyzeTable(join.getTable());
                       System.out.printf("table: %s, alias: %s\n",join.getTable().toString(),(join.getTable().getAliasClause() !=null)?join.getTable().getAliasClause().toString():"");
                       break;
                   case TBaseType.join_source_table:
                       analyzeTable(join.getTable());
                       System.out.printf("table: %s, alias: %s\n",join.getTable().toString(),(join.getTable().getAliasClause() !=null)?join.getTable().getAliasClause().toString():"");
                       for(int j=0;j<join.getJoinItems().size();j++){
                           TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
                           System.out.printf("Join type: %s\n",joinItem.getJoinType().toString());
                           analyzeTable(joinItem.getTable());
                           System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                           if (joinItem.getOnCondition() != null){
                               System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                           }else  if (joinItem.getUsingColumns() != null){
                               System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                           }
                       }
                       break;
                   case TBaseType.join_source_join:
                       TJoin source_join = join.getJoin();
                       analyzeTable(source_join.getTable());
                       System.out.printf("table: %s, alias: %s\n",source_join.getTable().toString(),(source_join.getTable().getAliasClause() !=null)?source_join.getTable().getAliasClause().toString():"");

                       for(int j=0;j<source_join.getJoinItems().size();j++){
                           TJoinItem joinItem = source_join.getJoinItems().getJoinItem(j);
                           System.out.printf("source_join type: %s\n",joinItem.getJoinType().toString());
                           analyzeTable(joinItem.getTable());
                           System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                           if (joinItem.getOnCondition() != null){
                               System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                           }else  if (joinItem.getUsingColumns() != null){
                               System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                           }
                       }

                       for(int j=0;j<join.getJoinItems().size();j++){
                           TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
                           System.out.printf("Join type: %s\n",joinItem.getJoinType().toString());
                            analyzeTable(joinItem.getTable());
                           System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
                           if (joinItem.getOnCondition() != null){
                               System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
                           }else  if (joinItem.getUsingColumns() != null){
                               System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
                           }
                       }

                       break;
                   default:
                       System.out.println("unknown type in join!");
                       break;
               }
           }

           //where clause
           if (pStmt.getWhereClause() != null){
               System.out.printf("where clause: \n%s\n", pStmt.getWhereClause().toString());
           }

           // group by
           if (pStmt.getGroupByClause() != null){
               System.out.printf("group by: \n%s\n",pStmt.getGroupByClause().toString());
           }

           // order by
           if (pStmt.getOrderbyClause() != null){
             System.out.printf("order by: \n%s\n",pStmt.getOrderbyClause().toString());
           }

           // for update
           if (pStmt.getForUpdateClause() != null){
               System.out.printf("for update: \n%s\n",pStmt.getForUpdateClause().toString());
           }

           // top clause
           if (pStmt.getTopClause() != null){
               System.out.printf("top clause: \n%s\n",pStmt.getTopClause().toString());
           }

           // limit clause
           if (pStmt.getLimitClause() != null){
               System.out.printf("top clause: \n%s\n",pStmt.getLimitClause().toString());
           }

           if (pStmt.getOptionClause() != null){
               for(int k=0;k<pStmt.getOptionClause().getQueryHints().size();k++){
                   TQueryHint qh = pStmt.getOptionClause().getQueryHints().getElement(k);
                   System.out.println("query hint type:"+qh.getQueryHintType());
                   switch (qh.getQueryHintType()){
                       case E_QUERY_HINT_TABLE_HINT:
                           System.out.println("exposed_object_name:"+qh.getExposed_object_name().toString());
                           for(int m=0;m<qh.getTableHints().size();m++){
                               TTableHint th = qh.getTableHints().getElement(m);
                               analyzeTableHint(th);
                           }
                           break;
                       default:
                           break;
                   }
               }
           }
       }
   }
   
   protected static void analyzeTable(TTable table){
       System.out.format("Table Type: %s\n",table.getTableType().toString());
       switch (table.getTableType()){
           case objectname:
               System.out.format("Table name: %s\n",table.getTableName().toString());
               break;
           case subquery:
               break;
           default:
               break;
       }
       if (table.getAliasClause() != null){
           System.out.format("Table alias: %s\n",table.getAliasClause().toString()) ;
       }
       if (table.getTableHintList() != null){
          for(int i=0;i<table.getTableHintList().size();i++){
              TTableHint th = table.getTableHintList().getElement(i);
              analyzeTableHint(th);
          }
       }
   }
   
   protected static void analyzeTableHint(TTableHint th){
       System.out.println("Table hint:");
       if (th.isIndex()){
           System.out.println("hint: index");
           if (th.getExprList() != null){
               for(int j=0;j<th.getExprList().size();j++){
                 System.out.println("Index value:"+th.getExprList().getElement(j).toString());
               }
           }else{
               System.out.println("Index value:"+th.getHint().toString());
           }
       }else{
           System.out.println("hint: "+th.getHint().toString());
       }
   }
}