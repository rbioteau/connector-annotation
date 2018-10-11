package org.bonitasoft.engine.connector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class ConnectorOutputCallback implements FieldCallback {

	private Object bean;

	private Map<String, Object> outputs = new HashMap<>();

	public ConnectorOutputCallback(Object bean) {
		this.bean = bean;
	}

	public Map<String, Object> getOutputs() {
		return outputs;
	}
	
	@Override
	public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
		if (!field.isAnnotationPresent(Output.class)) {
			return;
		}
		ReflectionUtils.makeAccessible(field);
		String outputName = field.getName();
		outputs.put(outputName, ReflectionUtils.getField(field, bean));
	}

}
