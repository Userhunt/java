package net.w3e.wlib.dungeon.layers.terra.biome;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.annotation.SkipSerialization;
import net.skds.lib2.io.codec.annotation.TransientComponent;
import net.skds.lib2.utils.ArrayUtils;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.ISetupRoomLayer;
import net.w3e.wlib.dungeon.layers.ListRegistryLayer;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonNamed;
import net.w3e.wlib.dungeon.layers.terra.IListLayer;
import net.w3e.wlib.dungeon.layers.terra.TerraLayer;
import net.w3e.wlib.dungeon.registry.DungeonRegistryContext;
import net.w3e.wlib.dungeon.registry.DungeonRegistryObject;
import net.w3e.wlib.dungeon.room.DungeonRoomData;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;
import net.w3e.wlib.json.WJsonBuilder;

@DefaultCodec(BiomeLayer.BiomeLayerJsonAdapter.class)
public class BiomeLayer extends ListRegistryLayer<DungeonRoomInfo, BiomeLayer.BiomeInfo> implements ISetupRoomLayer, IListLayer {

	public static final String TYPE = "terra/biome";

	public static final String KEY = "biome";

	private final DungeonKeySupplier def;

	private final transient BiomeRoomGenerator generator = new BiomeRoomGenerator(this);

	public BiomeLayer(DungeonGenerator generator, Collection<DungeonRegistryObject> values, DungeonKeySupplier def) {
		super(JSON_MAP.BIOME, generator, values);
		this.def = def;
	}
	
	@Override
	protected BiomeInfo getData(DungeonRegistryContext registryContext, DungeonKeySupplier keyName) {
		return registryContext.getBiome(keyName);
	}

	@Override
	public DungeonLayer createGenerator(DungeonGenerator generator) {
		return new BiomeLayer(generator, this.registryList, this.def);
	}

	@Override
	public void setupRoom(DungeonRoomInfo room) {
		room.getData().setBiome(this.def);
	}

	@Override
	public final void setupLayer(boolean composite) throws DungeonException {
		super.setupLayer(composite);
		this.workDataList.removeIf(BiomeInfo::notValid);
	}

	@Override
	public final float generate() throws DungeonException {
		return this.generator.generate();
	}

	@Override
	public final void generateRoom(DungeonRoomInfo room) {
		DungeonRoomData data = room.getData();
		BiomeDistance roomPos = new BiomeDistance(data);

		List<BiomeInfo> biomes = new ArrayList<>();
		float nearest = Float.MAX_VALUE;
		for (BiomeInfo biomePoint : BiomeLayer.this.workDataList) {
			float d = biomePoint.normalizedParams().squareDistanceTo(roomPos);
			if (d == nearest) {
				biomes.add(biomePoint);
			} else if (d < nearest) {
				nearest = d;
				biomes.clear();
				biomes.add(biomePoint);
			}
		}

		DungeonKeySupplier key = def;
		if (biomes.size() == 1) {
			key = biomes.getFirst().keyName();
		} else if (biomes.size() > 1) {
			key = Objects.requireNonNull(ArrayUtils.getRandom(biomes, random())).keyName();
		}
		if (key == null) {
			key = def;
		}

		room.getData().setBiome(key);
	}

	@DefaultCodec(BiomeLayer.BiomeInfoJsonAdapter.class)
	public record BiomeInfo(
		DungeonKeySupplier keyName,
		BiomeDistance params,
		@TransientComponent BiomeDistance normalizedParams,
		@SkipSerialization(predicate = DungeonInfoCountHolder.DungeonInfoCountHolderNullPredicate.class) DungeonInfoCountHolder count
	) implements IDungeonLimitedCount, IDungeonNamed {

		public BiomeInfo(DungeonKeySupplier keyName, BiomeDistance params, DungeonInfoCountHolder count) {
			this(keyName, params, params.getOrCreateNormalized(), count);
		}

		@Override
		public boolean notValid() {
			return this.keyName == null;
		}

		@Override
		public BiomeInfo clone() {
			return this.count.getValue() <= -1 ? this : new BiomeInfo(this.keyName(), this.params, this.normalizedParams, this.count().copy());
		}

	}

	public class BiomeRoomGenerator extends TerraLayer.IListLayerTerraHelper<DungeonRoomInfo, BiomeLayer> {
	
		public BiomeRoomGenerator(BiomeLayer layer) {
			super(layer);
		}

		@Override
		protected boolean isCreateRoomIfNotExists() {
			return false;
		}

		@Override
		protected int getStepRate() {
			return 50;
		}

		@Override
		protected void generateRoom(DungeonRoomInfo room) {
			BiomeLayer.this.generateRoom(room);
		}

	}

	static class BiomeLayerJsonAdapter extends ReflectiveBuilderCodec<BiomeLayer> {
		
		public BiomeLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, BiomeLayerData.class, registry);
		}

		private static class BiomeLayerData extends RegistryLayerDataBuilder<BiomeLayer, BiomeLayer.BiomeInfo> {

			protected DungeonKeySupplier def;

			@Override
			public BiomeLayer build() {
				return new BiomeLayer(null, this.values, this.def);
			}

		}
	}

	static class BiomeInfoJsonAdapter extends ReflectiveBuilderCodec<BiomeInfo> {

		public BiomeInfoJsonAdapter(Type type, CodecRegistry registry) {
			super(type, BiomeInfoData.class, registry);
		}

		public static class BiomeInfoData implements WJsonBuilder<BiomeInfo> {
			public DungeonKeySupplier keyName;

			public BiomeDistance params;

			public DungeonInfoCountHolder count = DungeonInfoCountHolder.NULL;

			@Override
			public BiomeInfo build() {
				this.nonNull(this.keyName, "keyName");
				this.nonNull(this.params, "params");

				return new BiomeInfo(this.keyName, this.params, this.count);
			}

		}

	}

}
