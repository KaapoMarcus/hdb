package com.jhh.hdb.sqlparser2.gettablecolumns;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class myMetaDB implements IMetaDatabase {

    String columns[][] = {
        {"server","db","DW","AcctInfo_PT","ACCT_ID"},
        {"server","db","DW","ImSysInfo_BC","ACCT_ID"},
        {"server","db","DW","AcctInfo_PT","SystemOfRec"},
        {"server","db","DW","ImSysInfo_BC","SystemOfRec"},
        {"server","db","DW","AcctInfo_PT","OfficerCode"},
        {"server","db","DW","ImSysInfo_BC","OpeningDate"},
    };

    public boolean checkColumn(String server, String database,String schema, String table, String column){
       boolean bServer,bDatabase,bSchema,bTable,bColumn,bRet = false;
        for (int i=0; i<columns.length;i++){
            if ((server == null)||(server.length() == 0)){
                bServer = true;
            }else{
                bServer = columns[i][0].equalsIgnoreCase(server);
            }
            if (!bServer) continue;

            if ((database == null)||(database.length() == 0)){
                bDatabase = true;
            }else{
                bDatabase = columns[i][1].equalsIgnoreCase(database);
            }
            if (!bDatabase) continue;

            if ((schema == null)||(schema.length() == 0)){
                bSchema = true;
            }else{
                bSchema = columns[i][2].equalsIgnoreCase(schema);
            }

            if (!bSchema) continue;

            bTable = columns[i][3].equalsIgnoreCase(table);
            if (!bTable) continue;

            bColumn = columns[i][4].equalsIgnoreCase(column);
            if (!bColumn) continue;

            bRet =true;
            break;

        }

        return bRet;
    }

}

public class TGetTableColumn{
    private EDbVendor dbVendor;
    private String queryStr;
    private TGSqlParser sqlParser;
    private IMetaDatabase metaDatabase = null;

    public void setMetaDatabase(IMetaDatabase metaDatabase) {
        this.metaDatabase = metaDatabase;
        sqlParser.setMetaDatabase(metaDatabase);
    }

    private StringBuffer functionlist,schemalist,
    triggerlist,sequencelist,databaselist;

    public StringBuffer infos;

    public StringBuffer outList;

    private ArrayList<String> fieldlist,tablelist;

    private StringBuffer tableColumnList;

    private String newline  = "\n";

    public boolean isConsole;
    public boolean listStarColumn;
    public boolean showTreeStructure;
    public boolean showTableEffect;
    public boolean showColumnLocation;
    public boolean linkOrphanColumnToFirstTable;

    public  TGetTableColumn(EDbVendor pDBVendor){
       dbVendor = pDBVendor;

        sqlParser = new TGSqlParser(dbVendor);
        //sqlParser.setMetaDatabase(new myMetaDB());

        tablelist = new ArrayList<String>();
        fieldlist = new ArrayList<String>();
         infos = new StringBuffer();
         functionlist = new StringBuffer();
         schemalist = new StringBuffer();
         triggerlist = new StringBuffer();
         sequencelist = new StringBuffer();
         databaselist  = new StringBuffer();
        tableColumnList  = new StringBuffer();
        outList  = new StringBuffer();
        isConsole = true;
        listStarColumn = false;
        showTreeStructure = false;
        showTableEffect = false;
        showColumnLocation = false;
        linkOrphanColumnToFirstTable = true;
    }

    public void runText(String pQuery){
        run(pQuery,false);
    }

    public void runFile(String pFileName){
        run(pFileName,true);
    }

    String numberOfSpace(int pNum){
        String ret="";
        for(int i=0;i<pNum;i++){
            ret = ret+" ";
        }
        return ret;
    }

    public StringBuffer getInfos() {
        return infos;
    }

    protected void run(String pQuery, boolean isFile){
        queryStr = pQuery;
        if (isFile) sqlParser.sqlfilename = pQuery;
        else sqlParser.sqltext = pQuery;
        int iRet = sqlParser.parse();
        if (iRet != 0){
        	if(isConsole)
        		System.out.println(sqlParser.getErrormessage());
        	else 
        		throw new RuntimeException(sqlParser.getErrormessage());
            return;
        }

        outList.setLength(0);
        tablelist.clear();
        fieldlist.clear();

        for(int i=0;i<sqlParser.sqlstatements.size();i++){
            analyzeStmt(sqlParser.sqlstatements.get(i),0);
        }

        if (showTreeStructure){
            System.out.println(infos.toString());
        }

        //System.out.println(tableColumnList.toString());
       removeDuplicateAndSort(tablelist);
       removeDuplicateAndSort(fieldlist);

       printArray("Tables:", tablelist);
       printArray("Fields:",fieldlist);
      //  System.out.println("Tables:" + newline + tablelist.toString());
      //  System.out.println("Fields:"+newline+fieldlist.toString());
    }

    void printArray(String pTitle,ArrayList<String> pList){
        if (isConsole){
            System.out.println(pTitle);
        }else {
        	//if(outList.length()>0)
        	//	outList.append(newline);
            outList.append(pTitle+newline);
        }
        Object str[] =  pList.toArray();
        for(int i=0;i<str.length;i++){
          if (isConsole){
            System.out.println(str[i]);
          }else{
              outList.append(str[i]+newline);
          }
        }
    }


