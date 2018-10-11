package org.bonitasoft.engine.connector;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

@Component
public class ConnectorRegistry {

	public Map<String, Class<?>> load() {
		Map<String, Class<?>> connectorsRegistry = new HashMap<>();

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Connector.class));

		for (BeanDefinition bd : scanner.findCandidateComponents("")) {
			System.out.println(bd);
			String className = bd.getBeanClassName();
			Class<?> connectorClass;
			try {
				connectorClass = Class.forName(className);
				Connector connector = connectorClass.getAnnotation(Connector.class);
				connectorsRegistry.put(connector.id()+"-"+connector.version(), connectorClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		
		}

		return connectorsRegistry;
	}

}
