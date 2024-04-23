package net.home.notes;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.W3EJsonSerializer;

public abstract class Note {

	private static final Gson GSON = BJsonUtil.GSON()
		.registerTypeAdapter(Note.class, new Note.Serializer())
		.registerTypeAdapter(NoteFolder.class, new NoteFolder.Serializer())
		.registerTypeAdapter(NoteFile.class, new NoteFile.Serializer())
	.create();
	
	protected static final String HTML = "<html><body>%s</html></body>";
	
	private String key;
	protected NoteFolder parent;

	public final String key() {
		return this.key;
	}

	public static Note load(String key, JsonElement value) {
		Note note = BJsonUtil.load(GSON, value, Note.class);
		note.key = key;
		return note;
	}

	public final JsonObject save() {
		JsonObject json = new JsonObject();
		if (this instanceof NoteFolder) {
			json.addProperty("type", "folder");
		}
		if (this instanceof NoteFile) {
			json.addProperty("type", "file");
		}
		save(json);
		return json;
	}

	protected abstract void save(JsonObject json);

	private static class Serializer extends W3EJsonSerializer<Note> {

		@Override
		public Note deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			String type = jsonObject.get("type").getAsString();
			if (type.equals("folder")) {
				return BJsonUtil.load(GSON, jsonObject, NoteFolder.class);
			}
			if (type.equals("file")) {
				return BJsonUtil.load(GSON, jsonObject, NoteFile.class);
			}
			throw new IllegalStateException("unknown type " + type);
		}
	}
}
