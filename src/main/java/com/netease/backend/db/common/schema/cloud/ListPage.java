package com.netease.backend.db.common.schema.cloud;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


public class ListPage<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = -1567661923774412995L;

	
	private List<T> list;

	
	private boolean hasNextPage = false;

	
	private String marker = null;


	
	public ListPage(List<T> list, String marker) {
		this.list = list;
		this.marker = marker;
		this.hasNextPage = (marker != null);
		
		
		if (marker == null)
			this.marker = "";
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public boolean hasNextPage() {
		return hasNextPage;
	}

	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}
	
	public static final <T extends Serializable> ListPage<T> emptyPage() {
		return new ListPage<T>(Collections.<T> emptyList(), null);
	}
}
