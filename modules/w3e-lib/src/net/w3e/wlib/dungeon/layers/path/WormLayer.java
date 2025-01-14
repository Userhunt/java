package net.w3e.wlib.dungeon.layers.path;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.io.json.codec.BuiltinCodecFactory.ArrayCodec;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Vec3I;
import net.w3e.wlib.collection.CollectionUtils;
import net.w3e.wlib.collection.ArraySet.ArraySetStrict;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonPos;
import net.w3e.wlib.dungeon.DungeonPos.DungeonPosentranceCodec;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.DungeonLayer.IPathLayer;
import net.w3e.wlib.dungeon.direction.DungeonChances;
import net.w3e.wlib.dungeon.json.ILayerData;

@DefaultJsonCodec(WormLayer.WormLayerDataJsonAdapter.class)
public class WormLayer extends DungeonLayer implements IPathLayer {

	public static final String TYPE = "path/worm";

	@DefaultJsonCodec(WormLayer.DungeonPosentranceFieldCodec.class)
	public final DungeonPos[] centers;
	public final WormDungeonStepChances stepChances;
	public final DungeonChances directionChances;
	public final DungeonChances connectionChances;
	private final transient List<DungeonPos>[] entries = CollectionUtils.createArrayOfList(DungeonPos.class, 2);

	public WormLayer(DungeonGenerator generator, DungeonPos[] centers, WormDungeonStepChances stepChances, DungeonChances directionChances, DungeonChances connectionChances) {
		super(TYPE, generator);
		this.centers = centers;
		this.stepChances = stepChances;
		this.directionChances = directionChances;
		this.connectionChances = connectionChances;
	}

	@Override
	public final WormLayer withDungeon(DungeonGenerator generator) {
		return new WormLayer(generator, this.centers, this.stepChances, this.directionChances, this.connectionChances);
	}

	@Override
	public final void regenerate(boolean composite) throws DungeonException {
		this.entries[0] = new ArraySetStrict<>();
		this.entries[1] = new ArraySetStrict<>();
		for(DungeonPos pos : this.centers) {
			this.add(pos);
		}
	}

	private final void add(DungeonPos pos) throws DungeonException {
		this.add(pos.pos(), pos.getDirection(this.random()), true);
	}

	@Override
	public final void add(Vec3I pos, Direction direction) throws DungeonException {
		this.add(pos, direction, false);
	}

	private final void add(Vec3I pos, Direction direction, boolean entrance) throws DungeonException {
		this.assertInside(pos);
		this.entries[1].add(new DungeonPos(pos, direction, entrance));
	}

	@Override
	public final int generate() throws DungeonException {
		this.entries[0] = this.entries[1];
		this.entries[1] = new ArraySetStrict<>();
		for (DungeonPos entry : this.entries[0]) {
			Vec3I pos = entry.pos();

			int d = this.stepChances.get(this.random());
			Direction direction = entry.getDirection(this.random());
			Collection<Direction> connections = new ArrayList<>();
			connections.add(direction);
			boolean success = true;

			DungeonRoomCreateInfo info = this.putOrGet(pos);
			DungeonRoomInfo room = info.room();
			room.setWall(false);
			if (entry.entrance()) {
				info.room().setentrance(true);
				if (!this.get(pos.addI(direction)).notExistsOrWall()) {
					Direction old = direction;
					entry = new DungeonPos(pos, null, true);
					while((direction = entry.getDirection(this.random())) == old) {}
					connections.clear();
					connections.add(direction);
				}
			}
			room.setConnections(connections, true, true);
			if (info.isInside() && info.notExistsOrWall()) {
				room.setConnections(connections, true, true);
			}
			connections.add(direction.getOpposite());
			while(d > 0) {
				d--;
				if (d == 0) {
					connections.remove(direction);
				}
				pos = pos.addI(direction);
				info = this.putOrGet(pos);
				if (info.isInside()) {
					room = info.room();
					room.setWall(false);
					room.setConnections(this.connectionChances.generate(this.random(), direction), true, false);
					success = info.isWall();
					info.room().setConnections(connections, true, true);
				} else {
					room.setConnection(direction, false, false);
					success = false;
					break;
				}
			}
			if (success) {
				connections = this.directionChances.generate(this.random(), direction);
				room = info.room();
				room.setConnections(connections, true, false);
				for (Direction connection : connections) {
					if (connection == Direction.UP || connection == Direction.DOWN) {
						this.add(room.pos().addI(connection), null);
					} else {
						this.add(room.pos(), connection);
					}
				}
			}
		}
		return Math.max(10 - this.entries[1].size(), 0) * 10;
	}

	public static record WormDungeonStepChances(int min, int max) {
		public static final WormDungeonStepChances INSTANCE = new WormDungeonStepChances(1, 3);
		public final int get(Random random) {
			int step = this.min < 1 ? 1 : this.min;
			if (this.min < this.max && this.min > 0) {
				step = random.nextInt(this.max - this.min + 1) + this.min;
			}
			return step;
		}
	}

	static class WormLayerDataJsonAdapter extends JsonReflectiveBuilderCodec<WormLayerDataJsonAdapter.WormLayerData> {

		public WormLayerDataJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, WormLayerData.class, registry);
		}

		private static class WormLayerData implements ILayerData<WormLayer> {

			private static final DungeonPos[] CENTERS = new DungeonPos[]{DungeonPos.EMPTY_POS};
	
			@DefaultJsonCodec(DungeonPosentranceFieldCodec.class)
			private DungeonPos[] centers = CENTERS;
			private WormDungeonStepChances stepChances = WormDungeonStepChances.INSTANCE;
			private DungeonChances directionChances = DungeonChances.INSTANCE;
			private DungeonChances connectionChances = DungeonChances.INSTANCE;
	
			@Override
			public final WormLayer withDungeon(DungeonGenerator generator) {
				this.nonNull("centers", this.centers);
				this.nonNull("stepChance", this.stepChances);
				this.nonNull("directionChances", this.directionChances);
				this.nonNull("connectionChances", this.connectionChances);
				this.isEmpty("centers", this.centers);
				return new WormLayer(generator, this.centers, this.stepChances, this.directionChances, this.connectionChances);
			}
		}
	}

	private static class DungeonPosentranceFieldCodec extends AbstractJsonCodec<DungeonPos[]> {

		private final JsonCodec<DungeonPos> codec;

		public DungeonPosentranceFieldCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
			this.codec = new DungeonPosentranceCodec(this.codecType, this.registry);
		}

		@Override
		public void write(DungeonPos[] value, JsonWriter writer) throws IOException {
			ArrayCodec.write(value, writer, this.codec);
		}

		@Override
		public DungeonPos[] read(JsonReader reader) throws IOException {
			return ArrayCodec.read(new DungeonPos[0], reader, this.codec);
		}
	}

	public static final WormLayer example(DungeonGenerator generator) {
		return new WormLayer(
			generator,
			new DungeonPos[]{DungeonPos.EMPTY_entrance, DungeonPos.EMPTY_entrance}, 
			WormDungeonStepChances.INSTANCE, 
			new DungeonChances(20, 15, 10, 5, 0, 0, 1, 5, 0, 0), 
			new DungeonChances(10, 4, 4, 4, 0, 0, 1, 1, 0, 0)
		);
	}
}
