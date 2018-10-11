package org.bonitasoft.engine.connector;

@Connector(id = "test-connector", version = "1.0.0")
public class TestConnector {

	@Input
	private String host;
	
	@Input(validateWith = PortValidator.class)
	private int port;
	
	@Output
	private int status;
	
	@Execute
	public void executeMe() throws Exception {
		System.out.println(host);
		System.out.println(port);
		status = 200;
	}
	
}
