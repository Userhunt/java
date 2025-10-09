package net.w3e.wlib.dungeon.layers;

import java.lang.reflect.Type;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.json.adapters.WJSonEmptyAdapter;

@DefaultCodec(EmptyLayer.EmptyLayerJsonAdapter.class)
public class EmptyLayer extends DungeonLayer {

	public static final EmptyLayer INSTANCE = new EmptyLayer();

	public static final String TYPE = "empty";

	public EmptyLayer() {
		super(JSON_MAP.EMPTY, null);
	}

	@Override
	public final EmptyLayer createGenerator(DungeonGenerator generator) {
		return this;
	}

	@Override
	public final void setupLayer(boolean composite) throws DungeonException {}

	@Override
	public final float generate() throws DungeonException {
		return 1;
	}

	static class EmptyLayerJsonAdapter extends WJSonEmptyAdapter {

		public EmptyLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		protected final EmptyLayer create() {
			return INSTANCE;
		}
	}

}
