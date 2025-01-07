package net.w3e.wlib.dungeon.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.utils.json.ConfigType;
import net.skds.lib2.utils.json.JsonAdapter;
import net.skds.lib2.utils.json.JsonUtils;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.layers.ClearLayer;
import net.w3e.wlib.dungeon.layers.DistanceLayer;
import net.w3e.wlib.dungeon.layers.DungeonLayerFactory;
import net.w3e.wlib.dungeon.layers.RoomLayer;
import net.w3e.wlib.dungeon.layers.RotateLayer;
import net.w3e.wlib.dungeon.layers.RoomLayer.RoomVariant;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.layers.path.PathRepeatLayer;
import net.w3e.wlib.dungeon.layers.path.WormLayer;
import net.w3e.wlib.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.wlib.dungeon.layers.terra.DifficultyLayer;
import net.w3e.wlib.dungeon.layers.terra.TemperatureLayer;
import net.w3e.wlib.dungeon.layers.terra.WetLayer;

public class DungeonJsonAdapters {

	public static final DungeonJsonAdapters INSTANCE = new DungeonJsonAdapters();

	private final Map<String, Adapter<?>> ADAPTERS = new HashMap<>();

	protected DungeonJsonAdapters() {
		if (this.getClass() != DungeonJsonAdapters.class) {
			this.ADAPTERS.putAll(ADAPTERS);
		}
	}

	static {
		INSTANCE.registerDefault();
	}

	@SuppressWarnings("rawtypes")
	private void registerDefault() {
		JsonUtils.addAdapter(DungeonLayerFactory.class, new JsonSerializer<DungeonLayerFactory>() {
			@Override
			public final JsonElement serialize(DungeonLayerFactory src, Type typeOfSrc, JsonSerializationContext context) {
				return context.serialize(src.create(null), DungeonLayer.class);
			}
		});
		JsonUtils.addAdapter(DungeonInfoCountHolder.class, new JsonAdapter<DungeonInfoCountHolder, Integer>() {
			@Override
			public final JsonElement serialize(DungeonInfoCountHolder src, Type typeOfSrc, JsonSerializationContext context) {
				if (src.getValue() != -1) {
					return new JsonPrimitive(src.getValue());
				} else {
					return null;
				}
			}
			@Override
			public final Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				return context.deserialize(json, int.class);
			}
		});
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
		});

		this.registerConfigType(new Adapter<>(PathRepeatLayer.TYPE, PathRepeatLayer.class) {
			protected void registerJson() {
				JsonUtils.addAdapter(PathRepeatLayer.class, new PathRepeatLayer.PathRepeatLayerAdapter());
			}
		});
		this.registerConfigType(new Adapter<>(WormLayer.TYPE, WormLayer.class) {
			protected void registerJson() {
				JsonUtils.addAdapter(WormLayer.class, new WormLayer.WormLayerAdapter());
			}
		});
		this.registerConfigType(new Adapter<>(DistanceLayer.TYPE, DistanceLayer.class));
		this.registerConfigType(new Adapter<>(CompositeTerraLayer.TYPE, CompositeTerraLayer.class) {
			protected void registerJson() {
				JsonUtils.addAdapter(CompositeTerraLayer.class, new CompositeTerraLayer.CompositeTerraLayerAdapter());
			}
		});
		this.registerConfigType(new Adapter<>(DifficultyLayer.TYPE, DifficultyLayer.class) {
			protected void registerJson() {
				JsonUtils.addAdapter(DifficultyLayer.class, new DifficultyLayer.DifficultyLayerAdapter());
			}
		});
		this.registerConfigType(new Adapter<>(TemperatureLayer.TYPE, TemperatureLayer.class) {
			protected void registerJson() {
				JsonUtils.addAdapter(TemperatureLayer.class, new TemperatureLayer.TemperatureLayerAdapter());
			}
		});
		this.registerConfigType(new Adapter<>(WetLayer.TYPE, WetLayer.class) {
			protected void registerJson() {
				JsonUtils.addAdapter(WetLayer.class, new WetLayer.WetLayerAdapter());
			}
		});
		this.registerConfigType(new Adapter<>(ClearLayer.TYPE, ClearLayer.class));
		this.registerConfigType(new Adapter<>(RotateLayer.TYPE, RotateLayer.class) {
			protected void registerJson() {
				JsonUtils.addAdapter(RotateLayer.class, new RotateLayer.RotateLayerAdapter());
			}
		});
	}

	public final Adapter<?> getConfigType(String keyName) {
		return ADAPTERS.get(keyName);
	}

	public final void registerConfigType(Adapter<?> configType) {
		ADAPTERS.computeIfAbsent(configType.keyName(), e -> {
			configType.registerJson();
			return configType;
		});
	}

	public final void register() {
		JsonUtils.addTypedAdapter(DungeonLayer.class, this.ADAPTERS);
		for (Adapter<?> adapter : this.ADAPTERS.values()) {
			adapter.registerJson();
		}
	}

	@AllArgsConstructor
	public static class Adapter<CT> implements ConfigType<CT> {

		private final String keyName;
		@Getter(onMethod_ = {@Override})
		private final Class<CT> typeClass;

		@Override
		public final String keyName() {
			return this.keyName;
		}

		protected void registerJson() {}
	}
}
