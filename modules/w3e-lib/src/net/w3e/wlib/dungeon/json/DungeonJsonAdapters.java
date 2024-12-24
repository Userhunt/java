package net.w3e.wlib.dungeon.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

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
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonGenerator.LayerFactory;
import net.w3e.wlib.dungeon.layers.ClearLayer;
import net.w3e.wlib.dungeon.layers.DistanceLayer;
import net.w3e.wlib.dungeon.layers.FeatureLayer;
import net.w3e.wlib.dungeon.layers.RoomLayer;
import net.w3e.wlib.dungeon.layers.RotateLayer;
import net.w3e.wlib.dungeon.layers.FeatureLayer.FeatureLayerAdapter;
import net.w3e.wlib.dungeon.layers.FeatureLayer.FeatureVariantAdapter;
import net.w3e.wlib.dungeon.layers.FeatureLayer.FeatureVariantData;
import net.w3e.wlib.dungeon.layers.RoomLayer.RoomLayerAdapter;
import net.w3e.wlib.dungeon.layers.RoomLayer.RoomVariant;
import net.w3e.wlib.dungeon.layers.RoomLayer.RoomVariantAdapter;
import net.w3e.wlib.dungeon.layers.RoomLayer.RoomVariantData;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.layers.path.PathRepeatLayer;
import net.w3e.wlib.dungeon.layers.path.WormLayer;
import net.w3e.wlib.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.wlib.dungeon.layers.terra.BiomeLayer;
import net.w3e.wlib.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.wlib.dungeon.layers.terra.DifficultyLayer;
import net.w3e.wlib.dungeon.layers.terra.TemperatureLayer;
import net.w3e.wlib.dungeon.layers.terra.WetLayer;
import net.w3e.wlib.dungeon.layers.terra.BiomeLayer.BiomeInfoAdapter;
import net.w3e.wlib.dungeon.layers.terra.BiomeLayer.BiomeInfoData;
import net.w3e.wlib.dungeon.layers.terra.BiomeLayer.BiomeLayerAdapter;
import net.w3e.wlib.mat.WBoxI;

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
		JsonUtils.addAdapter(LayerFactory.class, new JsonSerializer<LayerFactory>() {
			@Override
			public final JsonElement serialize(LayerFactory src, Type typeOfSrc, JsonSerializationContext context) {
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

	public static void init() {
		INSTANCE.registerConfigType(new Adapter<>(BiomeLayer.TYPE, BiomeLayer.class) {
			protected void registerJson() {
				JsonUtils.addAdapter(BiomeLayer.class, new BiomeLayerAdapterString());
				JsonUtils.addAdapter(BiomeInfoData.class, new BiomeInfoAdapter<>(BiomeLayerAdapterString.BiomeInfoDataString.class));
			}
		});
		INSTANCE.registerConfigType(new Adapter<>(RoomLayer.TYPE, RoomLayer.class) {
			protected void registerJson() {
				JsonUtils.addAdapter(RoomLayer.class, new RoomLayerAdapterString());
				JsonUtils.addAdapter(RoomVariantData.class, new RoomVariantAdapter<>(RoomLayerAdapterString.RoomVariantDataString.class));
			}
		});
		INSTANCE.registerConfigType(new Adapter<>(FeatureLayer.TYPE, FeatureLayer.class) {
			protected void registerJson() {
				JsonUtils.addAdapter(FeatureLayer.class, new FeatureLayerAdapterString());
				JsonUtils.addAdapter(FeatureVariantData.class, new FeatureVariantAdapter<>(FeatureLayerAdapterString.FeatureVariantDataString.class));
			}
		});
		INSTANCE.register();
	}

	public void register() {
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

	public static class EDungeon {
		private long seed = 0;
		private WBoxI dimension = new WBoxI(0, 0, 0, 0, 0, 0).expand(4, 0, 4);
		private MapTString data = new MapTString();
		private DungeonLayer[] layers = new DungeonLayer[0];
		private final transient List<LayerFactory> layerFactories = new ArrayList<>();

		public final DungeonGenerator createInstance() {
			if (this.layerFactories.size() != this.layers.length) {
				this.layerFactories.clear();
				for (DungeonLayer layer : this.layers) {
					if (layer == null) {
						throw new NullPointerException();
					}
					this.layerFactories.add(genrator -> layer.withDungeon(genrator));
				}
			}
			Supplier<MapTString> map = this.data != null ? () -> new MapTString(this.data) : MapTString::new;
			return new DungeonGenerator(this.seed, this.dimension, map, this.layerFactories);
		}
	}

	private static class BiomeLayerAdapterString extends BiomeLayerAdapter<String> {

		public BiomeLayerAdapterString() {
			super(BiomeLayerDataString.class);
		}

		private static class BiomeLayerDataString extends BiomeLayer.BiomeLayerData<String> {
			@Getter(onMethod_ = {@Override})
			private String def;
		}

		private static class BiomeInfoDataString extends BiomeLayer.BiomeInfoData<String> {
			@Getter
			private String value;
			@Getter
			private BaseLayerRoomRange layerRange = BaseLayerRoomRange.NULL;
		}
	}

	private static class RoomLayerAdapterString extends RoomLayerAdapter<String> {

		public RoomLayerAdapterString() {
			super(RoomLayerDataString.class);
		}

		private static class RoomLayerDataString extends RoomLayer.RoomLayerData<String> {}

		private static class RoomVariantDataString extends RoomVariantData<String> {
			@Getter(onMethod_ = {@Override})
			private String value;
			@Getter(onMethod_ = {@Override})
			private BaseLayerRoomRange layerRange = BaseLayerRoomRange.NULL;
		}
	}

	private static class FeatureLayerAdapterString extends FeatureLayerAdapter<String> {

		public FeatureLayerAdapterString() {
			super(FeatureLayerDataString.class);
		}

		private static class FeatureLayerDataString extends FeatureLayer.FeatureLayerData<String> {}

		private static class FeatureVariantDataString extends FeatureVariantData<String> {
			@Getter(onMethod_ = {@Override})
			private String value;
			@Getter(onMethod_ = {@Override})
			private BaseLayerRoomRange layerRange = BaseLayerRoomRange.NULL;
		}
	}
}
