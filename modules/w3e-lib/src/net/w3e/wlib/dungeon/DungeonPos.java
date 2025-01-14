package net.w3e.wlib.dungeon;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Random;

import net.skds.lib2.io.json.JsonPostDeserializeCall;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Vec3I;
import net.w3e.wlib.dungeon.json.IDungeonJsonAdapter;

public record DungeonPos(Vec3I pos, Direction direction, boolean enterance) {
	public static final DungeonPos EMPTY_POS = new DungeonPos();
	public static final DungeonPos EMPTY_ENTERANCE = new DungeonPos(Vec3I.ZERO, true);

	public DungeonPos() {
		this(Vec3I.ZERO);
	}

	public DungeonPos(Vec3I pos) {
		this(pos, false);
	}

	public DungeonPos(Vec3I pos, boolean enterance) {
		this(pos, null, enterance);
	}

	@Deprecated
	public final Direction direction() {
		return this.direction;
	}

	public final Direction getDirection(Random random) {
		if (this.direction != null) {
			return this.direction;
		}
		return switch (random.nextInt(4)) {
			case 0 -> Direction.EAST;
			case 1 -> Direction.NORTH;
			case 2 -> Direction.SOUTH;
			case 3 -> Direction.WEST;
			default -> null;
		};
	}

	public static class DungeonPosEnteranceCodec extends AbstractJsonCodec<DungeonPos> {

		private final JsonCodec<Vec3I> posCodec;
		private final JsonCodec<DungeonCenterPos> reader;

		public DungeonPosEnteranceCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
			this.posCodec = registry.getCodec(Vec3I.class);
			this.reader = registry.getCodecIndirect(DungeonCenterPos.class);
		}

		@Override
		public void write(DungeonPos value, JsonWriter writer) throws IOException {
			writer.beginObject();
			Direction direction = value.direction();
			if (direction != null) {
				writer.writeString("direction", direction.getName());
			}
			if (!value.pos().equals(Vec3I.ZERO)) {
				writer.writeName("pos");
				this.posCodec.write(value.pos(), writer);
			}
			writer.endObject();
		}

		@Override
		public DungeonPos read(JsonReader reader) throws IOException {
			DungeonCenterPos data = this.reader.read(reader);
			return new DungeonPos(data.pos, data.direction, true);
		}

		private static class DungeonCenterPos implements JsonPostDeserializeCall, IDungeonJsonAdapter {
			public Vec3I pos = Vec3I.ZERO;
			public Direction direction = null;
			@Override
			public void postDeserializedJson() {
				this.nonNull("pos", this.pos);
			}
		}
	}
}
