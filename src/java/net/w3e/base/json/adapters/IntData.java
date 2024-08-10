package net.w3e.base.json.adapters;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.w3e.base.json.BJsonUtil;

public class IntData {

	public final int min;
	public final int max;

	public IntData(int min, int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String toString() {
		return String.format("{min:%s,max:%s}", min, max);
	}

	public static class IntSerializer extends WJsonAdapter<IntData> {
		public IntData deserialize(JsonElement json, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
			try {
				int value = BJsonUtil.convertToInt(json, "<int>");
				return new IntData(value, value);
			} catch (Exception e) {
				JsonObject jsonobject = BJsonUtil.convertToJsonObject(json, "<int>");
				int min = jsonobject.has("min") ? BJsonUtil.getAsInt(jsonobject, "min") : 1;
				int max = jsonobject.has("max") ? BJsonUtil.getAsInt(jsonobject, "max") : min;
				return new IntData(min, max);
			}
		}
	}

}
