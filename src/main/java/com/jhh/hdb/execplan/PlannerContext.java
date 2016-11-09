package com.jhh.hdb.execplan;

import org.apache.hadoop.hive.ql.parse.ASTNode;



 public class PlannerContext {
	 public ASTNode   child;
	 public Phase1Ctx ctx_1;

    void setParseTreeAttr(ASTNode child, Phase1Ctx ctx_1) {
      this.child = child;
      this.ctx_1 = ctx_1;
    }

    void setCTASToken(ASTNode child) {
    }

    void setInsertToken(ASTNode ast, boolean isTmpFileDest) {
    }
  }