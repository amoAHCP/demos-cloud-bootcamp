package com.trivadis.notes.service;

import java.util.LinkedList;
import java.util.List;

import com.trivadis.notes.model.Note;

public class NotesDummyService implements NotesService {

	private static List<Note> notes;

	static {
		notes = new LinkedList<>();

		notes.add(new Note("Hello world!"));
		notes.add(new Note("The quick brown fox jumps over the lazy dog."));
		notes.add(new Note(
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam ..."));
		notes.add(new Note("Java is cool!"));

	}

	public List<Note> listNotes() {
		return new LinkedList<>(notes);
	}

	public Note saveNote(Note note) {
		Note result = new Note(note.getText());
		result.setLatitude(note.getLatitude());
		result.setLongitude(note.getLongitude());

		notes.add(0, result);
		return result;
	}

}
