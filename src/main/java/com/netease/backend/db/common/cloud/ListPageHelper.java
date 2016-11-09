package com.netease.backend.db.common.cloud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.netease.backend.db.common.cloud.Definition.SortType;
import com.netease.backend.db.common.schema.cloud.ListPage;


public class ListPageHelper {

	
	public static final <T> List<T> getPageListAfterSort(List<T> totalList,
			Comparator<T> comparator, SortType sortType, int pageNum,
			int pageSize, boolean oneMore) {
		
		if (totalList.size() <= (pageNum - 1) * pageSize) {
			return Collections.<T> emptyList();
		}

		
		if (sortType == SortType.DESC)
			comparator = Collections.reverseOrder(comparator);
		Collections.sort(totalList, comparator);

		
		int index = (pageNum - 1) * pageSize;
		int endIndex = pageNum * pageSize;
		if (oneMore)
			++endIndex;

		List<T> pageList = new ArrayList<T>(pageSize);
		for (; (index < endIndex) && (index < totalList.size()); index++) {
			pageList.add(totalList.get(index));
		}
		return pageList;
	}

	
	@SuppressWarnings("unchecked")
	public static final <T> List<T> getPageListAfterSortByMarker(
			List<T> totalList, Comparator<T> comparator, T marker,
			int pageSize, boolean oneMore) {
		
		if (totalList.size() == 0)
			return Collections.<T> emptyList();
		
		
		Collections.sort(totalList, comparator);
		
		
		int index = 0;
		if (marker != null) {
			
			index = Arrays.binarySearch(((T[]) totalList.toArray()), marker,
					comparator);
			if (index < 0)
				throw new IllegalArgumentException(
						"marker can not found in totalList");
		}

		List<T> returnList = new LinkedList<T>();
		int endIndex = index + pageSize;
		if (oneMore)
			endIndex++;
		for (; (index < endIndex) && (index < totalList.size()); index++) {
			returnList.add(totalList.get(index));
		}
		return returnList;
	}
	
	
	public static final <T, V extends Serializable & Marker> ListPage<V> getListPageFromListWithOneMore(
			List<T> listWithOneMore, int pageSize, Converter<T, V> converter) {
		List<V> list = new ArrayList<V>(pageSize);
		
		for (int i = 0; i < pageSize && i < listWithOneMore.size(); i++) {
			list.add(converter.convertTo(listWithOneMore.get(i)));
		}

		String nextMarker = null;
		if (listWithOneMore.size() > pageSize) {
			V nextV = converter.convertTo(listWithOneMore.get(pageSize));
			nextMarker = nextV.getMarker();
		}

		ListPage<V> page = new ListPage<V>(list, nextMarker);
		return page;
	}
}
