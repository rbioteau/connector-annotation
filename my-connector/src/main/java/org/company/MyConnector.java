package org.company;

import java.util.List;
import java.util.Optional;

import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.connector.Connect;
import org.bonitasoft.engine.connector.Connector;
import org.bonitasoft.engine.connector.Disconnect;
import org.bonitasoft.engine.connector.EngineExecutionContext;
import org.bonitasoft.engine.connector.Execute;
import org.bonitasoft.engine.connector.Input;
import org.bonitasoft.engine.connector.Output;
import org.bonitasoft.engine.connector.ui.Category;
import org.bonitasoft.engine.connector.ui.Page;
import org.bonitasoft.engine.connector.ui.Text;

@Category("ERP")
@Connector(id = "my-connector", version = "0.0.1",icon ="index.png")
public class MyConnector {

	@Input
	@Text
	@Page("connectionPage")
	private String host = "localhost";
	
	@Input(validateWith = PortValidator.class)
	@Text
	@Page("connectionPage")
	private int port = 8080;
	
	@Input
	@Page("argsPage")
	@org.bonitasoft.engine.connector.ui.List
	private Optional<List<String>> args;
	
	@Output
	private int status;
	
	@Connect
	public void connect() {
		System.out.println(String.format("Logging to system %s:%s",host,port));
	}
	
	@Execute
	public void doBusinessLogic(ProcessAPI processAPI,EngineExecutionContext context) {
		System.out.println("do some stuff using injected API :" + processAPI);
		System.out.println("do some stuff using EngineExecutionContext :" + context);
		System.out.println("Set status output value");
		System.out.println("Optional args input is:" + args);
		status = 200;
	}
	
	@Disconnect
	public void disconnect() {
		System.out.println("disconnect from system");
	}
	
}
