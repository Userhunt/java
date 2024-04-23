package net.home.notes;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.w3e.base.json.W3EJsonSerializer;

public class NoteFile extends Note {

	private final String text;
	private final String link;

	private NoteFile(String text, String link) {
		this.text = text;
		this.link = link;
	}

	@Override
	protected final void save(JsonObject json) {
		json.addProperty("text", this.text);
		json.addProperty("link", this.link);
	}

	@Override
	public final String toString() {
		return String.format("{name:\"%s\",text:\"%s\",link:\"%s\"}", this.key(), this.text, this.link);
	}

	protected static class Serializer extends W3EJsonSerializer<NoteFile> {

		@Override
		public NoteFile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();

			String text = jsonObject.get("text").getAsString();
			String link = jsonObject.get("link").getAsString();

			return new NoteFile(text, link);
		}
	}
}
