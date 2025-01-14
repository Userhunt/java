package net.w3e.wlib.dungeon.json.sasai;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecFactory;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonDeserializeBuilder;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.io.json.codec.ReflectiveJsonCodecFactory.ReflectiveSerializer;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.SerializeOnlyJsonCodec;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapter;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapter.DungeonJsonAdaptersMap;
import net.w3e.wlib.dungeon.json.RoomLayerJsonAdaptersMap;
import net.w3e.wlib.dungeon.layers.ClearLayer;
import net.w3e.wlib.dungeon.layers.DistanceLayer;
import net.w3e.wlib.dungeon.layers.FeatureLayer;
import net.w3e.wlib.dungeon.layers.RoomLayer;
import net.w3e.wlib.dungeon.layers.RotateLayer;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilter;
import net.w3e.wlib.dungeon.layers.filter.types.*;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.layers.path.PathRepeatLayer;
import net.w3e.wlib.dungeon.layers.path.WormLayer;
import net.w3e.wlib.dungeon.layers.terra.BiomeLayer;
import net.w3e.wlib.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.wlib.dungeon.layers.terra.DifficultyLayer;
import net.w3e.wlib.dungeon.layers.terra.TemperatureLayer;
import net.w3e.wlib.dungeon.layers.terra.WetLayer;

public class DungeonJsonAdapters {
	public static final DungeonJsonAdapters INSTANCE = new DungeonJsonAdapters();

	public final DungeonJsonAdaptersMap layerAdapters = new DungeonJsonAdaptersMap(DungeonLayer.class);
	public final RoomLayerJsonAdaptersMap roomFilter = new RoomLayerJsonAdaptersMap();

	private DungeonJsonAdapters() {}

	static {
		INSTANCE.registerDefault();
	}

	@SuppressWarnings("rawtypes")
	private void registerDefault() {
		/*
		JsonUtils.addAdapter(RoomVariant.class, new JsonSerializer<RoomVariant<?>>() {
			@Override
			public final JsonElement serialize(RoomVariant<?> src, Type typeOfSrc, JsonSerializationContext context) {
				JsonObject json = new JsonObject();
				Set<Object2BooleanArrayMap<Direction>> set = src.directionVariants();
				if (!set.isEmpty()) {
					Iterator<Object2BooleanArrayMap<Direction>> iterator = set.iterator();
					Object2BooleanArrayMap<Direction> last = null;
					while (iterator.hasNext()) {
						last = iterator.next();
					}
					json.add("connections", context.serialize(new RoomLayer.ConnectionsData(last)));
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
		});*/

		this.roomFilter.registerConfigType(new DungeonJsonAdapter<>(TemperatureLayer.KEY, TempRoomFilter.class));
		this.roomFilter.registerConfigType(new DungeonJsonAdapter<>(WetLayer.KEY, WetRoomFilter.class));
		this.roomFilter.registerConfigType(new DungeonJsonAdapter<>(DifficultyLayer.KEY, DifficultyRoomFilter.class));
		this.roomFilter.registerConfigType(new DungeonJsonAdapter<>(DistanceLayer.KEY, DistanceRoomFilter.class));
		this.roomFilter.registerConfigType(new DungeonJsonAdapter<>(RandomRangeRoomFilter.KEY, RandomRangeRoomFilter.class));

		this.registerLayerAdapter(new DungeonJsonAdapter<>(PathRepeatLayer.TYPE, PathRepeatLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(WormLayer.TYPE, WormLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(DistanceLayer.TYPE, DistanceLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(CompositeTerraLayer.TYPE, CompositeTerraLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(DifficultyLayer.TYPE, DifficultyLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(TemperatureLayer.TYPE, TemperatureLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(WetLayer.TYPE, WetLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(ClearLayer.TYPE, ClearLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(RotateLayer.TYPE, RotateLayer.class));

		this.registerLayerAdapter(new DungeonJsonAdapter<>(BiomeLayer.TYPE, BiomeLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(RoomLayer.TYPE, RoomLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(FeatureLayer.TYPE, FeatureLayer.class));
	}

	public final void registerLayerAdapter(DungeonJsonAdapter<?> configType) {
		this.layerAdapters.registerConfigType(configType);
	}

	public final void register() {}
}
