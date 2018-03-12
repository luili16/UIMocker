package com.llx278.uimocker2;

import android.util.Log;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedBridge;

/**
 * A reflection utility class.  
 * 
 * @author Per-Erik Bergman, bergman@uncle.se
 * 
 */

public class Reflect {
	private Object object;

	/**
	 * Constructs this object 
	 * 
	 * @param object the object to reflect on
	 */
	
	public Reflect(Object object) {
		if (object == null)
			throw new IllegalArgumentException("Object can not be null.");
		this.object = object;
	}

	/**
	 * Get a field from the object 
	 * 
	 * @param name the name of the field
	 * 
	 * @return a field reference
	 */
	
	public FieldRf field(String name) {
		return new FieldRf(object, name);
	}

	/**
	 * A field reference.  
	 */
	public class FieldRf {
		private Class<?> clazz;
		private Object object;
		private String name;

		/**
		 * Constructs this object 
		 * 
		 * @param object the object to reflect on
		 * @param name the name of the field
		 */
		
		public FieldRf(Object object, String name) {
			this.object = object;
			this.name = name;
		}

		/**
		 * Constructs this object 
		 * 
		 * @param outclazz the output type
		 *
		 * @return <T> T
		 */
		
		public Object out() throws Exception {
			Field field = ReflectUtil.findFieldRecursiveImpl(object.getClass(),name);
			field.setAccessible(true);
			return getValue(field);
		}


		/**
		 * Set a value to a field 
		 * 
		 * @param value the value to set
		 */
		
		public void in(Object value) throws Exception {
			Field field = null;
			field = ReflectUtil.findFieldRecursiveImpl(object.getClass(),name);
			field.set(object, value);
		}

		private Object getValue(Field field) throws Exception {
			if (field == null) {
				return null;
			}

			return field.get(object);
		}


	}

}
