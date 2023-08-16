package org.babinkuk.validator;

import java.util.Arrays;

public enum ValidatorRole {
	
	ROLE_STUDENT,
	ROLE_ADMIN,
	ROLE_INSTRUCTOR;
	
	public static ValidatorRole valueOfIgnoreCase(String str) {
		return Arrays.stream(ValidatorRole.values())
				.filter(e -> e.name().equalsIgnoreCase(str))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Cannot find enum constant for " + str));
	}
}
