package com.netease.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;


public class FieldBySetReflectionProvider extends Sun14ReflectionProvider {

	@SuppressWarnings("unchecked")
	@Override
	public void writeField(Object object, String fieldName, Object value, Class definedIn) {
		String setName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		try {
			Field field = fieldDictionary.field(object.getClass(), fieldName, definedIn);
			Class fieldType = field.getType();
			if (definedIn == null)
				definedIn = field.getDeclaringClass();
			
			Method setMethod;
			if (fieldType.isPrimitive()) {
				 if (fieldType.equals(Integer.TYPE)) {
					 setMethod = definedIn.getDeclaredMethod(setName, new Class[]{int.class});
	            } else if (fieldType.equals(Long.TYPE)) {
	            	setMethod = definedIn.getDeclaredMethod(setName, new Class[]{long.class});
	            } else if (fieldType.equals(Short.TYPE)) {
	            	setMethod = definedIn.getDeclaredMethod(setName, new Class[]{short.class});
	            } else if (fieldType.equals(Character.TYPE)) {
	            	setMethod = definedIn.getDeclaredMethod(setName, new Class[]{char.class});
	            } else if (fieldType.equals(Byte.TYPE)) {
	            	setMethod = definedIn.getDeclaredMethod(setName, new Class[]{byte.class});
	            } else if (fieldType.equals(Float.TYPE)) {
	            	setMethod = definedIn.getDeclaredMethod(setName, new Class[]{float.class});
	            } else if (fieldType.equals(Double.TYPE)) {
	            	setMethod = definedIn.getDeclaredMethod(setName, new Class[]{double.class});
	            } else if (fieldType.equals(Boolean.TYPE)) {
	            	setMethod = definedIn.getDeclaredMethod(setName, new Class[]{boolean.class});
	            } else {
	                throw new ObjectAccessException("Could not set field " +
	                        object.getClass() + "." + field.getName() +
	                        ": Unknown type " + fieldType);
	            }
			} else {
				setMethod = definedIn.getDeclaredMethod(setName, new Class[]{value.getClass()});
			}
			setMethod.setAccessible(true);
			setMethod.invoke(object, new Object[]{value});
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof IllegalArgumentException)
				throw (IllegalArgumentException)e.getCause();
			else 
				super.writeField(object, fieldName, value, definedIn);
		} catch (Exception e) {
			super.writeField(object, fieldName, value, definedIn);
		}
	}
}
