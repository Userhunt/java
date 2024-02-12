package net.home.tf2;

import java.lang.reflect.Type;
import java.util.Arrays;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.api.GsonHelper;
import net.w3e.base.json.W3EJsonSerializer;

public record Tf2RegistryObject(String id, String link, String image, String[] group) implements Tf2IconImpl {

	private static final String[] GROUP = new String[]{"all"};

	public static class Tf2Deserializer extends W3EJsonSerializer<Tf2RegistryObject> {

		@Override
		public Tf2RegistryObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "item");

			String id = Tf2.JSON.readString(jsonObject, "id", null);
			if (id == null) {
				throw new UnsupportedOperationException("id is null " + jsonObject);
			}

			String[] group = Tf2.JSON.readArraySet(jsonObject, "group", new String[0], context, true).toArray(new String[0]);
			if (group.length == 0) {
				group = GROUP;
			}

			String link = Tf2.JSON.readString(jsonObject, "link", id, false);

			String image = Tf2.JSON.readString(jsonObject, "image", null, false);

			return new Tf2RegistryObject(id, link, image, group);
		}
	}

	@Override
	public final Tf2RegistryObject self() {
		return this;
	}

	@Override
	public final String more(float dollar) {
		return "";
	}

	@Override
	public final boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (object == null) {
			return false;
		} else if (!(object instanceof Tf2RegistryObject reg)) {
			return false;
		} else {
			return this.id.equals(reg.id) && this.link.equals(reg.link) && this.image.equals(reg.image) && Arrays.equals(this.group, reg.group);
		}
	}

	@Override
	public final int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public final String toString() {
		return String.format("{class:%s,hash:%s,id:\"%s\",link:\"%s\",image:%s}", this.getClass().getSimpleName(), this.hashCode(), this.id, this.link, this.image != null);
	}
}