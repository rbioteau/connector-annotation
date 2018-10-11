package org.bonitasoft.engine.connector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class ConnectorInputCallback implements FieldCallback {

	private Object bean;

	private Map<String, Object> inputs;
	
	
	public final static Map<Class<?>, Class<?>> PRIMITIVE_TO_OBJECT = new HashMap<Class<?>, Class<?>>();
	static {
		PRIMITIVE_TO_OBJECT.put(boolean.class, Boolean.class);
		PRIMITIVE_TO_OBJECT.put(byte.class, Byte.class);
		PRIMITIVE_TO_OBJECT.put(short.class, Short.class);
	    PRIMITIVE_TO_OBJECT.put(char.class, Character.class);
	    PRIMITIVE_TO_OBJECT.put(int.class, Integer.class);
	    PRIMITIVE_TO_OBJECT.put(long.class, Long.class);
	    PRIMITIVE_TO_OBJECT.put(float.class, Float.class);
	    PRIMITIVE_TO_OBJECT.put(double.class, Double.class);
	}


	public ConnectorInputCallback(Object bean, Map<String, Object> inputs) {
		this.bean = bean;
		this.inputs = inputs;
	}

	@Override
	public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
		if (!field.isAnnotationPresent(Input.class)) {
			return;
		}
		ReflectionUtils.makeAccessible(field);
		Class<?> generic = field.getType();
		Class<?> validatorClass = field.getDeclaredAnnotation(Input.class).validateWith();
		String inputName = field.getName();
		Object object = inputs.get(inputName);
		Class<?> classValue = object != null ? object.getClass() : null;
		if(generic.isPrimitive()) {
			generic = PRIMITIVE_TO_OBJECT.get(generic);
		}
		try {
			if(Optional.class.getName().equals(generic.getName())) {
				if(classValue == null) {
					field.set(bean, Optional.empty());
				}else {
					if(isValidType(classValue, field.getGenericType())) {
						validateInput(validatorClass, object);
						field.set(bean, Optional.ofNullable(object));
					}else {
						throw new IllegalArgumentException(String.format("Type is consistent. Expected %s but found %s.",generic.getTypeName(),classValue.getName()));
					}
				}
			}else if (ReflectionUtils.getField(field, bean) == null &&  isValidType(classValue, generic)) {
				validateInput(validatorClass, object);
				field.set(bean, object);
			} else if (ReflectionUtils.getField(field, bean) == null){
				throw new IllegalArgumentException(String.format("Type is consistent. Expected %s but found %s.",generic.getTypeName(),classValue.getName()));
			}
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Type is consistent.");
		}
	}

	private void validateInput(Class<?> validatorClass, Object object) throws IllegalAccessException {
		try {
			Object validatorBean = validatorClass.newInstance();
			Method validateMethod = ReflectionUtils.findMethod(validatorClass, "validate",Object.class);
			validateMethod.invoke(validatorBean, object);
		} catch (InstantiationException | InvocationTargetException e) {
			String message = "Failed to instantiate input validator %s";
			if(e.getCause() instanceof InvalidInputExcpetion) {
				message = e.getCause().getMessage();
			}
			throw new IllegalArgumentException(
					String.format(message, validatorClass), e);
		}
	}

	public boolean isValidType(Class<?> clazz, Type field) throws ClassNotFoundException {
		if (field instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) field;
			Type type = parameterizedType.getActualTypeArguments()[0];
			if(type instanceof ParameterizedType) {
				type = ((ParameterizedType) type).getRawType();
			}
			return Class.forName(type.getTypeName()).isAssignableFrom(clazz);
		} else {
			return Class.forName(field.getTypeName()).isAssignableFrom(clazz);
		}
	}

}
