package com.trivadis.notes.service;

import java.util.List;

import com.googlecode.objectify.ObjectifyService;
import com.trivadis.notes.model.Note;

public class NotesObjectifyService implements NotesService {

	public List<Note> listNotes() {
		return ObjectifyService.ofy()
        .load()
        .type(Note.class) 
        .list();
	}

	public Note saveNote(Note note) {
		ObjectifyService.ofy() //
		.save().entity(note); //
		
		return note;
	}

}
