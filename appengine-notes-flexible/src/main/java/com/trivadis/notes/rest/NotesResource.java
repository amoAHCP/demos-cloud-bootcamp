package com.trivadis.notes.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.trivadis.notes.model.Note;
import com.trivadis.notes.service.NotesService;

@Path("notes")
@Produces(MediaType.APPLICATION_JSON)
public class NotesResource {
	
	@Inject
	NotesService service;

	@GET
	public Response listAllNotes() {
		
		List<Note> notes = service.listNotes();
		
		return Response.ok(notes.toArray(new Note[notes.size()]), MediaType.APPLICATION_JSON) //
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNote(Note note) {
		note = service.saveNote(note);
		
		return Response.ok(note, MediaType.APPLICATION_JSON) //
				.build();
	}

}
