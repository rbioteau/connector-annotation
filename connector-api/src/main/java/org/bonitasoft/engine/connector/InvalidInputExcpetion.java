package org.bonitasoft.engine.connector;

public class InvalidInputExcpetion extends Exception {

	public InvalidInputExcpetion(String message) {
		super(message);
	}
	
	public InvalidInputExcpetion(String message,Throwable cause) {
		super(message,cause);
	}
	
}
