package org.bonitasoft.engine.connector.ui;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.ResourceBundle;

import org.company.MyConnector;
import org.junit.Test;

public class ConnectorUIAnnotationProcessorTest {

	@Test
	public void should_load_message_bundle() throws Exception {
		ConnectorUIAnnotationProcessor processor = new ConnectorUIAnnotationProcessor(MyConnector.class);
	
		ResourceBundle resourceBundle = processor.getResourceBundle(Locale.getDefault());
		
		assertEquals(resourceBundle.getString("host.label"),"Hote");
	}
}
