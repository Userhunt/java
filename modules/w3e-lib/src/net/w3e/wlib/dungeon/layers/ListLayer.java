package net.w3e.wlib.dungeon.layers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.skds.lib2.mat.FastMath;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.json.WJsonTypedTypeAdapter;

public abstract class ListLayer<E> extends DungeonLayer {

	protected final transient List<E> list = new ArrayList<>();
	protected transient int filled = -1;

	protected ListLayer(WJsonTypedTypeAdapter<? extends ListLayer<E>> keyName, DungeonGenerator generator) {
		super(keyName, generator);
	}

	@Override
	public void regenerate(boolean composite) throws DungeonException {
		this.filled = -1;
		this.list.clear();
	}

	protected final void generateList(Function<DungeonRoomCreateInfo, GenerateListHolder<E>> filter) {
		this.generateList(filter, false);
	}

	protected final void generateList(Function<DungeonRoomCreateInfo, GenerateListHolder<E>> filter, boolean createIfNotExists) {
		this.filled = 0;
		this.forEach(room -> {
			GenerateListHolder<E> holder = filter.apply(room);
			if (holder.add) {
				this.list.add(holder.l);
			}
			if (holder.increase) {
				this.filled++;
			}
		}, createIfNotExists);
	}

	protected static record GenerateListHolder<L>(L l, boolean add, boolean increase) {
		public static final <L> GenerateListHolder<L> fail() {
			return new GenerateListHolder<L>(null, false, false);
		}
		public static final <L> GenerateListHolder<L> success(L value) {
			return new GenerateListHolder<L>(value, true, true);
		}
		public static final <L> GenerateListHolder<L> increase(L value) {
			return new GenerateListHolder<L>(value, false, true);
		}
	}

	public final int size() {
		return this.filled - this.list.size();
	}

	public final int filled() {
		return this.filled;
	}

	public final int progress() {
		return FastMath.round(this.size() * 100f / this.filled);
	}
}
