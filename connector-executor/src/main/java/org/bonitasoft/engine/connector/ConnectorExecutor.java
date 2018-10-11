package org.bonitasoft.engine.connector;

import java.util.Map;

import org.bonitasoft.engine.api.ProcessAPI;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ReflectionUtils;

public class ConnectorExecutor {

	private ConnectorRegistry registry;

	public ConnectorExecutor(ConnectorRegistry registry) {
		this.registry = registry;
	}

	public Map<String, Object> executeConnector(String id, String version, Map<String, Object> inputs)
			throws ConnectorNotFoundException, ConnectorExecutionException {
		Map<String, Class<?>> load = registry.load();
		String connectorId = connectorId(id, version);
		if (!load.containsKey(connectorId)) {
			throw new ConnectorNotFoundException(String.format("No connector found for %s (%s)", id, version));
		}
		Class<?> connector = load.get(connectorId);
		Object bean;
		try {
			bean = connector.newInstance();
			DefaultListableBeanFactory defaultListableBeanFactory = createBeanFactory();
			ReflectionUtils.doWithFields(connector, new ConnectorInputCallback(bean,inputs));
			ReflectionUtils.doWithMethods(connector, new ConnectMethodCallback(bean));
			ReflectionUtils.doWithMethods(connector, new ExecuteMethodCallback(defaultListableBeanFactory,bean));
			ReflectionUtils.doWithMethods(connector, new DisconnectMethodCallback(bean));
			ConnectorOutputCallback connectorOutputCallback = new ConnectorOutputCallback(bean);
			ReflectionUtils.doWithFields(connector, connectorOutputCallback);
			return connectorOutputCallback.getOutputs();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ConnectorExecutionException("Failed to execute connector",e);
		}
		
	}

	private DefaultListableBeanFactory createBeanFactory() {
		DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();
		defaultListableBeanFactory.registerSingleton(ProcessAPI.class.getName(), new MockProcessAPI());
		defaultListableBeanFactory.registerSingleton(EngineExecutionContext.class.getName(),new EngineExecutionContext());
		return defaultListableBeanFactory;
	}

	private String connectorId(String id, String version) {
		return id + "-" + version;
	}

}
