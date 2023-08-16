package org.babinkuk;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTest {
	
	public static final Logger log = LogManager.getLogger(ApplicationTest.class);
	
	@Value("${info.app.name}")
	String appName;
	
	@Value("${spring.profiles.active}")
	String profiles;

	@Value("${info.app.version}")
	String version;

	@Value("${info.app.build-timestamp}")
	String buildTime;
	
	@Value("${info.app.author}")
	String author;
	
	@Value("${test.message}")
	String customMessage;
	
	@Test
	void basicTest() {
		log.info(MessageFormat.format(
				"\n--------------------------------------------------------------------" + 
				"\nESB microservice {0} is running!" +
				"\nProfile(s): {1}" +
				"\nVersion: {2}" +
				"\nAuthor: {3}" +
				"\nBuilt on: {4}" +
				"\nsomething extra: {5}" +
				"\n--------------------------------------------------------------------",
				appName, profiles, version, author, buildTime, customMessage));
		
	}
}
