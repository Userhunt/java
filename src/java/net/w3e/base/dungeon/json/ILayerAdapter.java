package net.w3e.base.dungeon.json;

import com.google.gson.JsonSyntaxException;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.json.adapters.WTypedJsonAdapter.WJsonKeyHolder;
import net.w3e.base.message.MessageUtil;

public interface ILayerAdapter<T> extends WJsonKeyHolder {
	T withDungeon(DungeonGenerator generator);

	@SuppressWarnings("unchecked")
	default <VALUE> VALUE setTypeKey(String key) {
		T with = this.withDungeon(null);
		if (with instanceof WJsonKeyHolder holder) {
			return holder.setTypeKey(key);
		}
		return (VALUE)with;
	}

	default void nonNull(String msg, Object obj) {
		if (obj == null) throw new NullPointerException(msg);
	}

	default void lessThan(String msg, int value) {
		this.lessThan(msg, value, 0);
	}
	default void lessThan(String msg, int value, int min) {
		if (value <= min) {
			throw new JsonSyntaxException(MessageUtil.LESS_THAN.createMsg(msg, value, min + 1));
		}
	}

	default void lessThan(String msg, float value) {
		this.lessThan(msg, value, 0);
	}
	default void lessThan(String msg, float value, float min) {
		if (value <= min) {
			throw new JsonSyntaxException(MessageUtil.LESS_THAN.createMsg(msg, value, min + 0.01f));
		}
	}

	default <A> void isEmpty(String msg, @SuppressWarnings("unchecked") A... array) {
		this.nonNull(msg, array);
		if (array.length == 0) {
			throw new JsonSyntaxException(MessageUtil.IS_EMPTY.createMsg(msg));
		}
	}
}
