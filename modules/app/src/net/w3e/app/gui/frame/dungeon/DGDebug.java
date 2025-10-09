package net.w3e.app.gui.frame.dungeon;

import java.io.File;
import java.util.ArrayList;

import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.io.json.elements.*;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.w3e.app.gui.frame.dungeon.layers.TestNoiseLayer;
import net.w3e.app.gui.frame.dungeon.registry.DGRegistry;
import net.w3e.wlib.collection.CollectionBuilder;
import net.w3e.wlib.collection.CollectionBuilder.SimpleCollectionBuilder;
import net.w3e.wlib.dungeon.DungeonExamples;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonJsonAdaptersString;
import net.w3e.wlib.dungeon.room.DungeonRoomData;
import net.w3e.wlib.json.CompareJsonUtil;
import net.w3e.wlib.mat.WBoxI;

public class DGDebug {

	public static int SIZE = 8;

	public static final SimpleCollectionBuilder<DungeonLayer, ArrayList<DungeonLayer>> factoryCollectionBuilder() {
		return CollectionBuilder.list(DungeonLayer.class);
	}

	public enum Mode {
		DEFAULT,
		DEFAULT_NO_CLEAR,
		LAB_HAK,
		LAB_DFS,
		NOISE_W3E,
		NOISE_SASI,
		REGISTRY,
		;
	}

	// TODO
	/**
	 * <pre>BiomeLayer - not fill all rooms</pre>
	 * <pre>DelvePathLayer (poe)</pre>
	 * <pre>DLALayer https://youtu.be/gsJHzBTPG0Y?t=588</pre>
	 * 
	 */
	public static final DGFactory example(DGRegistry registry, long seed, Mode mode) {
		final int size = SIZE;
		SimpleCollectionBuilder<DungeonLayer, ArrayList<DungeonLayer>> layers = factoryCollectionBuilder();

		switch (mode) {
			case DEFAULT, DEFAULT_NO_CLEAR -> {
				layers.add(
					// temperature, wet, difficulty
					DungeonExamples.compositeTerraLayerExample(),
					// biomes
					DungeonExamples.biomeLayerExample(),

					// path
					DungeonExamples.pathRepeatLayerExample(size),
					//WormLayer::example//,
					// distance
					DungeonExamples.distanceLayerExample(),

					// rooms
					DungeonExamples.roomLayerExample(),
					// features - spawners, chests, ?
					DungeonExamples.featureLayerExample()
				);
				if (mode != DGDebug.Mode.DEFAULT_NO_CLEAR) {
					layers.add(
						// clear for save
						DungeonExamples.clearLayerExample()
					);
				}
			}
			case LAB_HAK -> {
				layers.add(DungeonExamples.labHAKLayerExample());
			}
			case LAB_DFS -> {
				layers.add(DungeonExamples.labDFSLayerExample());
			}
			case NOISE_W3E -> {
				layers.add(TestNoiseLayer.w3e());
			}
			case NOISE_SASI -> {
				layers.add(TestNoiseLayer.sasai());
			}
			case REGISTRY -> {
				return registry.getDungeon("[" + seed + "]");
			}
		}
		return new DGFactory(seed, new WBoxI(-size, 0, -size, size, 0, size), new DungeonRoomData(), layers.build());
	}

	public static final DGFactory exampleSave(DGFactory generator, boolean debug) {
		DGFactory data;

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

	private static DGFactory exampleSasaiGson(DGFactory generator, boolean debug) {
		//System.out.println("sasai start");
		final JsonObject jsonObject = SosisonUtils.parseJson(SosisonUtils.toJson(generator), JsonObject.class);
		SosisonUtils.saveJson(new File(String.format("dungeon/example_%s_sasai.json", generator.seed)), jsonObject);

		if (!debug) {
			return generator;
		}

		DGFactory data = SosisonUtils.parseJson(jsonObject, DGFactory.class);

		JsonObject result = SosisonUtils.parseJson(SosisonUtils.toJson(data), JsonObject.class);

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

		DGFactory dungeon = example(null, 0, Mode.DEFAULT);
		exampleSave(dungeon, true);
		System.out.println("done dungeon generator");
	}
}
