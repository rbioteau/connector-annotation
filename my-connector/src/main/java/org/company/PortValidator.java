package org.company;

import org.bonitasoft.engine.connector.InputValidator;
import org.bonitasoft.engine.connector.InvalidInputExcpetion;

public class PortValidator implements InputValidator<Integer> {

	public void validate(Integer value) throws InvalidInputExcpetion {
		if( value != null) {
			if(value < 1023 || value > 65535 ) {
				throw new InvalidInputExcpetion(String.format("Port value should be in port rang [1023..65535], but is %s",value));
			}
		}
	}
}
