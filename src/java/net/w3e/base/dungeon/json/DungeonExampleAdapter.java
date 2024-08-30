package net.w3e.base.dungeon.json;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import net.w3e.base.collection.MapT.MapTString;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonGenerator.LayerFactory;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.layers.FeatureLayer;
import net.w3e.base.dungeon.layers.RoomLayer;
import net.w3e.base.dungeon.layers.FeatureLayer.FeatureLayerData;
import net.w3e.base.dungeon.layers.FeatureLayer.FeatureVariant;
import net.w3e.base.dungeon.layers.FeatureLayer.FeatureVariantData;
import net.w3e.base.dungeon.layers.RoomLayer.RoomLayerData;
import net.w3e.base.dungeon.layers.RoomLayer.RoomVariant;
import net.w3e.base.dungeon.layers.RoomLayer.RoomVariantData;
import net.w3e.base.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.base.dungeon.layers.terra.BiomeLayer;
import net.w3e.base.dungeon.layers.terra.BiomeLayer.BiomeInfo;
import net.w3e.base.dungeon.layers.terra.BiomeLayer.BiomeInfoData;
import net.w3e.base.dungeon.layers.terra.BiomeLayer.BiomeLayerData;
import net.w3e.base.math.vector.i.WBoxI;

public class DungeonExampleAdapter {
	public static final DungeonLayerJsonAdapter LAYER_ADAPTER = DungeonGeneratorJsonAdapters.getLayerAdapter();

	static {
		LAYER_ADAPTER.register(BiomeLayer.TYPE, BiomeLayerDataString.class);
		LAYER_ADAPTER.register(RoomLayer.TYPE, RoomLayerDataString.class);
		LAYER_ADAPTER.register(FeatureLayer.TYPE, FeatureLayerDataString.class);
	}

	public static final Gson GSON = DungeonGeneratorJsonAdapters.modifyGson(new GsonBuilder()
		.registerTypeAdapter(DungeonLayer.class, LAYER_ADAPTER)
	).create();

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

	private static class BiomeLayerDataString extends BiomeLayerData<String> {

		@Getter
		private String def;

		private BiomeInfoDataString[] biomes;

		@Override
		@SuppressWarnings("unchecked")
		protected final BiomeInfo<String>[] getBiomes() {
			this.nonNull("biomes", this.biomes);
			return Stream.of(this.biomes).map(e -> e.withDungeon(null)).toArray(BiomeInfo[]::new);
		}

		private static class BiomeInfoDataString extends BiomeInfoData<String> {
			@Getter
			private String value;
			@Getter
			private BaseLayerRoomRange layerRange = BaseLayerRoomRange.NULL;
		}
	}

	private static class RoomLayerDataString extends RoomLayerData<String> {

		private RoomVariantDataString[] rooms;

		@Override
		@SuppressWarnings("unchecked")
		protected final RoomVariant<String>[] getRooms() {
			this.nonNull("rooms", this.rooms);
			return Stream.of(this.rooms).map(e -> e.withDungeon(null)).toArray(RoomVariant[]::new);
		}

		private static class RoomVariantDataString extends RoomVariantData<String> {
			@Getter
			private String value;
			@Getter
			private BaseLayerRoomRange layerRange = BaseLayerRoomRange.NULL;
		}
	}

	private static class FeatureLayerDataString extends FeatureLayerData<String> {

		private FeatureVariantDataString[] features;

		@Override
		@SuppressWarnings("unchecked")
		protected FeatureVariant<String>[] getFeatures() {
			this.nonNull("features", this.features);
			return Stream.of(this.features).map(e -> e.withDungeon(null)).toArray(FeatureVariant[]::new);
		}

		private static class FeatureVariantDataString extends FeatureVariantData<String> {
			@Getter
			private String value;
			@Getter
			private BaseLayerRoomRange layerRange = BaseLayerRoomRange.NULL;
		}
	}
}
