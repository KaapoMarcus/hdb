package com.jhh.hdb.sqlparser2;


import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.*;

public class columnInClause  {

    public columnInClause(){}

    public void printColumns(TExpression expression,TCustomSqlStatement statement){
        System.out.println("Referenced columns:");
        columnVisitor cv = new columnVisitor(statement);
        expression.postOrderTraverse(cv);
    }

    public void printColumns(TGroupByItemList list,TCustomSqlStatement statement){
        System.out.println("Referenced columns:");
        columnExprVisitor gbv = new columnExprVisitor(statement);
        list.accept(gbv);
    }

    public void printColumns(TOrderBy orderBy,TCustomSqlStatement statement){
        System.out.println("Referenced columns:");
        columnExprVisitor obv = new columnExprVisitor(statement);
        orderBy.accept(obv);
    }

}

class columnVisitor implements IExpressionVisitor {

    TCustomSqlStatement statement = null;

    public columnVisitor(TCustomSqlStatement statement) {
        this.statement = statement;
    }

    String getColumnWithBaseTable(TObjectName objectName){
        String ret = "";
        TTable table = null;
        boolean  find = false;
        TCustomSqlStatement lcStmt = statement;

        while ((lcStmt != null) && (!find)){
            for(int i=0;i<lcStmt.tables.size();i++){
                table = lcStmt.tables.getTable(i);
                for(int j=0;j<table.getLinkedColumns().size();j++){
                    if (objectName == table.getLinkedColumns().getObjectName(j)){
                        if(table.isBaseTable()){
                            ret =  table.getTableName()+"."+objectName.getColumnNameOnly();
                        }else{
                            //derived table
                            if (table.getAliasClause() != null){
                               ret =  table.getAliasClause().toString()+"."+objectName.getColumnNameOnly();
                            }else {
                                ret =  objectName.getColumnNameOnly();
                            }

                            ret += "(column in derived table)";
                        }
                        find = true;
                        break;
                    }
                }
            }
            if(!find){
                lcStmt = lcStmt.getParentStmt();
            }
        }

        return  ret;
    }

    public boolean exprVisit(TParseTreeNode pNode,boolean isLeafNode){
         TExpression expr = (TExpression)pNode;
         switch ((expr.getExpressionType())){
             case simple_object_name_t:
                 TObjectName obj = expr.getObjectOperand();
                 if (obj.getObjectType() != TObjectName.ttobjNotAObject){
                    System.out.println(getColumnWithBaseTable(obj));
                 }
                 break;
             case function_t:
                 columnExprVisitor fcv = new columnExprVisitor(statement);
                 expr.getFunctionCall().accept(fcv);
                 break;
             case case_t:
                 TCaseExpression caseExpression = expr.getCaseExpression();
                 columnExprVisitor cv = new columnExprVisitor(statement);
                 caseExpression.accept(cv);

                 break;
         }
         return  true;
     }

}

class columnExprVisitor extends TParseTreeVisitor{

    TCustomSqlStatement statement = null;

    public columnExprVisitor(TCustomSqlStatement statement) {
        this.statement = statement;
    }

    public void preVisit(TExpression expression){
        columnVisitor cv = new columnVisitor(statement);
        expression.postOrderTraverse(cv);
    }
}

//class functionCallVisitor extends TParseTreeVisitor{
//
//    TCustomSqlStatement statement = null;
//
//    public functionCallVisitor(TCustomSqlStatement statement) {
//        this.statement = statement;
//    }
//
//    public void preVisit(TExpression expression){
//        columnVisitor cv = new columnVisitor(statement);
//        expression.postOrderTraverse(cv);
//    }
//}

//
//class groupByVisitor extends TParseTreeVisitor{
//
//    TCustomSqlStatement statement = null;
//
//    public groupByVisitor(TCustomSqlStatement statement) {
//        this.statement = statement;
//    }
//
//    public void preVisit(TExpression expression){
//        columnVisitor cv = new columnVisitor(statement);
//        expression.postOrderTraverse(cv);
//    }
//}

//class orderByVisitor extends TParseTreeVisitor{
//
//    TCustomSqlStatement statement = null;
//
//    public orderByVisitor(TCustomSqlStatement statement) {
//        this.statement = statement;
//    }
//
//    public void preVisit(TExpression expression){
//        columnVisitor cv = new columnVisitor(statement);
//        expression.postOrderTraverse(cv);
//    }
//}

