
package com.netease.matrix;

public class SparseMatrixException extends Exception 
{

	
	private static final long serialVersionUID = 1L;

	
    public SparseMatrixException(String message) 
    {
        super(message); 
    }

    
     public  SparseMatrixException(String message, Throwable cause) 
     {
         super(message, cause);
     }
 }
