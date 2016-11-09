
package com.netease.backend.db.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.netease.backend.db.common.schema.TableInfo;


public class SynUtils {
    private static RWLock rwLock = new RWLock();
    
    public static RWLock getSynLock() {
        return rwLock;
    }
    
	
	public static void getReadLock(Collection<TableInfo> tables)
	{
		ArrayList<TableInfo> list = new ArrayList<TableInfo>();
		for(TableInfo table: tables)
		{
			if(table.isView())
			{
				for(TableInfo baseTable : table.getBaseTables())
					if(!list.contains(baseTable))
						list.add(baseTable);
			}else if(!list.contains(table))
				list.add(table);
		}
		Collections.sort(list);
		for(TableInfo table: list)
				table.getReadLock();
	}
	
	
	public static void getWriteLock(Collection<TableInfo> tables)
	{
		ArrayList<TableInfo> list = new ArrayList<TableInfo>();
		for(TableInfo table: tables)
		{
			if(table.isView())
			{
				for(TableInfo baseTable : table.getBaseTables())
					if(!list.contains(baseTable))
						list.add(baseTable);
			}else if(!list.contains(table))
				list.add(table);
		}
		Collections.sort(list);
		for(TableInfo table: list)
				table.getWriteLock();
	}
	
	
	public static void getWriteLock(Collection<TableInfo> tables, long timeout)
	{
		ArrayList<TableInfo> list = new ArrayList<TableInfo>();
		for(TableInfo table: tables)
		{
			if(table.isView())
			{
				for(TableInfo baseTable : table.getBaseTables())
					if(!list.contains(baseTable))
						list.add(baseTable);
			}else if(!list.contains(table))
				list.add(table);
		}
		Collections.sort(list);
		for(TableInfo table: list)
				table.getWriteLock(timeout);
	}
	
	
	public static void releaseLock(Collection<TableInfo> tables)
	{
		ArrayList<TableInfo> list = new ArrayList<TableInfo>();
		for(TableInfo table: tables)
		{
			if(table.isView())
			{
				for(TableInfo baseTable : table.getBaseTables())
					if(!list.contains(baseTable))
						list.add(baseTable);
			}else if(!list.contains(table))
				list.add(table);
		}
		Collections.sort(list);
		for(TableInfo table: list)
				table.releaseLock();
	}
	
	
	public static void releaseLockWithTimeout(Collection<TableInfo> tables)
	{
		ArrayList<TableInfo> list = new ArrayList<TableInfo>();
		for(TableInfo table: tables)
		{
			if(table.isView())
			{
				for(TableInfo baseTable : table.getBaseTables())
					if(!list.contains(baseTable))
						list.add(baseTable);
			}else if(!list.contains(table))
				list.add(table);
		}
		Collections.sort(list);
		for(TableInfo table: list)
				table.releaseLockWithTimeout();
	}
	
	public static void main(String[] args)
	{
		LinkedList<TableInfo> al = new LinkedList<TableInfo>();
		TableInfo table1 = new TableInfo("a",null,null, null);
		TableInfo table2 = new TableInfo("b",null,null, null);
		TableInfo table3 = new TableInfo("c",null,null, null);
		al.add(table2);
		al.add(table1);
		al.add(table3);
		System.out.print("Original ArrayList: ");
		for(int i=0;i<al.size();i++)
			System.out.print(al.get(i).getName()+" ");
		System.out.println();
		ArrayList<TableInfo> al2 = new ArrayList<TableInfo>(al);
		Collections.sort(al2);
		System.out.print("Sorted ArrayList: ");
		for(int i=0;i<al.size();i++)
			System.out.print(al.get(i).getName()+" ");
		System.out.println();
		System.out.print("New Sorted ArrayList: ");
		for(int i=0;i<al2.size();i++)
			System.out.print(al2.get(i).getName()+" ");
		System.out.println();
	}
	
}
