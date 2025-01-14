package net.w3e.wlib.dungeon.layers.filter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.skds.lib2.io.json.JsonPostDeserializeCall;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.BuiltinCodecFactory.ArrayCodec;
import net.skds.lib2.utils.ArrayUtils;

@NoArgsConstructor
@AllArgsConstructor
@DefaultJsonCodec(RoomLayerFilters.RoomLayerFiltersJsonAdapter.class)
public class RoomLayerFilters implements JsonPostDeserializeCall {

	public static final RoomLayerFilters NULL = new RoomLayerFilters();

	@Getter
	private List<RoomLayerFilter<?>> values = new ArrayList<>();

	public final boolean notValid() {
		for (RoomLayerFilter<?> value : values) {
			if (value.notValid()) {
				return true;
			}
		}
		return false;
	}

	public final boolean test(Random random, RoomLayerFilterValues room) {
		for (RoomLayerFilter<?> value : values) {
			if (!value.test(random, room.get(value.keyName()))) {
				return false;
			}
		}
		return true;
	}

	public final boolean isNull() {
		return this.values.isEmpty();
	}

	@Override
	public final void postDeserializedJson() {
		this.values.removeIf((e) -> e == null);
	}

	private static class RoomLayerFiltersJsonAdapter extends AbstractJsonCodec<RoomLayerFilters> {

		private final JsonCodec<RoomLayerFilter<?>> codec;
		private final RoomLayerFilter<?>[] array = ArrayUtils.createGenericArray(RoomLayerFilter.class, 0);

		public RoomLayerFiltersJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, registry);
			this.codec = this.registry.getCodecIndirect(RoomLayerFilter.class);
		}

		@Override
		public void write(RoomLayerFilters value, JsonWriter writer) throws IOException {
			ArrayCodec.write(value.getValues().toArray(e -> this.array), writer, this.codec);
		}

		@Override
		public RoomLayerFilters read(JsonReader reader) throws IOException {
			List<RoomLayerFilter<?>> filters = new ArrayList<>();
			for (RoomLayerFilter<?> roomLayerFilter : ArrayCodec.read(this.array, reader, this.codec)) {
				filters.add(roomLayerFilter);
			}
			return new RoomLayerFilters(Collections.unmodifiableList(filters));
		}
		
	}
}