package net.w3e.wlib.dungeon.layers.terra.noise;

import java.lang.reflect.Type;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.room.DungeonRoomData;

@DefaultCodec(VariantTerraLayer.VariantTerrraLayerJsonAdapter.class)
public class VariantTerraLayer extends NoiseLayer {

	public static final String TYPE = "terra/variant";

	public static final String KEY = "variantTerra";

	public VariantTerraLayer(DungeonGenerator generator, NoiseData data, int stepRate, boolean createRoomIfNotExists) {
		super(JSON_MAP.TEMPERATURE, generator, KEY, data, stepRate, createRoomIfNotExists);
	}

	@Override
	public final VariantTerraLayer createGenerator(DungeonGenerator generator) {
		return new VariantTerraLayer(generator, this.noise, this.stepRate, this.createRoomIfNotExists);
	}

	@Override
	protected void putData(DungeonRoomData data, float noise) {
		data.setVariant(noise);
	}

	static class VariantTerrraLayerJsonAdapter extends ReflectiveBuilderCodec<VariantTerraLayer> {

		public VariantTerrraLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, VariantTerraLayerData.class, registry);
		}

		private static class VariantTerraLayerData extends NoiseLayerData<VariantTerraLayer> {
			@Override
			public final VariantTerraLayer build(NoiseData noise, int stepRate, boolean createRoomIfNotExists) {
				return new VariantTerraLayer(null, noise, stepRate, createRoomIfNotExists);
			}
		}
	}

}
