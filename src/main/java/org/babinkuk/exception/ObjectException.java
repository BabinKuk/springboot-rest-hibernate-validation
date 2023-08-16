package org.babinkuk.exception;

public class ObjectException extends ApplicationServiceException {

	private static final long serialVersionUID = 1L;

	public ObjectException(String message) {
        super(message);
    }

    public ObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectException(Throwable cause) {
        super(cause);
    }
}
