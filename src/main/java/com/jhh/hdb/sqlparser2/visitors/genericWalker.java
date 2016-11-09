package com.jhh.hdb.sqlparser2.visitors;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;


public class genericWalker {

    public static void main(String args[])
    {
        long t;
        t = System.currentTimeMillis();

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqlfilename  = args[0];

        int ret = sqlparser.parse();
        if (ret == 0){
            for(int i=0;i<sqlparser.sqlstatements.size();i++){
//                sqlparser.sqlstatements.get(i).accept(tv);
            }
        }else{
            System.out.println(sqlparser.getErrormessage());
        }

        System.out.println("Time Escaped: "+ (System.currentTimeMillis() - t) );
    }

}

