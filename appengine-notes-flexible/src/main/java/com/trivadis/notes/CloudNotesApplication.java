package com.trivadis.notes;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class CloudNotesApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CloudNotesApplication.class);
	}
	
	public static void main(String[] args) {
		new CloudNotesApplication()
		.configure(new SpringApplicationBuilder(CloudNotesApplication.class))
		.run(args);
	}

}
