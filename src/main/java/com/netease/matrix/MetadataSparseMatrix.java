package com.netease.matrix;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public abstract class MetadataSparseMatrix implements Serializable {
	
	
	protected int                         m_size = 0;
	 
	
	protected boolean                 m_isLoaded = false;
	
	
	final protected static int m_MAX_MATRIX_SIZE = 0x100000;
	
	public int getSize()
	{
		return m_size;
	}
	public void setSize(int size)
	{
		m_size = size;
	}

	
	protected MetadataSparseMatrix(int size) throws SparseMatrixException
	{
		if(size <= 0 || size > m_MAX_MATRIX_SIZE)
		{
			throw new SparseMatrixException("Invalid size of matrix : "+size+
					", it should be within the range: [1,"+m_MAX_MATRIX_SIZE+"]." );
		}
		m_size = size;
	}
		
	
	abstract public long get(int x, int y) throws SparseMatrixException;
	
	
	abstract public void set(int x, int y, long value) throws SparseMatrixException;
	
	
	public void add(int x, int y, long value) throws SparseMatrixException
	{
		set(x,y,(get(x,y) + value));
	}
		
	
	@SuppressWarnings("unchecked")
	public int[] getNZElementIndexes(int x) throws SparseMatrixException
	{
		List<Integer> list = new ArrayList();
		for(int i = 0; i < m_size; ++i)
		{
			if(i != x && get(x,i) != 0)
			{
				list.add(i);
			}			
		}
		int[] result = new int[list.size()];
		for(int i = 0; i < result.length; ++i)
		{
			result[i] = list.get(i);
		}		
		return result;
		
	}	
	
	
	
	public void load(String MetadataSparseMatrixFile) throws FileNotFoundException, IOException, ClassNotFoundException, SparseMatrixException
	{
		if(m_isLoaded)
		{
			throw new SparseMatrixException("A metadataSparseMatrix can be loaded only once.");
		}
		m_isLoaded = true;
		if(MetadataSparseMatrixFile == null)
		{
			return ;
		}
		DataInputStream in = new DataInputStream(
					         new BufferedInputStream(
                             new FileInputStream(MetadataSparseMatrixFile)));
		try
		{
			while(true)
			{
				int x     = in.readInt();
				int y     = in.readInt();
				long value = in.readLong();
				set(x,y,value);
			}
		}
		catch (EOFException exception)
		{
			
		} 
		in.close();
		
	}

	
	
	public void store(String MetadataSparseMatrixFile) throws FileNotFoundException, IOException, SparseMatrixException
	{
		DataOutputStream out = new DataOutputStream(
                               new BufferedOutputStream(
                               new FileOutputStream(MetadataSparseMatrixFile)));
		long size = getSize();
		for(int i = 0; i < size; ++i)
		{
			for(int j = 0; j < size; ++j)
			{
				long value = get(i, j); 
				if(value != 0)
				{
					out.writeInt(i);					
					out.writeInt(j);
					out.writeLong(value);
				}
			}
		}
		out.close();		
	}
}
