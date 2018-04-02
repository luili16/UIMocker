package com.llx278.uimocker2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflect {
	private Object object;

	public Reflect(Object object) {
		if (object == null)
			throw new IllegalArgumentException("Object can not be null.");
		this.object = object;
	}

	public FieldRf field(String fieldName) {
		return new FieldRf(object, fieldName);
	}

	MethodRf method(String methodName,Class<?> ... paramTypes) {
		return new MethodRf(object,methodName,paramTypes);
	}

	public class FieldRf {
		private Object mObject;
		private String mName;

		FieldRf(Object object, String name) {
			mObject = object;
			mName = name;
		}
		
		public Object out() throws Exception {
			Field field = ReflectUtil.findFieldRecursiveImpl(mObject.getClass(), mName);
			field.setAccessible(true);
			return getValue(field);
		}
		
		public void in(Object value) throws Exception {
			Field field = null;
			field = ReflectUtil.findFieldRecursiveImpl(mObject.getClass(), mName);
			field.set(mObject, value);
		}

		private Object getValue(Field field) throws Exception {
			if (field == null) {
				return null;
			}
			return field.get(mObject);
		}
	}

	class MethodRf {
		private Object mObject;
		private String mMethodName;
		private Class<?>[] mParamTypes;
		MethodRf(Object object, String methodName, Class<?>... paramTypes) {
			mObject = object;
			mMethodName = methodName;
			mParamTypes = paramTypes;
		}

		Object invoke(Object... args) throws Exception {
			Method method;
			method = mObject.getClass().getMethod(mMethodName,mParamTypes);
			return method.invoke(mObject,args);
		}
	}

}
