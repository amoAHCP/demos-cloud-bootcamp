package com.trivadis.notes.jaxrs;

import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import com.trivadis.notes.rest.GoogleResource;
import com.trivadis.notes.rest.NotesResource;
import com.trivadis.notes.rest.PrimesResource;
import com.trivadis.notes.service.NotesAppEngineDatastoreService;
import com.trivadis.notes.service.NotesDummyService;
import com.trivadis.notes.service.NotesObjectifyService;
import com.trivadis.notes.service.NotesService;

@ApplicationPath("/")
public class NotesApplication extends ResourceConfig {
	
	public NotesApplication() {
		register(NotesResource.class);
		register(PrimesResource.class);
		register(GoogleResource.class);
		
		register(new AbstractBinder() {
            @Override
            protected void configure() {
            	bind(NotesAppEngineDatastoreService.class).to(NotesService.class);
            }
        });
	}
	
}
