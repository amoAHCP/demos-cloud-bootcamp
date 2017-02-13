package com.trivadis.notes.service;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Named;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import com.trivadis.notes.model.Note;

@Named
public class NotesDatastoreService implements NotesService {

	public List<Note> listNotes() {
		List<Note> result = new LinkedList<>();
		
		// http://googlecloudplatform.github.io/google-cloud-java/0.7.0/index.html
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		final Query<Entity> query = Query.newEntityQueryBuilder()//
				.setKind("Note").build();
		final QueryResults<Entity> queryResult = datastore.run(query);

		queryResult.forEachRemaining(new Consumer<Entity>() {
			@Override
			public void accept(Entity e) {
				Note n = new Note();
				n.setId(e.getKey().getId().toString());
				n.setText(e.getString("text"));
				n.setLatitude(e.getDouble("latitude"));
				n.setLongitude(e.getDouble("longitude"));
				result.add(n);
			}
		});

		return result;
	}

	public Note saveNote(Note note) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		KeyFactory keyFactory = datastore.newKeyFactory().setKind("Note");
		
		Key key = datastore.allocateId(keyFactory.newKey());
	    Entity e = Entity.newBuilder(key)
	        .set("text", note.getText())
	        .set("latitude", note.getLatitude())
	        .set("longitude", note.getLongitude())
	        .build();
	    datastore.put(e);
	    
	    note.setId(key.getId().toString());
	    
	    return note;
	}

}
