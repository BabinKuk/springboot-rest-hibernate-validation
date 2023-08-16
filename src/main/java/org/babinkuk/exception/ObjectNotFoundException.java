package org.babinkuk.exception;

import java.util.ArrayList;
import java.util.List;

public class ObjectNotFoundException extends ObjectException {

	private static final long serialVersionUID = 1L;
	
	private List<String> validationErorrs;

	public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Throwable cause) {
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
    
    public ObjectNotFoundException addValidationError(String error) {
    	getValidationErrors().add(error);
    	return this;
    }
}
