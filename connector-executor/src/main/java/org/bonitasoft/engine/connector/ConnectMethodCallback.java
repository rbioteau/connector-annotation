package org.bonitasoft.engine.connector;

import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

public class ConnectMethodCallback implements MethodCallback {

	private Object bean;

	public ConnectMethodCallback(Object bean) {
		this.bean = bean;
	}

	@Override
	public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
		if (!method.isAnnotationPresent(Connect.class)) {
			return;
		}
		ReflectionUtils.invokeMethod(method, bean);
	}

}
