package org.bonitasoft.engine.connector.ui;

import java.util.Locale;
import java.util.ResourceBundle;

import org.bonitasoft.engine.connector.Connector;

public class ConnectorUIAnnotationProcessor {
	
	private Class<?> connectorClass;

	public ConnectorUIAnnotationProcessor(Class<?> connectorClass) {
		this.connectorClass = connectorClass;
	}

	public ResourceBundle getResourceBundle() {
		return getResourceBundle(Locale.getDefault());
	}
	
	public ResourceBundle getResourceBundle(Locale locale) {
		Connector annotation = connectorClass.getAnnotation(Connector.class);
		String messages = annotation.messages();
		if(!messages.startsWith("/")) {
			messages =  connectorClass.getPackage().getName().replace(".", "/")+"/"+messages ;
		}
		return ResourceBundle.getBundle(messages,locale);
	}
}
