package com.netease.backend.db.common.cloud;


public interface Converter<T, V extends Marker> {
	V convertTo(T t);
}
