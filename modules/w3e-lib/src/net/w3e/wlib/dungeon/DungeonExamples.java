package net.w3e.wlib.dungeon;

import java.util.*;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.vec3.Direction;
import net.w3e.lib.TFNStateEnum;
import net.w3e.wlib.dungeon.direction.ConnectionState;
import net.w3e.wlib.dungeon.direction.DungeonChances;
import net.w3e.wlib.dungeon.filters.RoomFilterDistance;
import net.w3e.wlib.dungeon.filters.RoomFilterTemp;
import net.w3e.wlib.dungeon.filters.RoomFilterWet;
import net.w3e.wlib.dungeon.filters.RoomFilters;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.ClearLayer;
import net.w3e.wlib.dungeon.layers.DistanceLayer;
import net.w3e.wlib.dungeon.layers.FeatureLayer;
import net.w3e.wlib.dungeon.layers.FeatureLayer.FeatureVariant;
import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.layers.RoomLayer;
import net.w3e.wlib.dungeon.layers.RoomLayer.RoomVariant;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.layers.path.PathRepeatLayer;
import net.w3e.wlib.dungeon.layers.path.WormLayer;
import net.w3e.wlib.dungeon.layers.path.WormLayer.WormDungeonStepChances;
import net.w3e.wlib.dungeon.layers.path.lab.LabDFSLayer;
import net.w3e.wlib.dungeon.layers.path.lab.LabHAKLayer;
import net.w3e.wlib.dungeon.layers.terra.biome.BiomeDistance;
import net.w3e.wlib.dungeon.layers.terra.biome.BiomeLayer;
import net.w3e.wlib.dungeon.layers.terra.biome.BiomeLegacyLayer;
import net.w3e.wlib.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.wlib.dungeon.layers.terra.noise.DifficultyLayer;
import net.w3e.wlib.dungeon.layers.terra.noise.NoiseData;
import net.w3e.wlib.dungeon.layers.terra.noise.TemperatureLayer;
import net.w3e.wlib.dungeon.layers.terra.noise.VariantTerraLayer;
import net.w3e.wlib.dungeon.layers.terra.noise.WetLayer;
import net.w3e.wlib.dungeon.registry.DungeonRegistryObject;
import net.w3e.wlib.dungeon.room.DungeonPos;

public class DungeonExamples {
	
	public static final CompositeTerraLayer compositeTerraLayerExample() {
		return new CompositeTerraLayer(null, 50, true, temperatureLayerExample(), wetLayerExample(), difficultyLayerExample(), terraVariantLayerExample());
	}

	public static final int TEMPERATURE_MIN = -25;
	public static final int TEMPERATURE_MAX = 35;

	public static final TemperatureLayer temperatureLayerExample() {
		return new TemperatureLayer(null, new NoiseData.NoiseDataBuilder().setMinMax(TEMPERATURE_MIN, TEMPERATURE_MAX).generateDefValue().build(), 50, true);
	}

	// TODO change
	public static final int WET_MIN = 15;
	public static final int WET_MAX = 90;

	public static final WetLayer wetLayerExample() {
		return new WetLayer(null, new NoiseData.NoiseDataBuilder().setMinMax(WET_MIN, WET_MAX).build(), 50, true);
	}

	public static final int DIFFICULTY_MIN = 0;
	public static final int DIFFICULTY_MAX = 100;

	public static final DifficultyLayer difficultyLayerExample() {
		return new DifficultyLayer(null, new NoiseData.NoiseDataBuilder().setMinMax(DIFFICULTY_MIN, DIFFICULTY_MAX).build(), 50, 0, 1, true);
	}

	public static final int VARIANT_MIN = 0;
	public static final int VARIANT_MAX = 100;

	public static final VariantTerraLayer terraVariantLayerExample() {
		return new VariantTerraLayer(null, new NoiseData.NoiseDataBuilder().setMinMax(VARIANT_MIN, VARIANT_MAX).generateDefValue().build(), 50, true);
	}

