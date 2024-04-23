package net.home.notes;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.w3e.base.BStringUtil;
import net.w3e.base.holders.Object2Holder;
import net.w3e.base.json.W3EJsonSerializer;

public class NoteFolder extends Note {
	
	private final Map<String, Note> map;

	public NoteFolder() {
		this(new LinkedHashMap<>());
	}

	private NoteFolder(Map<String, Note> map) {
		this.map = map;
		for (Note note : this.map.values()) {
			note.parent = this;
		}
	}

	public final Map<String, Note> files() {
		return this.map;
	}

	@Override
	protected final void save(JsonObject json) {
		for (Entry<String, Note> entry : this.map.entrySet()) {
			json.add(entry.getKey(), entry.getValue().save());
		}
	}

	@Override
	public final String toString() {
		return String.format("{name:\"%s\",map:%s}", this.key(), BStringUtil.toString(this.map, entry -> new Object2Holder<>(entry.getKey(), entry.getValue())));
	}

	protected static class Serializer extends W3EJsonSerializer<NoteFolder> {
		@Override
		public final NoteFolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			Map<String, Note> map = new LinkedHashMap<>();
			for (Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("elements").entrySet()) {
				String key = entry.getKey();
				map.put(key, load(key, entry.getValue()));
			}
			return new NoteFolder(map);
		}
	}
}
