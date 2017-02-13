package com.trivadis.notes.service;

import java.util.List;

import com.trivadis.notes.model.Note;

public interface NotesService {

	List<Note> listNotes();

	Note saveNote(Note note);

}
