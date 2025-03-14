package net.w3e.wlib.dungeon;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Random;

import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.json.WJsonBuilder;

public record DungeonPos(Vec3I pos, Direction direction, boolean entrance) {
	public static final DungeonPos EMPTY_POS = new DungeonPos();
	public static final DungeonPos EMPTY_entrance = new DungeonPos(Vec3I.ZERO, true);

	public DungeonPos() {
		this(Vec3I.ZERO);
	}

	public DungeonPos(Vec3I pos) {
		this(pos, false);
	}

	public DungeonPos(Vec3I pos, boolean entrance) {
		this(pos, null, entrance);
	}

	@Deprecated
	public Direction direction() {
		return this.direction;
	}

	public Direction getDirection(Random random) {
		return getDirectionOrRandom(this.direction, random);
	}

	public static Direction getDirectionOrRandom(Direction direction, Random random) {
		if (direction != null) {
			return direction;
		}
		return switch (random.nextInt(4)) {
			case 0 -> Direction.EAST;
			case 1 -> Direction.NORTH;
			case 2 -> Direction.SOUTH;
			case 3 -> Direction.WEST;
			default -> null;
		};
	}

	public static class DungeonPosentranceCodec extends JsonReflectiveBuilderCodec<DungeonPos> {

		private final JsonCodec<Vec3I> posCodec;

		public DungeonPosentranceCodec(Type type, JsonCodecRegistry registry) {
			super(type, DungeonCenterPos.class, registry);
			this.posCodec = registry.getCodec(Vec3I.class);
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

		private static class DungeonCenterPos implements WJsonBuilder<DungeonPos> {
			public Vec3I pos = Vec3I.ZERO;
			public Direction direction = null;

			@Override
			public final DungeonPos build() {
				this.nonNull("pos", this.pos);
				return new DungeonPos(this.pos, this.direction, true);
			}
		}
	}
}
