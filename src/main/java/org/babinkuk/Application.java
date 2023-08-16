package org.babinkuk;

import java.text.MessageFormat;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;

/**
 * used to bootstrap and initialize application
 * 
 * @author BabinKuk
 *
 */
@SpringBootApplication
public class Application {
	
	public static final Logger log = LogManager.getLogger(Application.class);
	
	public static void main(String[] args) {
		
		//SpringApplication.run(Application.class, args);
		final ConfigurableApplicationContext context = new SpringApplicationBuilder()
				.main(Application.class).sources(Application.class)
				//.listeners(listeners)
				.run(args);
		logApplicationStartup(context);
	}

	private static void logApplicationStartup(EnvironmentCapable environmentCapable) {
		// TODO Auto-generated method stub
		if (log.isInfoEnabled() && environmentCapable != null) {
			Environment env = environmentCapable.getEnvironment();
			
			String appName = env.getProperty("info.app.name");
			String profiles = env.getProperty("spring.profiles.active");
			String version = env.getProperty("info.app.version");
			String buildTime = env.getProperty("info.app.build-timestamp");
			String author = env.getProperty("info.app.author");
			
			log.info(MessageFormat.format(
					"\n--------------------------------------------------------------------" + 
					"\nESB microservice {0} is running!" +
					"\nProfile(s): {1}" +
					"\nVersion: {2}" +
					"\nAuthor: {3}" +
					"\nBuild on: {4}" +
					"\n--------------------------------------------------------------------",
					appName, profiles, version, author, buildTime));
		}
	}
}