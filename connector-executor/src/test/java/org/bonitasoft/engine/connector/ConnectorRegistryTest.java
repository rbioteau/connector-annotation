package org.bonitasoft.engine.connector;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Objects;

import org.junit.Test;

public class ConnectorRegistryTest {

	@Test
	public void should_find_connector_annotated_classes_in_classloader() throws Exception {
		ConnectorRegistry connectorRegistry = new ConnectorRegistry();
		
		Map<String, Class<?>> connectors = connectorRegistry.load();
		
		assertTrue(Objects.equals(connectors.get("test-connector-1.0.0"),TestConnector.class));
	}
	
}
