package org.bonitasoft.engine.connector;

import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

public class DisconnectMethodCallback implements MethodCallback {

	private Object bean;

	public DisconnectMethodCallback(Object bean) {
		this.bean = bean;
	}

	@Override
	public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
		if (!method.isAnnotationPresent(Disconnect.class)) {
			return;
		}
		ReflectionUtils.invokeMethod(method, bean);
	}

}
