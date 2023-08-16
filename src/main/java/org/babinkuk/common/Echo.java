package org.babinkuk.common;

import java.time.Instant;

public class Echo {

	private static final String OK = "It's OK!";
	
	private String message;
	private Instant instant;
	
	public Echo() {
		this(OK);
	}

	public Echo(String message) {
		super();
		this.message = message;
		this.instant = Instant.now();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Instant getInstant() {
		return instant;
	}

	public void setInstant(Instant instant) {
		this.instant = instant;
	}

	
}
