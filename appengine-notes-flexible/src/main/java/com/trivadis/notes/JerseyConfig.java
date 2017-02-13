package com.trivadis.notes;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.trivadis.notes.rest.GoogleResource;
import com.trivadis.notes.rest.NotesResource;
import com.trivadis.notes.rest.PrimesResource;

@Component
@ApplicationPath("api")
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(GoogleResource.class);
		register(NotesResource.class);
		register(PrimesResource.class);
	}
}
