package net.w3e.wlib.dungeon.layers.terra.biome;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.skds.lib2.io.codec.annotation.SkipSerialization;
import net.w3e.wlib.dungeon.room.DungeonRoomBaseData;
import net.w3e.wlib.dungeon.room.DungeonRoomData;

@NoArgsConstructor
public class BiomeDistance extends DungeonRoomBaseData {

	public static final BiomeDistance NULL = new BiomeDistanceNull();

	@Getter
	@Setter
	@SkipSerialization(defaultFloat = Float.POSITIVE_INFINITY)
	private float distance = Float.POSITIVE_INFINITY;

	@Getter
	@Setter(AccessLevel.PROTECTED)
	@SkipSerialization
	private boolean normalized = false;

	public BiomeDistance(DungeonRoomData data) {
		this.copyFrom(data);
		this.distance = data.getDistance();
		normalize(false);
	}

	public float distanceTo(BiomeDistance other) {
		return (float) Math.sqrt(squareDistanceTo(other));
	}

	public float squareDistanceTo(BiomeDistance other) {
		float d = 0;

		b1:{
			final float a = this.getTemperature();
			if (a == Float.POSITIVE_INFINITY) {
				break b1;
			}
			final float b = other.getTemperature();
			if (b == Float.POSITIVE_INFINITY) {
				break b1;
			}
			final float c = a - b;
			d += c * c;
		}

		b1:{
			final float a = this.getWet();
			if (a == Float.POSITIVE_INFINITY) {
				break b1;
			}
			final float b = other.getWet();
			if (b == Float.POSITIVE_INFINITY) {
				break b1;
			}
			final float c = a - b;
			d += c * c;
		}

		b1:{
			final float a = this.getVariant();
			if (a == Float.POSITIVE_INFINITY) {
				break b1;
			}
			final float b = other.getVariant();
			if (b == Float.POSITIVE_INFINITY) {
				break b1;
			}
			final float c = a - b;
			d += c * c;
		}

		b1:{
			final float a = this.getDifficulty();
			if (a == Float.POSITIVE_INFINITY) {
				break b1;
			}
			final float b = other.getDifficulty();
			if (b == Float.POSITIVE_INFINITY) {
				break b1;
			}
			final float c = a - b;
			d += c * c;
		}

		b1:{
			final float a = this.getDistance();
			if (a == Float.POSITIVE_INFINITY) {
				break b1;
			}
			final float b = other.getDistance();
			if (b == Float.POSITIVE_INFINITY) {
				break b1;
			}
			final float c = a - b;
			d += c * c;
		}

		return d;
	}

	public BiomeDistance getOrCreateNormalized() {
		return normalize(true);
	}

	private BiomeDistance normalize(boolean create) {
		if (this.normalized) {
			return this;
		}
		BiomeDistance norm = create ? new BiomeDistance() : this;
		norm.normalized = true;
		norm.setTemperature((this.getTemperature() + 100) / 200);
		norm.setWet(this.getWet() / 100);
		norm.setVariant(this.getVariant() / 100);
		norm.setDifficulty(this.getDifficulty() / 100);
		norm.setDistance(this.getDistance() / 50);
		return norm;
	}

	public BiomeDistance copyFrom(BiomeDistance data) {
		super.copyFrom(data);
		this.distance = data.distance;
		this.normalized = data.normalized;
		return this;
	}

	@Override
	public BiomeDistance clone() {
		return new BiomeDistance().copyFrom(this);
	}

	private static class BiomeDistanceNull extends BiomeDistance {

		public BiomeDistanceNull() {
			this.setNormalized(true);
		}

		@Override
		public float distanceTo(BiomeDistance other) {
			return Float.POSITIVE_INFINITY;
		}

		@Override
		public float squareDistanceTo(BiomeDistance other) {
			return Float.POSITIVE_INFINITY;
		}

		@Override
		public BiomeDistance getOrCreateNormalized() {
			return this;
		}

		@Override
		public BiomeDistance copyFrom(BiomeDistance data) {
			return this;
		}

		@Override
		public BiomeDistance clone() {
			return this;
		}

		@Override
		public void setTemperature(float temperature) {
		}

		@Override
		public void setWet(float wet) {
		}

		@Override
		public void setVariant(float variant) {
		}

		@Override
		public void setDifficulty(float difficulty) {
		}

		@Override
		public void setDistance(float distance) {
		}
	}
}
