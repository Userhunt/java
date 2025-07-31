package net.w3e.app.gui.frame.dungeon;

import java.io.File;
import java.util.ArrayList;

import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.elements.*;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.w3e.app.gui.frame.dungeon.layers.TestNoiseLayer;
import net.w3e.wlib.collection.CollectionBuilder;
import net.w3e.wlib.collection.CollectionBuilder.SimpleCollectionBuilder;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonGeneratorFactory;
import net.w3e.wlib.dungeon.json.DungeonJsonAdaptersString;
import net.w3e.wlib.dungeon.layers.*;
import net.w3e.wlib.dungeon.layers.path.*;
import net.w3e.wlib.dungeon.layers.path.lab.LabDFSLayer;
import net.w3e.wlib.dungeon.layers.path.lab.LabHAKLayer;
import net.w3e.wlib.dungeon.layers.terra.*;
import net.w3e.wlib.json.CompareJsonUtil;
import net.w3e.wlib.mat.WBoxI;

public class DungeonGeneratorDebug {

	public static int SIZE = 8;

	public static final SimpleCollectionBuilder<DungeonLayerFactory, ArrayList<DungeonLayerFactory>> factoryCollectionBuilder() {
		return CollectionBuilder.list(DungeonLayerFactory.class);
	}

	public enum Mode {
		DEFAULT,
		DEFAULT_NO_CLEAR,
		LAB_HAK,
		LAB_DFS,
		NOISE_W3E,
		NOISE_SASI,
		;
	}

	// TODO
	/**
	 * <pre>BiomeLayer - not fill all rooms</pre>
	 * <pre>DelvePathLayer (poe)</pre>
	 * <pre>DLALayer https://youtu.be/gsJHzBTPG0Y?t=588</pre>
	 * 
	 */
	public static final DungeonGenerator example(long seed, Direction direction, boolean debug, Mode mode) {
		final int size = SIZE;
		SimpleCollectionBuilder<DungeonLayerFactory, ArrayList<DungeonLayerFactory>> layers = factoryCollectionBuilder();
		switch (mode) {
			case DEFAULT, DEFAULT_NO_CLEAR -> {
				layers.add(
					// temperature, wet, difficulty
					CompositeTerraLayer::example,
					// biomes
					BiomeLayer::example,
					// path
					gen -> PathRepeatLayer.example(gen, size),
					//WormLayer::example//,
					// distance
					DistanceLayer::example,
					// rooms
					RoomLayer::example,
					// features - spawners, chests, ?
					FeatureLayer::example
				);
				if (mode != DungeonGeneratorDebug.Mode.DEFAULT_NO_CLEAR) {
					layers.add(
						// clear for save
						ClearLayer::example
					);
				}
			}
			case LAB_HAK -> {
				layers.add(LabHAKLayer::example);
			}
			case LAB_DFS -> {
				layers.add(LabDFSLayer::example);
			}
			case NOISE_W3E -> {
				layers.add(TestNoiseLayer::w3e);
			}
			case NOISE_SASI -> {
				layers.add(TestNoiseLayer::sasai);
			}
		}
		if (direction != Direction.SOUTH) {
			layers.add(gen -> new RotateLayer(gen, direction));
		}
		DungeonGeneratorFactory generator = new DungeonGeneratorFactory(seed, new WBoxI(-size, 0, -size, size, 0, size), new MapTString(), layers.build());
		return exampleSave(generator, debug).create(null, null);
	}

	public static final DungeonGeneratorFactory exampleSave(DungeonGeneratorFactory generator, boolean debug) {
		DungeonGeneratorFactory data;

		/*data = exampleGson(jsonObject, debug);
		if (data != null) {
			generator = data;
		}*/

		data = exampleSasaiGson(generator, debug);
		if (data != null) {
			generator = data;
		}

		return generator;
	}

	/*private static DungeonGenerator exampleGson(DungeonGenerator generator, boolean debug) {
		System.out.println("gson start");
		Gson gson = JsonUtils.getGSON_COMPACT();

		final JsonObject jsonObject = JsonParser.parseString(JsonUtils.getGSON_COMPACT().toJson(generator)).getAsJsonObject();
		JsonUtils.saveConfig(new File(String.format("dungeon/example_%s.json", generator.seed)), jsonObject);

		DungeonGenerator data = gson.fromJson(jsonObject, DungeonGenerator.class);

		JsonObject result = JsonParser.parseString(gson.toJson(data)).getAsJsonObject();

		if (!jsonObject.equals(result)) {
			JsonArray jsonSave = jsonObject.remove("layers").getAsJsonArray();
			JsonArray jsonRead = result.remove("layers").getAsJsonArray();
			if (!jsonObject.equals(result)) {
				System.out.println(jsonObject);
				System.out.println(result);
			}
			if (!jsonSave.equals(jsonRead)) {
				if (jsonSave.size() != jsonRead.size()) {
					System.out.println("wrong size");
				} else {
					for (int i = 0; i < jsonSave.size(); i++) {
						JsonElement js = jsonSave.get(i);
						JsonElement jr = jsonRead.get(i);
						if (!js.equals(jr)) {
							System.err.println(String.format("layer[%s]", i));
							System.err.println(js);
							System.err.println(jr);
						}
					}
				}
			}
			return null;
		} else {
			return data;
		}
	}*/

	private static DungeonGeneratorFactory exampleSasaiGson(DungeonGeneratorFactory generator, boolean debug) {
		//System.out.println("sasai start");
		final JsonObject jsonObject = JsonUtils.parseJson(JsonUtils.toJson(generator), JsonObject.class);
		JsonUtils.saveJson(new File(String.format("dungeon/example_%s_sasai.json", generator.seed)), jsonObject);

		if (!debug) {
			return generator;
		}

		DungeonGeneratorFactory data = JsonUtils.parseJson(jsonObject, DungeonGeneratorFactory.class);

		JsonObject result = JsonUtils.parseJson(JsonUtils.toJson(data), JsonObject.class);

		CompareJsonUtil.CompareResult compare = CompareJsonUtil.compare(jsonObject, result);
		System.out.println(compare.print(true));

		if (!jsonObject.equals(result)) {
			JsonArray jsonSave = jsonObject.remove("layers").getAsJsonArray();
			JsonArray jsonRead = result.remove("layers").getAsJsonArray();
			if (!jsonObject.equals(result)) {
				System.out.println(jsonObject);
				System.out.println(result);
			}
			if (!jsonSave.equals(jsonRead)) {
				if (jsonSave.size() != jsonRead.size()) {
					System.out.println("wrong size");
				} else {
					for (int i = 0; i < jsonSave.size(); i++) {
						JsonElement js = jsonSave.get(i);
						JsonElement jr = jsonRead.get(i);
						if (!js.equals(jr)) {
							System.err.println(String.format("layer[%s]", i));
							System.err.println(js);
							System.err.println(jr);
						}
					}
				}
			}
			return data;
		}

		return null;
	}

	public static void main(String[] args) {
		SKDSLogger.replaceOuts();

		DungeonJsonAdaptersString.initString();

		//InnerDungeonGenerator inner = new InnerDungeonGenerator();

		example(0, Direction.SOUTH, true, Mode.DEFAULT);
		System.out.println("done dungeon generator");
	}
}
