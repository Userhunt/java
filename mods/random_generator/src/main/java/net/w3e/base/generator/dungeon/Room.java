package net.w3e.base.generator.dungeon;

import net.w3e.base.generator.GenRange;
import net.w3e.base.generator.PropertyType;
import net.w3e.base.generator.GenRange.ValueRangeBuilder;
import net.w3e.base.generator.property.GenProperty;

public class Room extends GenProperty {

	private final GenRange chance;

	public Room(String type, GenRange levelRange, GenRange weight, GenRange valueRange, GenRange chance, Boolean nerfLvl) {
		this(DungeonGenerator.ROOM_TYPES.get(type), levelRange, weight, valueRange, chance, nerfLvl);
	}

	protected Room(RoomType type, GenRange levelRange, GenRange weight, GenRange valueRange, GenRange chance, Boolean nerfLvl) {
		super(type, levelRange, weight, valueRange, nerfLvl, true);
		this.chance = chance;
	}

	@Override
	public final boolean required() {
		return this.chance == null;
	}

	@Override
	public final GenRange chance() {
		return this.chance;
	}

	public static class RoomBuilder extends AbstractGenBuilder<RoomBuilder> {

		protected GenRange chance;

		public RoomBuilder(String type) {
			super(DungeonGenerator.ROOM_TYPES.get(type));
			valueRange(GenRange.VALUE_1);
		}

		public RoomBuilder type(String type) {
			if (type != null) {
				return type(DungeonGenerator.ROOM_TYPES.get(type));
			}
			return this;
		}

		public RoomBuilder type(RoomType type) {
			if (type != null) {
				return super.type(type);
			}
			return this;
		}

		/**
		 * @deprecated - wrong type
		 * @param type
		 * @return
		 */
		@Override
		public RoomBuilder type(PropertyType type) {
			if (type instanceof RoomType room) {
				return type(room);
			}
			return this;
		}

		public RoomBuilder chance(GenRange chance) {
			this.chance = chance;
			return this;
		}

		public RoomBuilder required() {
			return this.required(true);
		}

		/**
		 * @deprecated - unused
		 * @param clampRandom
		 * @return
		 */
		@Override
		public RoomBuilder clampRandom(Boolean clampRandom) {
			return this;
		}

		public RoomBuilder required(boolean required) {
			if (required) {
				this.chance = null;
			} else if (this.chance == null) {
				this.chance = GenRange.EMPTY_CHANCE;
			}
			return this;
		}

		public Room build() {
			return new Room(this.type.getRegistryName(), this.levelRange, this.weight, new ValueRangeBuilder().read(this.valueRange).round(0).build(), this.chance, this.nerfLvl);
		}
	}
}
