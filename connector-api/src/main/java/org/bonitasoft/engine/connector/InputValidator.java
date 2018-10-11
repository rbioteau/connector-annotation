package org.bonitasoft.engine.connector;

public interface InputValidator<T> {

	default void validate(T value) throws InvalidInputExcpetion {
		
	}
	
}
