package net.w3e.app.gui.dungeon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.w3e.wlib.dungeon.DungeonGeneratorFactory;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonNamed;
import net.w3e.wlib.dungeon.room.DungeonRoomData;
import net.w3e.wlib.mat.WBoxI;

@DefaultCodec(DGFactory.JCodec.class)
public class DGFactory extends DungeonGeneratorFactory implements IDungeonNamed {

	private transient DungeonKeySupplier keyName;

	public DGFactory(long seed, WBoxI dimension, DungeonRoomData dataFactory, List<DungeonLayer> layers) {
		super(seed, dimension, dataFactory, layers);
	}

	public final DGFactory setKeyName(DungeonKeySupplier keyName) {
		this.keyName = keyName;
		return this;
	}

	public final DungeonKeySupplier keyName() {
		return this.keyName;
	}

	public final List<DungeonLayer> getLayers() {
		return this.layers;
	}

	public final DGFactory withSeed(long seed) {
		return new DGFactory(seed, this.dimension, this.dataFactory, this.layers).setKeyName(this.keyName);
	}

	public final DGFactory withDimension(WBoxI dimension) {
		return new DGFactory(this.seed, dimension, this.dataFactory, this.layers).setKeyName(this.keyName);
	}

	public final DGFactory withLayers(List<DungeonLayer> layers) {
		return new DGFactory(this.seed, this.dimension, this.dataFactory, layers).setKeyName(this.keyName);
	}

	public final DGFactory withLayers(Consumer<List<DungeonLayer>> layers) {
		List<DungeonLayer> list = new ArrayList<>(this.layers);
		layers.accept(list);
		return new DGFactory(this.seed, this.dimension, this.dataFactory, list).setKeyName(this.keyName);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
	}

	static class JCodec extends DungeonGeneratorFactory.JCodec {

		public JCodec(Type type, CodecRegistry registry) {
			super(type, DGFData.class, registry);
		}

		protected static class DGFData extends DungeonGeneratorFactory.JCodec.DGFData {

			@Override
			public DungeonGeneratorFactory build() {
				this.nonNull(this.data, "data");
				return new DGFactory(this.seed, this.dimension, this.data, this.layers);
			}

		}
	}

}
