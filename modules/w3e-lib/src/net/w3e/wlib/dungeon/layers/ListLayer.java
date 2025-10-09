package net.w3e.wlib.dungeon.layers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lombok.Getter;
import net.skds.lib2.io.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.log.LogUtil;

public abstract class ListLayer<E> extends DungeonLayer {

	public final transient List<E> list = new ArrayList<>();
	@Getter
	public transient int filled = -1;

	protected ListLayer(ConfigType<? extends ListLayer<E>> configType, DungeonGenerator generator) {
		super(configType, generator);
	}

	protected final void generateList(Function<DungeonRoomCreateInfo, GenerateListHolder<E>> filter) {
		this.generateList(filter, false);
	}

	public final void generateList(Function<DungeonRoomCreateInfo, GenerateListHolder<E>> filter, boolean createIfNotExists) {
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

	public static record GenerateListHolder<L>(L l, boolean add, boolean increase) {
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

	protected final <T> void copyList(List<T> list, Function<T, T> copy) {
		if (list.isEmpty()) {
			throw new DungeonException(LogUtil.IS_EMPTY.createMsg(this.getClass().getSimpleName()));
		}
		List<T> values = new ArrayList<>(list);
		list.clear();
		values.stream().map(copy).forEach(list::add);
	}

	protected final <S, D> void copyList(List<S> source, List<D> dest, Function<S, D> function) {
		if (source.isEmpty()) {
			throw new DungeonException(LogUtil.IS_EMPTY.createMsg(this.getClass().getSimpleName()));
		}
		dest.clear();
		source.stream().map(function).forEach(dest::add);
	}

	public final int size() {
		return this.filled - this.list.size();
	}

	public final float progress() {
		return this.size() * 1f / this.filled;
	}

}
