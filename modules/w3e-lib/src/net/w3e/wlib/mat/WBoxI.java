package net.w3e.wlib.mat;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.DeserializeBuilder;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3I;

@DefaultCodec(WBoxI.WBoxIJsonAdapter.class)
public class WBoxI {

	private int minX, minY, minZ;
	private int maxX, maxY, maxZ;

	public WBoxI(int x, int y, int z) {
		this(x, y, z, x, y, z);
	}

	public WBoxI(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.set(x1, y1, z1, x2, y2, z2);
	}

	public final void set(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.minX = Math.min(x1, x2);
		this.minY = Math.min(y1, y2);
		this.minZ = Math.min(z1, z2);
		this.maxX = Math.max(x1, x2);
		this.maxY = Math.max(y1, y2);
		this.maxZ = Math.max(z1, z2);
	}

	public final WBoxI expand(int value) {
		return this.expand(value, value, value);
	}

	public final WBoxI expand(int x, int y, int z) {
		return this.modify(x, y, z);
	}

	public final WBoxI inflate(int value) {
		return this.inflate(value, value, value);
	}

	public final WBoxI inflate(int x, int y, int z) {
		return this.modify(x * -1, y * -1, z * -1);
	}

	public final WBoxI offset(int x, int y, int z) {
		this.minX += x;
		this.minY += y;
		this.minZ += z;
		this.maxX += x;
		this.maxY += y;
		this.maxZ += z;
		return this;
	}

	public final WBoxI move(int x, int y, int z) {
		Vec3I center = this.center();
		return offset(x - center.xi(), y - center.yi(), z - center.zi());
	}

	public boolean contains(int x, int y, int z) {
		return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
	}

	public boolean contains(Vec3I vector) {
		return this.contains(vector.xi(), vector.yi(), vector.zi());
	}

	public final Vec3I size() {
		return new Vec3I(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
	}

	public final Vec3I min() {
		return new Vec3I(this.minX, this.minY, this.minZ);
	}

	public final Vec3I max() {
		return new Vec3I(this.maxX, this.maxY, this.maxZ);
	}

	public final Vec3I center() {
		return new Vec3I((this.minX + this.maxX) / 2, (this.minY + this.maxY) / 2, (this.minZ + this.maxZ) / 2);
	}

	public final WBoxI copy() {
		return new WBoxI(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	public static final WBoxI MAX() {
		int min = -10000;
		int max = 10000;
		return new WBoxI(min, min, min, max, max, max);
	}

	public static final WBoxI of(int x, int y, int z) {
		return new WBoxI(0, 0, 0, x, y, z);
	}

	public static final WBoxI of(Vec3... points) {
		return of(Arrays.asList(points));
	}

	public static final WBoxI of(Collection<? extends Vec3> points) {
		WBoxI box;
		if (points.isEmpty()) {
			box = WBoxI.of(0, 0, 0);
		} else {
			Vec3 point = points.iterator().next();
			box = new WBoxI(point.xi(), point.yi(), point.zi());
		}
		for (Vec3 point : points) {
			box.minX = Math.min(box.minX, point.xi());
			box.minY = Math.min(box.minY, point.yi());
			box.minZ = Math.min(box.minZ, point.zi());
			box.maxX = Math.max(box.maxX, point.xi());
			box.maxY = Math.max(box.maxY, point.yi());
			box.maxZ = Math.max(box.maxZ, point.zi());
		}
		return box;
	}

	public final void rotate(Direction rotation) {
		this.rotate(Direction.SOUTH, rotation);
	}

	public final void rotate(Direction base, Direction rotation) {
		Vec3I min = VecUtil.rotateI(this.min(), base, rotation);
		Vec3I max = VecUtil.rotateI(this.max(), base, rotation);
		this.set(min.xi(), min.yi(), min.zi(), max.xi(), max.yi(), max.zi());
	}

	private final WBoxI modify(int x, int y, int z) {
		return this.modifyX(x).modifyY(y).modifyZ(z);
	}

	private final WBoxI modifyX(int value) {
		int min = this.minX;
		int max = this.maxX;
		this.minX -= value;
		this.maxX += value;
		if (this.minX > this.maxX) {
			int v = (min + max) / 2;
			this.minX = v;
			if (min + max % 2 == 0) {
				this.maxX = v;
			} else {
				this.maxX = v + 1;
			}
		}
		return this;
	}

	private final WBoxI modifyY(int value) {
		int min = this.minY;
		int max = this.maxY;
		this.minY -= value;
		this.maxY += value;
		if (this.minY > this.maxY) {
			int v = (min + max) / 2;
			this.minY = v;
			if (min + max % 2 == 0) {
				this.maxY = v;
			} else {
				this.maxY = v + 1;
			}
		}
		return this;
	}

	private final WBoxI modifyZ(int value) {
		int min = this.minZ;
		int max = this.maxZ;
		this.minZ -= value;
		this.maxZ += value;
		if (this.minZ > this.maxZ) {
			int v = (min + max) / 2;
			this.minZ = v;
			if (min + max % 2 == 0) {
				this.maxZ = v;
			} else {
				this.maxZ = v + 1;
			}
		}
		return this;
	}

	@Override
	public final String toString() {
		return String.format("{min:[%s,%s,%s],max:[%s,%s,%s]}", this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	static class WBoxIJsonAdapter extends ReflectiveBuilderCodec<WBoxI> {

		public WBoxIJsonAdapter(Type type, CodecRegistry registry) {
			super(type, WBoxIData.class, registry);
		}

		private static class WBoxIData implements DeserializeBuilder<WBoxI> {
			private int minX, minY, minZ;
			private int maxX, maxY, maxZ;
			@Override
			public WBoxI build() {
				return new WBoxI(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
			}
		}
	}
}
