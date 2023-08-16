package org.babinkuk.exception;

import java.util.ArrayList;
import java.util.List;

public class ObjectValidationException extends ObjectException {

	private static final long serialVersionUID = 1L;
	
	private List<String> validationErorrs;

	public ObjectValidationException(String message) {
        super(message);
    }

    public ObjectValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public boolean hasErrors()  {
    	return getValidationErrors().size() > 0;
    }
    
    public List<String> getValidationErrors() {
    	if (validationErorrs == null) {
			validationErorrs = new ArrayList<String>(0);
		}
    	return validationErorrs;
    }
    
    public ObjectValidationException addValidationError(String error) {
    	getValidationErrors().add(error);
    	return this;
    }
}
