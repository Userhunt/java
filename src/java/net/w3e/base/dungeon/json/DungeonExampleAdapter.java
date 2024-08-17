package net.w3e.base.dungeon.json;

import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.layers.FeatureLayer.FeatureLayerData;
import net.w3e.base.dungeon.layers.FeatureLayer.FeatureVariant;
import net.w3e.base.dungeon.layers.FeatureLayer.FeatureVariantData;
import net.w3e.base.dungeon.layers.RoomLayer.RoomLayerData;
import net.w3e.base.dungeon.layers.RoomLayer.RoomVariant;
import net.w3e.base.dungeon.layers.RoomLayer.RoomVariantData;
import net.w3e.base.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.base.dungeon.layers.terra.BiomeLayer.BiomeInfo;
import net.w3e.base.dungeon.layers.terra.BiomeLayer.BiomeInfoData;
import net.w3e.base.dungeon.layers.terra.BiomeLayer.BiomeLayerData;

public class DungeonExampleAdapter {
	public static final DungeonLayerJsonAdapter LAYERS_ADAPTER = DungeonGeneratorJsonAdapters.getLayersAdapter();

	static {
		LAYERS_ADAPTER.register("terra/biome", BiomeLayerDataString.class);
		LAYERS_ADAPTER.register("room", RoomLayerDataString.class);
		LAYERS_ADAPTER.register("feature", FeatureLayerDataString.class);
	}

	public static final Gson GSON = new GsonBuilder().registerTypeAdapter(DungeonLayer.class, LAYERS_ADAPTER).create();

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
	
		private class BiomeInfoDataString extends BiomeInfoData<String> {
			@Getter
			private String value;
			@Getter
			private BaseLayerRoomRange layerRange = BaseLayerRoomRange.NULL;
		}
	}

	private static class RoomLayerDataString extends RoomLayerData<String> {

		private RoomVariantDataString[] variants;

		@Override
		@SuppressWarnings("unchecked")
		protected final RoomVariant<String>[] getVariants() {
			this.nonNull("variants", this.variants);
			return Stream.of(this.variants).map(e -> e.withDungeon(null)).toArray(RoomVariant[]::new);
		}

		private class RoomVariantDataString extends RoomVariantData<String> {
			@Getter
			private String value;
			@Getter
			private BaseLayerRoomRange layerRange = BaseLayerRoomRange.NULL;
		}
	}

	private static class FeatureLayerDataString extends FeatureLayerData<String> {

		private FeatureVariantDataString[] variants;

		@Override
		@SuppressWarnings("unchecked")
		protected FeatureVariant<String>[] getFeatures() {
			this.nonNull("variants", this.variants);
			return Stream.of(this.variants).map(e -> e.withDungeon(null)).toArray(FeatureVariant[]::new);
		}

		private class FeatureVariantDataString extends FeatureVariantData<String> {
			@Getter
			private String value;
			@Getter
			private BaseLayerRoomRange layerRange = BaseLayerRoomRange.NULL;
		}
	}
}
