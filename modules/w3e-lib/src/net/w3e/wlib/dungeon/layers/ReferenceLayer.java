package net.w3e.wlib.dungeon.layers;

import java.io.IOException;
import java.lang.reflect.Type;

import lombok.CustomLog;
import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalCodec;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.registry.DungeonRegistryContext;
import net.w3e.wlib.json.WJsonHelper;
import net.w3e.wlib.log.LogUtil;

@CustomLog
@DefaultCodec(ReferenceLayer.JCodec.class)
public class ReferenceLayer extends DungeonLayer {

	public static final String TYPE = "reference";

	private final DungeonKeySupplier key;
	private transient DungeonLayer layer;

	/**
	 * json
	 */
	@SuppressWarnings("unused")
	private ReferenceLayer() {
		this(null);
	}

	public ReferenceLayer(DungeonKeySupplier key, DungeonLayer layer) {
		this(key);
		this.layer = layer;
	}

	public ReferenceLayer(DungeonKeySupplier key) {
		super(JSON_MAP.REFERENCE, null);
		this.key = key;
	}

	@Override
	public void applyRegistryContext(DungeonRegistryContext registryContext) {
		this.layer = registryContext.getLayer(key);
		if (this.layer == null) {
			logNull();
		}
	}

	@Override
	public DungeonLayer createGenerator(DungeonGenerator generator) {
		if (this.layer != null) {
			return this.layer.createGenerator(generator);
		} else {
			logNull();
			return EmptyLayer.INSTANCE;
		}
	}

	private void logNull() {
		log.error(LogUtil.NULL.createMsg("layer \"" + this.key.get() + "\""));
	}

	@Override
	public void setupLayer(boolean composite) throws DungeonException {
		throw new UnsupportedOperationException("setupLayer");
	}

	@Override
	public float generate() throws DungeonException {
		throw new UnsupportedOperationException("generate");
	}
	
	static class JCodec extends AbstractCodec<ReferenceLayer> implements WJsonHelper {

		private final UniversalCodec<DungeonKeySupplier> codec;

		public JCodec(Type type, CodecRegistry registry) {
			super(type, registry);
			this.codec = registry.getCodecIndirect(DungeonKeySupplier.class);
		}

		@Override
		public void write(ReferenceLayer value, UniversalWriter writer) throws IOException {
			this.codec.write(value.key, writer);
		}

		@Override
		public ReferenceLayer read(UniversalReader reader) throws IOException {
			return new ReferenceLayer(nonNull(this.codec.read(reader), "key"));
		}

	}
}
