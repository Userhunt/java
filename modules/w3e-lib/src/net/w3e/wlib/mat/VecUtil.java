package net.w3e.wlib.mat;

import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.mat.vec2.Vec2D;
import net.skds.lib2.mat.vec2.Vec2F;
import net.skds.lib2.mat.vec2.Vec2I;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.mat.vec3.Direction.Axis;

public class VecUtil {

	public static Vec3I chunk2Pos(Vec3I chunk) {
		if (chunk == null) {
			return Vec3I.ZERO;
		}
		return new Vec3I(chunk.xi() << 4, chunk.yi() << 4, chunk.zi() << 4);
	}

	public static Vec3I pos2Chunk(Vec3I pos) {
		if (pos == null) {
			return Vec3I.ZERO;
		}
		return new Vec3I(pos.xi() >> 4, pos.yi() >> 4, pos.zi() >> 4);
	}

	public static Vec2I chunk2Pos(Vec2I chunk) {
		if (chunk == null) {
			return Vec2I.ZERO;
		}
		return new Vec2I(chunk.xi() << 4, chunk.yi() << 4);
	}

	public static Vec2I pos2Chunk(Vec2I pos) {
		if (pos == null) {
			return Vec2I.ZERO;
		}
		return new Vec2I(pos.xi() >> 4, pos.yi() >> 4);
	}

	public static Vec3I rotateI(Vec3 vec, Direction rotation) {
		return rotateI(vec, Direction.SOUTH, rotation);
	}

	public static Vec3I rotateI(Vec3 vec, Direction base, Direction rotation) {
		if (base != null && rotation != null && base != rotation && base.isHorizontal() && rotation.isHorizontal()) {
			while (base != Direction.SOUTH) {
				base = base.rotateClockwise(Axis.Y);
				rotation = rotation.rotateClockwise(Axis.Y);
			}
			int x = vec.xi();
			int z = vec.zi();
			while (rotation != Direction.SOUTH) {
				rotation = rotation.rotateCounterclockwise(Axis.Y);
				int v = x;
				x = z;
				z = -v;
			}
			return new Vec3I(x, vec.yi(), z);
		}
		return vec.getAsIntVec();
	}

	public static Vec3F rotateF(Vec3 vec, Direction rotation) {
		return rotateF(vec, Direction.SOUTH, rotation);
	}

	public static Vec3F rotateF(Vec3 vec, Direction base, Direction rotation) {
		if (base != null && rotation != null && base != rotation && base.isHorizontal() && rotation.isHorizontal()) {
			while (base != Direction.SOUTH) {
				base = base.rotateClockwise(Axis.Y);
				rotation = rotation.rotateClockwise(Axis.Y);
			}
			float x = vec.xf();
			float z = vec.zf();
			while (rotation != Direction.SOUTH) {
				rotation = rotation.rotateCounterclockwise(Axis.Y);
				float v = x;
				x = z;
				z = -v;
			}
			return new Vec3F(x, vec.yf(), z);
		}
		return vec.getAsFloatVec();
	}

	public static Vec3D rotateD(Vec3 vec, Direction rotation) {
		return rotateD(vec, Direction.SOUTH, rotation);
	}

	public static Vec3D rotateD(Vec3 vec, Direction base, Direction rotation) {
		if (base != null && rotation != null && base != rotation && base.isHorizontal() && rotation.isHorizontal()) {
			while (base != Direction.SOUTH) {
				base = base.rotateClockwise(Axis.Y);
				rotation = rotation.rotateClockwise(Axis.Y);
			}
			double x = vec.x();
			double z = vec.z();
			while (rotation != Direction.SOUTH) {
				rotation = rotation.rotateCounterclockwise(Axis.Y);
				double v = x;
				x = z;
				z = -v;
			}
			return new Vec3D(x, vec.y(), z);
		}
		return vec.getAsDoubleVec();
	}


	public static Vec2I rotateI(Vec2 vec, Direction rotation) {
		return rotateI(vec, Direction.SOUTH, rotation);
	}

	public static Vec2I rotateI(Vec2 vec, Direction base, Direction rotation) {
		if (base != null && rotation != null && base != rotation && base.isHorizontal() && rotation.isHorizontal()) {
			while (base != Direction.SOUTH) {
				base = base.rotateClockwise(Axis.Y);
				rotation = rotation.rotateClockwise(Axis.Y);
			}
			int x = vec.xi();
			int z = vec.yi();
			while (rotation != Direction.SOUTH) {
				rotation = rotation.rotateCounterclockwise(Axis.Y);
				int v = x;
				x = z;
				z = -v;
			}
			return new Vec2I(x, z);
		}
		return vec.getAsIntVec();
	}

	public static Vec2F rotateF(Vec2 vec, Direction rotation) {
		return rotateF(vec, Direction.SOUTH, rotation);
	}

	public static Vec2F rotateF(Vec2 vec, Direction base, Direction rotation) {
		if (base != null && rotation != null && base != rotation && base.isHorizontal() && rotation.isHorizontal()) {
			while (base != Direction.SOUTH) {
				base = base.rotateClockwise(Axis.Y);
				rotation = rotation.rotateClockwise(Axis.Y);
			}
			float x = vec.xf();
			float z = vec.yf();
			while (rotation != Direction.SOUTH) {
				rotation = rotation.rotateCounterclockwise(Axis.Y);
				float v = x;
				x = z;
				z = -v;
			}
			return new Vec2F(x, z);
		}
		return vec.getAsFloatVec();
	}

	public static Vec2D rotateD(Vec2 vec, Direction rotation) {
		return rotateD(vec, Direction.SOUTH, rotation);
	}

	public static Vec2D rotateD(Vec2 vec, Direction base, Direction rotation) {
		if (base != null && rotation != null && base != rotation && base.isHorizontal() && rotation.isHorizontal()) {
			while (base != Direction.SOUTH) {
				base = base.rotateClockwise(Axis.Y);
				rotation = rotation.rotateClockwise(Axis.Y);
			}
			double x = vec.x();
			double z = vec.y();
			while (rotation != Direction.SOUTH) {
				rotation = rotation.rotateCounterclockwise(Axis.Y);
				double v = x;
				x = z;
				z = -v;
			}
			return new Vec2D(x, z);
		}
		return vec.getAsDoubleVec();
	}
}
