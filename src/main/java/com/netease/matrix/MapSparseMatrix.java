package com.netease.matrix;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;







public class MapSparseMatrix extends MetadataSparseMatrix
{	
	
    
	private static final long serialVersionUID = 1144444478836474232L;

	
    private Map<MatrixCoordinate, MatrixElement> m_map = null;
	
	
	final int                              m_SPARSITY ;	
	
	
	private static final int        s_DEFAULT_SPARSITY = 10;
	
	
	public class MatrixCoordinate
	{
		int m_x = 0;
		int m_y = 0;
		MatrixCoordinate(int x, int y) throws SparseMatrixException
		{
			int size = m_size;
			if(x < 0 || x >= size || y < 0 || y >= size)
			{
				throw new SparseMatrixException("Invalid coordinate of matrix : "+ x +" and "+ y + 
						", both coordinates should be within the range: [0,"+(size-1)+"]."  );
			}
			m_x = Math.max(x,y);
			m_y = Math.min(x,y);			
		}
		public int hashCode()
		{						
			return computeHashCode(m_x,m_y);			
		}		
		public boolean  equals(Object object)
		{
			if(!(object instanceof MatrixCoordinate))
			{
				return false;
			}
			MatrixCoordinate anotherCoordinate = (MatrixCoordinate)object;
			return this.m_x ==anotherCoordinate.m_x && this.m_y == anotherCoordinate.m_y;
		}
		public int getx() {
			return m_x;
		}
		public int gety() {
			return m_y;
		}
		
		
	}
	
	
	protected int computeHashCode(int x, int y)
	{
		long xIndex = (long) x;
		long yIndex = (long) y;	
		long value = ((xIndex-1) * (xIndex )) / 2 + yIndex;
		return (int)(value ^ (value >>> 32));			
	}
	
	
	public class MatrixElement
	{
		private long m_Value = 0;
		
		
		MatrixElement(long value)
		{
			m_Value = value;
		}
		public synchronized void set(long value)
		{
			m_Value = value;
		}
		public synchronized void add(long addValue)
		{
			m_Value += addValue;			
		}
		public synchronized long get()
		{
			return m_Value;
		}
	}
	
	 
	
	public MapSparseMatrix(int size) throws SparseMatrixException
	{
		this(size, (size > s_DEFAULT_SPARSITY) ? s_DEFAULT_SPARSITY : size);
	}
	
	
	@SuppressWarnings("unchecked")
	public MapSparseMatrix(int size, int sparsity) throws SparseMatrixException
	{
		super(size);
		if(sparsity <= 0 || sparsity > size)
		{
			throw new SparseMatrixException("Invalid sparsity of matrix : "+sparsity +
				          	", it should be within the range: [1,"+size+"].");
		}
		m_SPARSITY     = sparsity;		
		m_map          = new ConcurrentHashMap(new HashMap( size * m_SPARSITY ));
		m_isLoaded     = false;		
		
	}
	
    public Iterator<Entry<MatrixCoordinate, MatrixElement>> iterator () {
    	return m_map.entrySet().iterator();
    }

    
	public synchronized long get(int x, int y) throws SparseMatrixException
	{
		
		MatrixCoordinate matrixCoordinate = new MatrixCoordinate(x,y);
		MatrixElement element = null;
		element = m_map.get(matrixCoordinate);
		if(element == null)
		{
			return 0;
		}
		else
		{
			return element.get();
		}
	}

	
	public synchronized void set(int x, int y, long value) throws SparseMatrixException
	{		
		
		MatrixCoordinate matrixCoordinate = new MatrixCoordinate(x,y);
		if(m_map.containsKey(matrixCoordinate))
		{
			(m_map.get(matrixCoordinate)).set(value);
		}
		else
		{
			m_map.put(matrixCoordinate,new MatrixElement(value));
		}			
	}

	
	public synchronized void add(int x, int y, long value) throws SparseMatrixException
	{
		MatrixCoordinate matrixCoordinate = new MatrixCoordinate(x,y);
		if(m_map.containsKey(matrixCoordinate))
		{
			(m_map.get(matrixCoordinate)).add(value);			
		}
		else
		{
			m_map.put(matrixCoordinate,new MatrixElement(value));
		}		
	}
	
	public int getNoneZeroElementSize() 
	{
		return m_map.size();
	}
}