	public static final BiomeLegacyLayer biomeLegacyLayerExample() {
		Random random = new Random(0);
		int range = FastMath.round((TEMPERATURE_MAX - TEMPERATURE_MIN) / 5f);
		List<BiomeLegacyLayer.BiomeInfo> biomes = new ArrayList<>(); 
		for (int i = 0; i < range; i++) {
			for (int j = 1; j < 3; j++) {
				int name = i * 5 + TEMPERATURE_MIN;

				int minTemp = name;
				int maxTemp = minTemp + 5;
				int weight = random.nextInt(10) + 5;
				minTemp += random.nextInt(10) - 9;
				maxTemp += random.nextInt(10) - 5;

				int minImpulse = random.nextInt(4);
				int maxImpulse = random.nextInt(6);

				RoomFilters filters = new RoomFilters();

				filters.add(new RoomFilterTemp(new LayerRange(minTemp, maxTemp)));

				if (random.nextBoolean()) {
					int minWet = random.nextInt(25);
					int maxWet = random.nextInt(50) + 51;
					filters.add(new RoomFilterWet(new LayerRange(minWet, maxWet)));
				}

				float chanceSpread = random.nextFloat() / 4;

				String key = String.format("%s (%s)", name, j);

				BiomeLegacyLayer.BiomeInfo biome = new BiomeLegacyLayer.BiomeInfo(new DungeonKeySupplier(key), weight, filters, new LayerRange(minImpulse, maxImpulse), chanceSpread, new DungeonInfoCountHolder(1 + random.nextInt(3)));

				biomes.add(biome);
			}
		}

		biomes.removeIf(BiomeLegacyLayer.BiomeInfo::notValid);

		return new BiomeLegacyLayer(null, biomes.stream().map(DungeonRegistryObject::new).toList(), new DungeonKeySupplier("void"), 11);
	}

	public static final BiomeLayer biomeLayerExample() {
		Random random = new Random(0);
		int range = FastMath.round((TEMPERATURE_MAX - TEMPERATURE_MIN) / 5f);
		List<BiomeLayer.BiomeInfo> biomes = new ArrayList<>(); 
		for (int i = 0; i < range; i++) {
			for (int j = 1; j < 5; j++) {
				String key = String.format("%s (%s)", (i * 5 + TEMPERATURE_MIN), j);

				float temp = random.nextFloat() * (TEMPERATURE_MAX - TEMPERATURE_MIN) + TEMPERATURE_MIN;
				float wet = random.nextFloat() * (WET_MAX - WET_MIN) + WET_MIN;
				float variant = random.nextFloat() * (VARIANT_MIN - VARIANT_MAX) + VARIANT_MIN;

				BiomeDistance params = new BiomeDistance();
				params.setTemperature(temp);
				params.setWet(wet);
				params.setVariant(variant);

				BiomeLayer.BiomeInfo biome = new BiomeLayer.BiomeInfo(new DungeonKeySupplier(key), params, new DungeonInfoCountHolder(1 + random.nextInt(3)));

				biomes.add(biome);
			}
		}

		biomes.removeIf(BiomeLayer.BiomeInfo::notValid);

		return new BiomeLayer(null, biomes.stream().map(DungeonRegistryObject::new).toList(), new DungeonKeySupplier("void"));
	}

	public static final PathRepeatLayer<WormLayer> pathRepeatLayerExample(int size) {
		size -= 1;
		size *= 2;
		size += 1;
		size *= size;
		float min = 2205f / size;
		return new PathRepeatLayer<>(null, wormLayerExample(), min, 1);
	}

	public static final WormLayer wormLayerExample() {
		return new WormLayer(
			null,
			new DungeonPos[]{DungeonPos.EMPTY_entrance, DungeonPos.EMPTY_entrance}, 
			WormDungeonStepChances.INSTANCE, 
			new DungeonChances(20, 15, 10, 5, 0, 0, 1, 5, 0, 0), 
			new DungeonChances(10, 4, 4, 4, 0, 0, 1, 1, 0, 0)
		);
	}

	public static final DistanceLayer distanceLayerExample() {
		return new DistanceLayer(null);
	}