    void removeDuplicateAndSort(ArrayList <String>  pList){
        Collections.sort(pList, new SortIgnoreCase() );

        for ( int i = 0 ; i < pList.size() - 1 ; i ++ ) {
             for ( int j = pList.size() - 1 ; j > i; j -- ) {
               if (pList.get(j).equalsIgnoreCase((pList.get(i)))) {
                 pList.remove(j);
               }
              }
            }
    }

    protected void analyzeStmt(TCustomSqlStatement stmt, int pNest){
        TTable lcTable = null;
        TObjectName lcColumn = null;
        String tn = "",cn="";
        //System.out.println( numberOfSpace(pNest)+ stmt.sqlstatementtype);
        infos.append(numberOfSpace(pNest) + stmt.sqlstatementtype+newline);

        for(int i=0;i<stmt.tables.size();i++){
            //if  (stmt.tables.getTable(i).isBaseTable())
            //{
                lcTable = stmt.tables.getTable(i);
                if (lcTable.getTableType() == ETableSource.subquery){
                    tn = "(subquery, alias:"+lcTable.getAliasName()+")";
                }else{
                    tn = lcTable.getTableName().toString();
                    if (lcTable.isLinkTable()){
                        tn = tn+"("+lcTable.getLinkTable().getTableName().toString()+")";
                    }else if (lcTable.isCTEName()){
                       tn = tn+"(CTE)";
                    }
                }
                //System.out.println(numberOfSpace(pNest+1)+tn.getName());
                if ((showTableEffect) &&(lcTable.isBaseTable())){
                    infos.append(numberOfSpace(pNest+1)+ tn+"("+lcTable.getEffectType()+")"+newline);
                }else{
                    infos.append(numberOfSpace(pNest+1)+ tn+newline);
                }

                tableColumnList.append(","+tn);

                if (!((lcTable.getTableType() == ETableSource.subquery) || (lcTable.isCTEName()))) {
                   if (lcTable.isLinkTable()){
                      // tablelist.append(lcTable.getLinkTable().toString()+newline);
                       tablelist.add(lcTable.getLinkTable().toString());
                   }else{
                      // tablelist.append(lcTable.toString()+newline);
                       tablelist.add(lcTable.toString());
                   }
                }

                for (int j=0;j<stmt.tables.getTable(i).getLinkedColumns().size();j++){
                    lcColumn = stmt.tables.getTable(i).getLinkedColumns().getObjectName(j);
                    cn = lcColumn.getColumnNameOnly();
                    //System.out.println(numberOfSpace(pNest+2)+cn.getColumnNameOnly());
                    if (showColumnLocation){
                        String  posStr = "";
//                        if ( lcColumn.getColumnToken() != null) {
//                            TSourceToken lcStartToken = lcColumn.getColumnToken();
//                            posStr ="("+ lcStartToken.lineNo+","+lcStartToken.columnNo+ ")";
//                        }
                        infos.append(numberOfSpace(pNest+3)+ lcColumn.getColumnNameOnly()+posStr+"("+lcColumn.getLocation()+")"+newline);
                    }else{
                        infos.append(numberOfSpace(pNest+3)+ lcColumn.getColumnNameOnly()+newline);
                    }

                    if (!((lcTable.getTableType() == ETableSource.subquery)||(lcTable.isCTEName()))){
                         if ((listStarColumn) || (!(lcColumn.getColumnNameOnly().equals("*")))){
                             if (lcTable.isLinkTable()){
                                 fieldlist.add(lcTable.getLinkTable().getTableName() + "." + cn );
                             }else{
                                 fieldlist.add(tn + "." + cn );
                             }
                         }
                    }
                    tableColumnList.append(","+tn+"."+ cn);
                }
            //}
        }

        if (stmt.getOrphanColumns().size() > 0){
            infos.append(numberOfSpace(pNest+1)+" orphan columns:"+newline);
            String oc = "";
            for (int k=0;k<stmt.getOrphanColumns().size();k++){
                oc = stmt.getOrphanColumns().getObjectName(k).toString();
                if (showColumnLocation){
                    infos.append(numberOfSpace(pNest+3)+oc+"("+stmt.getOrphanColumns().getObjectName(k).getLocation()+")"+newline);
                }else{
                    infos.append(numberOfSpace(pNest+3)+oc+newline);
                }

                if ((linkOrphanColumnToFirstTable)&&(stmt.getFirstPhysicalTable() != null)){
                    if ((listStarColumn) ||(!(oc.equalsIgnoreCase("*"))))
                    fieldlist.add(stmt.getFirstPhysicalTable().toString() + "." + oc );
                }else {
                    fieldlist.add("missed." + oc );
                }
                tableColumnList.append(",missed."+oc+newline);
            }
        }

        for (int i=0;i<stmt.getStatements().size();i++){
            analyzeStmt(stmt.getStatements().get(i),pNest+1);
        }
    }
}

 class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }


