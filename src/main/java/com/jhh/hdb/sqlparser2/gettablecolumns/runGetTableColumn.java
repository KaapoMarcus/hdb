package com.jhh.hdb.sqlparser2.gettablecolumns;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.IMetaDatabase;

class sampleMetaDB implements IMetaDatabase {

    String columns[][] = {
            {"server","db","schema","promotion","promo_desc"},
            {"server","db","schema","sales","dollars"}
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

public class runGetTableColumn {
    public static void main(String args[])
    {
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvmssql);
        getTableColumn.listStarColumn = false;
        getTableColumn.showTreeStructure = true;
        getTableColumn.showTableEffect = true;
        getTableColumn.showColumnLocation = true;
        getTableColumn.linkOrphanColumnToFirstTable = true;
        //getTableColumn.setMetaDatabase(new sampleMetaDB());
//        getTableColumn.runText("SELECT a.deptno \"Department\", \n" +
//                "       a.num_emp/b.total_count \"Employees\", \n" +
//                "       a.sal_sum/b.total_sal \"Salary\"\n" +
//                "  FROM\n" +
//                "(SELECT deptno, COUNT(*) num_emp, SUM(SAL) sal_sum\n" +
//                "    FROM scott.emp\n" +
//                "    GROUP BY deptno) a,\n" +
//                "(SELECT COUNT(*) total_count, SUM(sal) total_sal\n" +
//                "    FROM scott.emp) b");

        getTableColumn.runFile("c:\\prg\\tmp\\demo.sql");
    }
}
