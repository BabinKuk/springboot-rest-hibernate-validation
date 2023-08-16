package org.babinkuk.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagePool {
	
	private static MessageSource messages;
	
	@Autowired
	public MessagePool(MessageSource messageSource) {
		messages = messages == null ? messageSource : messages;
	}
	
	public static String getMessage(final String key, final Object...arguments) {
		return messages.getMessage(key, arguments, Locale.getDefault());
	}
}
