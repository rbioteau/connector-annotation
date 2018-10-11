package org.bonitasoft.engine.connector;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

public class ExecuteMethodCallback implements MethodCallback {

	private Object bean;
	private ConfigurableListableBeanFactory configurableBeanFactory;

	public ExecuteMethodCallback(ConfigurableListableBeanFactory configurableBeanFactory, Object bean) {
		this.configurableBeanFactory = configurableBeanFactory;
		this.bean = bean;
	}

	@Override
	public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
		if (!method.isAnnotationPresent(Execute.class)) {
			return;
		}
		List<Object> args = new ArrayList<>();
		for (Parameter param : method.getParameters()) {
			Class<?> type = param.getType();
			Object paramBean = configurableBeanFactory.getBean(type.getName());
			if(paramBean == null) {
				System.err.println(String.format("No bean found for %s",type));
			}
			args.add(paramBean);
		}
		ReflectionUtils.invokeMethod(method, bean, args.toArray());
	}


}