	public static final RoomLayer roomLayerExample() {
		Random random = new Random(0);
		List<RoomVariant> rooms = new ArrayList<>();
		List<Direction> dir = new ArrayList<>();
		RoomFilters filters = new RoomFilters();
		filters.add(new RoomFilterDistance(new LayerRange(5, Integer.MAX_VALUE)));
		for (Direction direction : Direction.values()) {
			if (direction != Direction.UP && direction != Direction.DOWN) {
				dir.add(direction);
			}
		}
		for (int i = 0; i < 150; i++) {
			LinkedHashMap<Direction, ConnectionState> directions = new LinkedHashMap<>();
			{
				int count = random.nextInt(4) + 1;
				if (count == 4) {
					for (Direction wDirection : dir) {
						directions.put(wDirection, ConnectionState.HARD);
					}
				} else {
					List<Direction> dirCopy = new ArrayList<>(dir);
					while (count > 0) {
						directions.put(dirCopy.remove(random.nextInt(dirCopy.size())), (random.nextInt(100) + 1 <= 75) ? ConnectionState.HARD : ConnectionState.SOFT);
						count--;
					}
				}
			}
			RoomFilters baseLayerRange = RoomFilters.NULL;
			int count;
			String name = String.valueOf(i + 1);
			if (random.nextInt(100) + 1 <= 5) {
				name += "_boss";
				count = 1;
				baseLayerRange = filters;
			} else {
				count = random.nextInt(100) + 1 <= 20 ? random.nextInt(5) + 1 : -1;
			}

			rooms.add(new RoomVariant(new DungeonKeySupplier(name), directions, baseLayerRange, false, new DungeonInfoCountHolder(count)));
		}

		/*boolean print = false;
		if (print) {
			DungeonGenerator.LOGGER.debug("===============");
			for (RoomVariant info : rooms) {
				DungeonGenerator.LOGGER.debug(info);
			}
			DungeonGenerator.LOGGER.debug("");
		}*/

		Set<Set<Direction>> filter = new HashSet<>();

		for (Direction d1 : dir) {
			Set<Direction> s1 = new HashSet<>();
			s1.add(d1);
			if (filter.add(s1)) {
				rooms.add(roomLayerExampleEntrance(s1));
				for (Direction d2 : dir) {
					Set<Direction> s2 = new HashSet<>(s1);
					s2.add(d2);
					if (filter.add(s2)) {
						rooms.add(roomLayerExampleEntrance(s2));
						for (Direction d3 : dir) {
							Set<Direction> s3 = new HashSet<>(s2);
							s3.add(d3);
							if (filter.add(s3)) {
								rooms.add(roomLayerExampleEntrance(s3));
								for (Direction d4 : dir) {
									Set<Direction> s4 = new HashSet<>(s3);
									s4.add(d4);
									if (filter.add(s4)) {
										rooms.add(roomLayerExampleEntrance(s4));
									}
								}
							}
						}
					}
				}
			}
		}

		/*if (print) {
			for (int i = 1; i <= 4; i++) {
				StringBuilder builder = new StringBuilder();
				for (Set<Direction> set : filter) {
					if (set.size() == i) {
						builder.append(set);
					}
				}
				DungeonGenerator.LOGGER.debug(builder);
			}
			DungeonGenerator.LOGGER.debug("===============");
		}*/

		return new RoomLayer(null, rooms.stream().map(DungeonRegistryObject::new).toList(), 50);
	}

	private static RoomVariant roomLayerExampleEntrance(Set<Direction> direction) {
		LinkedHashMap<Direction, ConnectionState> directions = new LinkedHashMap<>();
		for (Direction d : direction) {
			directions.put(d, ConnectionState.HARD);
		}
		return new RoomVariant(new DungeonKeySupplier("center"), directions, RoomFilters.NULL, true, DungeonInfoCountHolder.NULL);
	}

	public static final FeatureLayer featureLayerExample() {
		List<FeatureVariant> features = new ArrayList<>();
		Random random = new Random(0);
		for (int i = 0; i < 20; i++) {
			String name = String.valueOf(i + 1);
			features.add(new FeatureVariant(new DungeonKeySupplier(name), RoomFilters.NULL, random.nextInt(100) + 1 <= 5 ? TFNStateEnum.TRUE : TFNStateEnum.FALSE, random.nextInt(100) + 1 <= 70, new DungeonInfoCountHolder(random.nextInt(100) + 1 <= 75 ? random.nextInt(3) + 1 : -1)));
		}

		return new FeatureLayer(null, features.stream().map(DungeonRegistryObject::new).toList());
	}

	public static final ClearLayer clearLayerExample() {
		return new ClearLayer(null);
	}

	public static final LabHAKLayer labHAKLayerExample() {
		return new LabHAKLayer(null, 3, new DungeonChances(10, 4, 4, 4, 0, 0, 1, 1, 0, 0));
	}

	public static final LabDFSLayer labDFSLayerExample() {
		return new LabDFSLayer(null, 3, new DungeonChances(10, 4, 4, 4, 0, 0, 1, 1, 0, 0));
	}

}
