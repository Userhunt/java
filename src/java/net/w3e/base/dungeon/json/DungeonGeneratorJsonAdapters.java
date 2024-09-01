package net.w3e.base.dungeon.json;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.w3e.base.dungeon.DungeonGenerator.LayerFactory;
import net.w3e.base.dungeon.layers.ClearLayer;
import net.w3e.base.dungeon.layers.DistanceLayer;
import net.w3e.base.dungeon.layers.RotateLayer;
import net.w3e.base.dungeon.layers.RotateLayer.RotateLayerData;
import net.w3e.base.dungeon.layers.RoomLayer.RoomVariant;
import net.w3e.base.dungeon.layers.RoomLayer.RoomVariantData;
import net.w3e.base.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.base.dungeon.layers.path.PathRepeatLayer;
import net.w3e.base.dungeon.layers.path.WormLayer;
import net.w3e.base.dungeon.layers.path.PathRepeatLayer.PathRepeatLayerData;
import net.w3e.base.dungeon.layers.path.WormLayer.WormLayerData;
import net.w3e.base.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.base.dungeon.layers.terra.DifficultyLayer;
import net.w3e.base.dungeon.layers.terra.TemperatureLayer;
import net.w3e.base.dungeon.layers.terra.WetLayer;
import net.w3e.base.dungeon.layers.terra.CompositeTerraLayer.CompositeTerraLayerData;
import net.w3e.base.dungeon.layers.terra.DifficultyLayer.DifficultyLayerData;
import net.w3e.base.dungeon.layers.terra.TemperatureLayer.TemperatureLayerData;
import net.w3e.base.dungeon.layers.terra.WetLayer.WetLayerData;
import net.w3e.base.math.vector.WDirection;

public class DungeonGeneratorJsonAdapters {

	private static final DungeonLayerJsonAdapter LAYER_ADAPTER = new DungeonLayerJsonAdapter();

	static {
		LAYER_ADAPTER.register(WormLayer.TYPE, WormLayerData.class);
		LAYER_ADAPTER.register(PathRepeatLayer.TYPE, (json, context) -> PathRepeatLayerData.INSTANCE.deserialize(json, null, context));
		LAYER_ADAPTER.register(DistanceLayer.TYPE, DistanceLayer.class);
		LAYER_ADAPTER.register(CompositeTerraLayer.TYPE, CompositeTerraLayerData.class);
		LAYER_ADAPTER.register(DifficultyLayer.TYPE, DifficultyLayerData.class);
		LAYER_ADAPTER.register(TemperatureLayer.TYPE, TemperatureLayerData.class);
		LAYER_ADAPTER.register(WetLayer.TYPE, WetLayerData.class);
		// BIOME
		// ROOM
		// FEATURE
		LAYER_ADAPTER.register(ClearLayer.TYPE, ClearLayer.class);
		LAYER_ADAPTER.register(RotateLayer.TYPE, RotateLayerData.class);
	}

	public static final DungeonLayerJsonAdapter getLayerAdapter() {
		return LAYER_ADAPTER.copy(true);
	}

	public static final GsonBuilder modifyGson(GsonBuilder gson) {
		gson.registerTypeAdapter(LayerFactory.class, new JsonSerializer<LayerFactory>() {
			@Override
			public final JsonElement serialize(LayerFactory src, Type typeOfSrc, JsonSerializationContext context) {
				return context.serialize(src.create(null));
			}
		});
		gson.registerTypeAdapter(DungeonInfoCountHolder.class, new JsonSerializer<DungeonInfoCountHolder>() {
			@Override
			public final JsonElement serialize(DungeonInfoCountHolder src, Type typeOfSrc, JsonSerializationContext context) {
				if (src.get() != -1) {
					return new JsonPrimitive(src.get());
				} else {
					return null;
				}
			}
		});
		gson.registerTypeAdapter(DungeonInfoCountHolder.class, new JsonDeserializer<Integer>() {
			@Override
			public final Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				return context.deserialize(json, int.class);
			}
		});
		gson.registerTypeAdapter(RoomVariant.class, new JsonSerializer<RoomVariant<?>>() {
			@Override
			public final JsonElement serialize(RoomVariant<?> src, Type typeOfSrc, JsonSerializationContext context) {
				JsonObject json = new JsonObject();
				Set<Object2BooleanArrayMap<WDirection>> set = src.directionVariants();
				if (!set.isEmpty()) {
					Iterator<Object2BooleanArrayMap<WDirection>> iterator = set.iterator();
					Object2BooleanArrayMap<WDirection> last = null;
					while (iterator.hasNext()) {
						last = iterator.next();
					}
					json.add("connections", context.serialize(new RoomVariantData.Connections(last)));
				}
				json.add("layerRange", context.serialize(src.layerRange()));
				if (src.enterance()) {
					json.addProperty("enterance", true);
				}
				json.add("value", context.serialize(src.value()));
				JsonElement count = context.serialize(src.count());
				if (count != null) {
					json.add("count", count);
				}
				return json;
			}
		});
		return gson;
	}
}
