package com.netease.backend.db.common.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.netease.backend.db.common.utils.JavaTypeConvertor;


public abstract class PropertiesConfigHelperBase<T extends PropertiesConfig>
		implements PropertiesConfigHelper<T> {

	
	private static final String DEFAULT_CFG_FILE_ENC = "iso8859-1";

	
	private static final ConcurrentHashMap<Class<?>, LinkedHashSet<Field>> cache;

	static {
		cache = new ConcurrentHashMap<Class<?>, LinkedHashSet<Field>>();
	}

	public String toPropertiesString(T config) {
		return toPropertiesString(this.toProperties(config),
				config.getDescription());
	}

	public Properties toProperties(T config) {
		final Properties props = new Properties(); 
		for (final Field field : getDeclaredFields(config.getClass())) {
			if (!Modifier.isStatic(field.getModifiers())) {
				final boolean accessable;
				if (!(accessable = field.isAccessible())) {
					field.setAccessible(true);
				}
				try {
					final Object value;
					if ((value = field.get(config)) != null) {
						if (!JavaTypeConvertor.isTypeSupported(field.getType())) {
							
							if (value instanceof PropertiesConfig) {
								props.putAll(((PropertiesConfig) value)
										.toProperties());
							}
						} else {
							props.setProperty(field.getName(), value.toString());
						}
					}
				} catch (final IllegalAccessException ex ) {
					ex.printStackTrace();
				} finally {
					if (!accessable) {
						field.setAccessible(false);
					}
				}
			}
		}
		return props;
	}

	
	public void updateConfig(T config, Properties props) {
		for (final Field field : getDeclaredFields(config.getClass())) {
			
			if (!Modifier.isStatic(field.getModifiers())
					&& !Modifier.isFinal(field.getModifiers())) {
				final boolean accessable;
				if (!(accessable = field.isAccessible())) {
					field.setAccessible(true);
				}
				try {
					if (!JavaTypeConvertor.isTypeSupported(field.getType())) {
						
						
						final PropertiesConfig value;
						if (PropertiesConfig.class.isAssignableFrom(field
								.getType())
								&& ((value = ((PropertiesConfig) field
										.get(config))) != null)) {
							value.update(props);
							field.set(config, value);
						}
					} else {
						final String value;
						if ((value = getPropertyIgnoreCase(props,
								field.getName())) != null) {
							
							field.set(
									config,
									JavaTypeConvertor.convertType(
											field.getType(), value));
						}
					}
					
				} catch (final IllegalAccessException ex ) {
					ex.printStackTrace();
				} finally {
					if (!accessable) {
						field.setAccessible(false);
					}
				}
			}
		}
	}

	public void updateConfig(T config, T from) {
		for (final Field field : getDeclaredFields(config.getClass())) {
			
			if (!Modifier.isStatic(field.getModifiers())
					&& !Modifier.isFinal(field.getModifiers())) {
				final boolean accessable;
				if (!(accessable = field.isAccessible())) {
					field.setAccessible(true);
				}
				try {
					
					from.getClass().getDeclaredField(field.getName());
					
					final Object value = field.get(from);
					field.set(config, value);
				} catch (final IllegalArgumentException ex ) {
					ex.printStackTrace();
				} catch (final NoSuchFieldException ex ) {
				} catch (final IllegalAccessException ex) {
					ex.printStackTrace();
				} finally {
					if (!accessable) {
						field.setAccessible(false);
					}
				}
			}
		}
	}

	
	private static String getPropertyIgnoreCase(Properties props, String name) {
		String value = props.getProperty(name);
		if (value == null) {
			value = props.getProperty(name.toLowerCase());
		}
		return value;
	}

	
	public static String toPropertiesString(Properties cfgProps, String header) {
		final ByteArrayOutputStream bOut = new ByteArrayOutputStream(1024);
		try {
			cfgProps.store(bOut, header);
		} catch (final IOException ex ) {
		} catch (final ClassCastException ex) {
			ex.printStackTrace();
		}
		try {
			return bOut.toString(DEFAULT_CFG_FILE_ENC).trim();
		} catch (final UnsupportedEncodingException ex ) {
			throw new RuntimeException(ex);
		}
	}

	private static LinkedHashSet<Field> getDeclaredFields(Class<?> javaClass) {
		final LinkedHashSet<Field> fields;
		if (cache.contains(javaClass)) {
			fields = cache.get(javaClass);
		} else {
			fields = new LinkedHashSet<Field>(Arrays.asList(javaClass
					.getDeclaredFields()));
			Class<?> superClass = javaClass.getSuperclass();
			while (superClass != Object.class) {
				List<Field> fromSuper;
				if (((fromSuper = extractFields(superClass)) != null)
						&& (fromSuper.size() > 0)) {
					fields.addAll(fromSuper);
				}
				superClass = superClass.getSuperclass();
			}
			cache.put(javaClass, fields);
		}
		return fields;
	}

	private static List<Field> extractFields(Class<?> classType) {
		List<Field> fields = null;

		final Field[] fieldArray;
		if (((fieldArray = classType.getDeclaredFields()) != null)
				&& (fieldArray.length > 0)) {
			fields = Arrays.asList(fieldArray);
		}

		return fields;
	}

}
