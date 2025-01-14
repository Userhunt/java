package net.w3e.wlib.dungeon.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;

public interface ILayerDeserializerAdapter<D, L> extends JsonDeserializer<L>, IDungeonJsonAdapter {
	default D deserialize(D data, JsonDeserializationContext context) {
		return data;
	}
}
