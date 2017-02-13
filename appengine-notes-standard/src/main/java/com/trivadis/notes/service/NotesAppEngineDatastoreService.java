package com.trivadis.notes.service;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Named;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.trivadis.notes.model.Note;

@Named
public class NotesAppEngineDatastoreService implements NotesService {

	public List<Note> listNotes() {
		final List<Note> result = new LinkedList<>();
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		
		for (Entity e : datastoreService.prepare( //
				new Query("Note")//
				).asIterable()) {
			Note n = new Note();
			n.setId(new Long(e.getKey().getId()).toString());
			n.setText((String) e.getProperty("text"));
			n.setLatitude((double) e.getProperty("latitude"));
			n.setLongitude((double) e.getProperty("longitude"));
			result.add(n);
		}

		return result;
	}

	public Note saveNote(Note note) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
	    Entity e = new Entity("Note");
	    e.setProperty("text", note.getText());
	    e.setProperty("latitude", note.getLatitude());
	    e.setProperty("longitude", note.getLongitude());

	    Key key = datastore.put(e);
	    
	    note.setId(new Long(key.getId()).toString());
	    
	    return note;
	}

}
