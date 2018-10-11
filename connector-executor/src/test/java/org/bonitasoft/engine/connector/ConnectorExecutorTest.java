package org.bonitasoft.engine.connector;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ConnectorExecutorTest {

	@Test
	public void should_execute_connector() throws Exception {
		ConnectorExecutor connectorExecutor = new ConnectorExecutor(new ConnectorRegistry());
		Map<String, Object> inputs = new HashMap<>();
		inputs.put("host", "locahost");
		inputs.put("port", 1000);
		Map<String, Object> outputs = connectorExecutor.executeConnector("test-connector", "1.0.0", inputs);
		
		assertEquals(outputs.get("status"),Integer.valueOf(200));
	}
	
	@Test
	public void should_execute_connector_2() throws Exception {
		ConnectorExecutor connectorExecutor = new ConnectorExecutor(new ConnectorRegistry());
		Map<String, Object> inputs = new HashMap<>();
		inputs.put("host", "locahost");
		inputs.put("args", Arrays.asList("1","2"));
		Map<String, Object> outputs = connectorExecutor.executeConnector("my-connector", "0.0.1", inputs);
		
		assertEquals(outputs.get("status"),Integer.valueOf(200));
	}
}
