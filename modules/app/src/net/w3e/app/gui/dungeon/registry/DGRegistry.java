package net.w3e.app.gui.dungeon.registry;

import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.utils.SKDSUtils;
import net.w3e.app.gui.dungeon.DGFactory;
import net.w3e.lib.utils.FileUtils;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.FeatureLayer;
import net.w3e.wlib.dungeon.layers.ReferenceLayer;
import net.w3e.wlib.dungeon.layers.RoomLayer;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonNamed;
import net.w3e.wlib.dungeon.layers.terra.biome.BiomeLayer;
import net.w3e.wlib.dungeon.layers.terra.biome.BiomeLegacyLayer;
import net.w3e.wlib.dungeon.registry.DungeonRegistryContext;
import net.w3e.wlib.dungeon.registry.DungeonRegistryObject;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class DGRegistry implements DungeonRegistryContext {

	public final Map<String, DGFactory> DUNGEONS = new TreeMap<>();
	@DefaultCodec(JLayerCodec.class)
	private final Map<String, DungeonRegistryObject> LAYERS = new TreeMap<>();
	private final Map<String, BiomeLegacyLayer.BiomeInfo> BIOMES_LEGACY = new TreeMap<>();
	private final Map<String, BiomeLayer.BiomeInfo> BIOMES = new TreeMap<>();
	private final Map<String, FeatureLayer.FeatureVariant> FEATURES = new TreeMap<>();
	private final Map<String, RoomLayer.RoomVariant> ROOMS = new TreeMap<>();

	// TODO избавиться от множества файлов?
	private static final File ROOT_FOLDER = new File("dungeon/registry");

	private static final File DUNGEON_FOLDER = new File(ROOT_FOLDER, "dungeon");
	private static final File LAYER_FOLDER = new File(ROOT_FOLDER, "layer");
	private static final File BIOME_LEGACY_FOLDER = new File(ROOT_FOLDER, "biome_legacy");
	private static final File BIOME_FOLDER = new File(ROOT_FOLDER, "biome");
	private static final File FEATURE_FOLDER = new File(ROOT_FOLDER, "feature");
	private static final File ROOM_FOLDER = new File(ROOT_FOLDER, "room");

	private static final Predicate<File> FILE_PREDICATE = f -> f.getName().endsWith(".json");

	public final void clear() {
		this.DUNGEONS.clear();
		this.LAYERS.clear();
		this.BIOMES_LEGACY.clear();
		this.BIOMES.clear();
		this.FEATURES.clear();
		this.ROOMS.clear();
	}

	public final void load() {
		load(BIOME_LEGACY_FOLDER, BiomeLegacyLayer.BiomeInfo.class, this::registerLegacyBiome);
		load(BIOME_FOLDER, BiomeLayer.BiomeInfo.class, this::registerBiome);
		load(FEATURE_FOLDER, FeatureLayer.FeatureVariant.class, this::registerFeature);
		load(ROOM_FOLDER, RoomLayer.RoomVariant.class, this::registerRoom);
		loadLayers();
		loadDungeons();
	}

	private void loadDungeons() {
		for (File file : SKDSUtils.collectFileTree(DUNGEON_FOLDER, FILE_PREDICATE)) {
			String key = DUNGEON_FOLDER.toPath().relativize(file.toPath()).toString();
			key = key.substring(0, key.length() - 5);
			DGFactory dungeon = SosisonUtils.readJson(file, DGFactory.class);
			registerDungeon(key, dungeon);
		}
	}

	private void loadLayers() {
		for (File file : SKDSUtils.collectFileTree(LAYER_FOLDER, FILE_PREDICATE)) {
			String key = LAYER_FOLDER.toPath().relativize(file.toPath()).toString();
			key = key.substring(0, key.length() - 5);
			DungeonLayer layer = SosisonUtils.readJson(file, DungeonLayer.class);
			registerLayer(key, layer);
		}
	}

	private <T extends IDungeonNamed> void load(File rootFolder, Class<T> clazz, BiConsumer<String, T> register) {
		for (File file : SKDSUtils.collectFileTree(rootFolder, FILE_PREDICATE)) {
			T entry = SosisonUtils.readJson(file, clazz);
			String key = rootFolder.toPath().relativize(file.toPath()).toString();
			key = key.substring(0, key.length() - 5);
			register.accept(key, entry);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public final void save() {
		save(DUNGEONS, DUNGEON_FOLDER);
		save(LAYERS, LAYER_FOLDER, (UniversalSerializer) LAYER_CODEC);
		save(BIOMES_LEGACY, BIOME_LEGACY_FOLDER);
		save(BIOMES, BIOME_FOLDER);
		save(FEATURES, FEATURE_FOLDER);
		save(ROOMS, ROOM_FOLDER);
	}

	private void save(Map<String, ?> map, File rootFolder) {
		save(map, rootFolder, null);
	}

	private void save(Map<String, ?> map, File rootFolder, UniversalSerializer<Object> serializer) {
		Map<String, List<Object>> list = new HashMap<>();
		for (Entry<String, ?> entry : map.entrySet()) {
			Object value = entry.getValue();
			list.computeIfAbsent(entry.getKey(), _ -> new ArrayList<>(1)).add(value);
		}
		saveTree(list, rootFolder, serializer);
	}

	private void saveTree(Map<String, List<Object>> map, File rootFolder, UniversalSerializer<Object> serializer) {
		map.entrySet().removeIf(e -> {
			if (e.getValue().size() == 1) {
				File file = new File(rootFolder, e.getKey() + ".json");
				Object first = e.getValue().getFirst();
				if (serializer != null) {
					Path path = file.toPath();
					try {
						String text = serializer.toJson(first);
						FileUtils.createParentDirs(path);
						Files.writeString(path, text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
						return true;
					} catch (Exception ex) {
						ex.printStackTrace(System.err);
					}
				}
				SosisonUtils.saveJson(file, first);
				return true;
			}
			return false;
		});
		if (map.isEmpty()) {
			return;
		}
		Map<String, List<Object>> list = new HashMap<>();
		for (Entry<String, List<Object>> entry : map.entrySet()) {
			String key = entry.getKey();
			List<Object> value = entry.getValue();
			int size = value.size();
			for (int i = 0; i < size; i++) {
				list.computeIfAbsent(key + "/" + i, _ -> new ArrayList<>(1)).add(value.get(i));
			}
		}
		saveTree(list, rootFolder, serializer);
	}

	public final void delete() {
		FileUtils.deleteFilesFromDirectory(ROOT_FOLDER);
	}

	public final void registerDungeon(String key, DGFactory dungeon) {
		dungeon.applyRegistryContext(this);
		DUNGEONS.put(key, dungeon);
	}

	public final DGFactory getDungeon(String key) {
		return DUNGEONS.get(key);
	}

	public final void registerLayer(String key, DungeonLayer layer) {
		layer.applyRegistryContext(this);
		LAYERS.put(key, new DungeonRegistryObject(null, layer));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getTyped(DungeonKeySupplier key, Class<T> type) {
		if (type == BiomeLayer.BiomeInfo.class) {
			return (T) BIOMES_LEGACY.get((String) key.get());
		}
		if (type == FeatureLayer.FeatureVariant.class) {
			return (T) FEATURES.get((String) key.getRaw());
		}
		if (type == RoomLayer.RoomVariant.class) {
			return (T) ROOMS.get((String) key.getRaw());
		}
		throw new IllegalStateException(type.getSimpleName());
	}

	@Override
	public final DungeonLayer getLayer(DungeonKeySupplier key) {
		return LAYERS.get((String) key.get()).getObject();
	}

	public final void registerLegacyBiome(String key, BiomeLegacyLayer.BiomeInfo biome) {
		BIOMES_LEGACY.put(key, biome);
	}

	public final void registerBiome(String key, BiomeLayer.BiomeInfo biome) {
		BIOMES.put(key, biome);
	}

	@Override
	public BiomeLayer.BiomeInfo getBiome(DungeonKeySupplier key) {
		return BIOMES.get((String) key.get());
	}

	public final void registerFeature(String key, FeatureLayer.FeatureVariant feature) {
		FEATURES.put(key, feature);
	}

	public final void registerRoom(String key, RoomLayer.RoomVariant room) {
		ROOMS.put(key, room);
	}

	public static final String PREFIX = "[";
	public static final String SUFFIX = "]";

	private String layerKey(DGFactory dungeon, int i) {
		List<DungeonLayer> values = dungeon.getLayers();
		int stringSize = String.valueOf(values.size()).length();
		String indexString = String.valueOf(i);
		return PREFIX + dungeon.seed + SUFFIX + PREFIX + "0".repeat(stringSize - indexString.length()) + indexString + SUFFIX;
	}

	private <T> String objectKey(DGFactory dungeon, DungeonLayer layer, String layerKey, List<DungeonRegistryObject> values, int i) {
		int stringSize = String.valueOf(values.size()).length();
		String indexString = String.valueOf(i);
		return layerKey + PREFIX + "0".repeat(stringSize - indexString.length()) + indexString + SUFFIX;
	}

	public final void registerLayerData(DGFactory dungeon) {
		List<DungeonLayer> values = dungeon.getLayers();
		for (int i = 0; i < values.size(); i++) {
			String layerKey = layerKey(dungeon, i);
			DungeonLayer layer = values.get(i);
			registerLayer(layerKey, layer);
			registerLayerData(layerKey, layer);
		}
	}

	public final void registerLayerData(String key, DungeonLayer layer) {
		if (layer instanceof BiomeLegacyLayer l) {
			register(key, l.getRegistryList(), this::registerLegacyBiome);
		}
		if (layer instanceof BiomeLayer l) {
			register(key, l.getRegistryList(), this::registerBiome);
		}
		if (layer instanceof FeatureLayer l) {
			register(key, l.getRegistryList(), this::registerFeature);
		}
		if (layer instanceof RoomLayer l) {
			register(key, l.getRegistryList(), this::registerRoom);
		}
	}

	private <T> void register(String parentKey, List<DungeonRegistryObject> values, BiConsumer<String, T> register) {
		int stringSize = String.valueOf(values.size()).length();
		for (int i = 0; i < values.size(); i++) {
			T value = values.get(i).getObject();
			if (value == null) {
				continue;
			}
			String rawIndex = String.valueOf(i);

			StringBuilder builder = new StringBuilder(parentKey).append(PREFIX);
			int zeroSize = stringSize - rawIndex.length();
			builder.append("0".repeat(Math.max(0, zeroSize)));
			builder.append(rawIndex);
			builder.append(SUFFIX);

			register.accept(builder.toString(), value);
		}
	}

	public final void toReference() {
		for (Entry<String, DGFactory> dungeonEntry : this.DUNGEONS.entrySet()) {
			DGFactory dungeon = dungeonEntry.getValue();

			List<DungeonLayer> values = dungeon.getLayers();
			for (int i = 0; i < values.size(); i++) {
				DungeonLayer layer = values.get(i);
				while (layer instanceof ReferenceLayer) {
					layer = layer.createGenerator(null);
				}
				String layerKey = layerKey(dungeon, i);

				if (layer instanceof BiomeLegacyLayer l) {
					toReference(dungeon, layer, layerKey, l.getRegistryList(), this::registerLegacyBiome);
				}
				if (layer instanceof BiomeLayer l) {
					toReference(dungeon, layer, layerKey, l.getRegistryList(), this::registerBiome);
				}
				if (layer instanceof FeatureLayer l) {
					toReference(dungeon, layer, layerKey, l.getRegistryList(), this::registerFeature);
				}
				if (layer instanceof RoomLayer l) {
					toReference(dungeon, layer, layerKey, l.getRegistryList(), this::registerRoom);
				}

				values.set(i, new ReferenceLayer(new DungeonKeySupplier(layerKey(dungeon, i)), layer));
				registerLayer(layerKey, layer);
			}

			dungeon.applyRegistryContext(this);
		}
	}

	private <T> void toReference(DGFactory dungeon, DungeonLayer layer, String layerKey, List<DungeonRegistryObject> values, BiConsumer<String, T> register) {
		for (int i = 0; i < values.size(); i++) {
			DungeonRegistryObject entry = values.get(i);
			String objectKey = objectKey(dungeon, layer, layerKey, values, i);
			if (entry.keyName() != null && entry.keyName().get().equals(objectKey)) {
				continue;
			}

			T value = entry.getObject();

			values.set(i, new DungeonRegistryObject(new DungeonKeySupplier(objectKey), value));
			register.accept(objectKey, value);
		}
	}

	@Override
	public String toString() {
		return SosisonUtils.toJson(this);
	}

	static class JLayerCodec extends BuiltinCodecFactory.MapCodec {

		public JLayerCodec(Type tClass, CodecRegistry registry) {
			super(tClass, new UniversalCodec[]{registry.getCodecIndirect(String.class), LAYER_CODEC}, HashMap::new, registry);
		}

	}

	private static final DungeonRegistryObject.JCodec LAYER_CODEC = new DungeonRegistryObject.JCodec(DungeonLayer.class, SosisonUtils.getFancyRegistry());

}
