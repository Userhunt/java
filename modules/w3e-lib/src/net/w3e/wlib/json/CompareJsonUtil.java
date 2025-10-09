package net.w3e.wlib.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.ToString;
import net.skds.lib2.io.json.elements.JsonArray;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.json.elements.JsonNumber;
import net.skds.lib2.io.json.elements.JsonObject;
import net.skds.lib2.io.json.elements.JsonString;

public class CompareJsonUtil {

	public static CompareResult compare(JsonElement a, JsonElement b) {
		if (a.equals(b)) {
			return new Compared(a);
		}
		if (a.getClass() != b.getClass()) {
			return new DifferenceType(a, b);
		}
		return switch (a.type()) {
			case LIST -> new DifferenceArray(a.getAsJsonArray(), b.getAsJsonArray());
			case OBJECT -> new DifferenceObject(a.getAsJsonObject(), b.getAsJsonObject());
			case BOOLEAN, NULL, NUMBER, STRING -> new DifferencePrimitive(a, b);
			default -> throw new IllegalArgumentException("unknown json " + a.getClass());
		};
	}

	@AllArgsConstructor
	public abstract static class CompareResult {
		public final JsonElement a;
		public final JsonElement b;

		public CompareResult(CompareResult compare) {
			this(compare.a, compare.b);
		}

		public final JsonElement print(boolean all) {
			JsonObject json = printResult(all);
			if (json != null) {
				json.put("type", this.getClass().getSimpleName());
			}
			return json;
		}

		protected abstract JsonObject printResult(boolean all);

		protected final JsonElement keyToJson(Object object) {
			return object instanceof String s ? new JsonString(s) : new JsonNumber((Number)object);
		}

		public CompareResult getRoot() {
			return this;
		}

		protected String getKey() {
			throw new UnsupportedOperationException("getKey");
		}
	}

	public static class Compared extends CompareResult {
		public Compared(JsonElement a) {
			super(a, a);
		}

		@Override
		protected final JsonObject printResult(boolean all) {
			if (all) {
				JsonObject json = new JsonObject();
				json.put("compare", true);
				return json;
			} else {
				return null;
			}
		}
	}

	public static class DifferenceType extends CompareResult {
		public DifferenceType(JsonElement a, JsonElement b) {
			super(a, b);
		}

		@Override
		protected final JsonObject printResult(boolean all) {
			JsonObject json = new JsonObject();
			json.put("compare", "difference type");
			json.put("a", this.a.getClass().getSimpleName());
			json.put("b", this.b.getClass().getSimpleName());
			return json;
		}
	}

	public static class DifferencePrimitive extends CompareResult {
		public DifferencePrimitive(JsonElement a, JsonElement b) {
			super(a, b);
		}

		@Override
		protected final JsonObject printResult(boolean all) {
			JsonObject json = new JsonObject();
			json.put("compare", "difference values");
			json.put("a", this.a);
			json.put("b", this.b);
			return json;
		}
	}

	@ToString
	public static class DifferenceObject extends CompareResult {

		private final List<CompareResult> list = new ArrayList<>();

		public DifferenceObject(JsonObject a, JsonObject b) {
			super(a, b);
			a = (JsonObject)a.clone();
			b = (JsonObject)b.clone();

			for (Entry<String, JsonElement> next : a.entrySet()) {
				String key = next.getKey();
				JsonElement aValue = next.getValue();
				JsonElement bValue = b.remove(key);
				if (bValue == null) {
					this.list.add(new NullResult(key, aValue, true));
					continue;
				}
				CompareResult result = compare(aValue, bValue);
				if (result == null || result instanceof Compared) {
					continue;
				}
				this.list.add(new FieldResult(key, result));
			}

			for (Entry<String, JsonElement> next : b.entrySet()) {
				this.list.add(new NullResult(next.getKey(), next.getValue(), false));
			}
		}

		@Override
		protected final JsonObject printResult(boolean all) {
			if (all || !this.list.isEmpty()) {
				JsonObject json = new JsonObject();
				json.put("compare", this.list.isEmpty());

				JsonArray array = new JsonArray();
				for (CompareResult compareResult : this.list) {
					array.add(compareResult.printResult(all));
				}
				json.put("values", array);

				return json;
			} else {
				return null;
			}
		}

		@Override
		public final CompareResult getRoot() {
			if (this.list.size() == 1) {
				CompareResult value = list.get(0);
				return new RootResult(value.getKey(), value.getRoot());
			}
			return super.getRoot();
		}
	}

	@ToString
	public static class DifferenceArray extends CompareResult {
		private final List<CompareResult> list = new ArrayList<>();

		public DifferenceArray(JsonArray a, JsonArray b) {
			super(a, b);
			a = (JsonArray)a.clone();
			b = (JsonArray)b.clone();

			int i = -1;
			for (JsonElement aValue : a) {
				i++;
				if (b.size() <= i) {
					this.list.add(new NullResult(i, aValue, true));
					continue;
				}
				CompareResult result = compare(aValue, b.get(i));
				if (result == null || result instanceof Compared) {
					continue;
				}
				this.list.add(new FieldResult(i, result));
			}
			for (int j = i; j < b.size(); j++) {
				this.list.add(new NullResult(i, b.get(i), false));
			}
		}

		@Override
		protected JsonObject printResult(boolean all) {
			if (all || !this.list.isEmpty()) {
				JsonObject json = new JsonObject();
				json.put("compare", this.list.isEmpty());

				JsonArray array = new JsonArray();
				for (CompareResult compareResult : this.list) {
					array.add(compareResult.printResult(all));
				}
				json.put("values", array);

				return json;
			} else {
				return null;
			}
		}

		@Override
		public final CompareResult getRoot() {
			if (this.list.size() == 1) {
				CompareResult value = list.get(0);
				return new RootResult(value.getKey(), value.getRoot());
			}
			return super.getRoot();
		}
	}

	private static class FieldResult extends CompareResult {
		private final Object field;
		private final CompareResult compare;

		public FieldResult(Object field, CompareResult compare) {
			super(compare);
			this.field = field;
			this.compare = compare;
		}

		@Override
		protected final JsonObject printResult(boolean all) {
			JsonObject json = new JsonObject();
			json.put("compare", "values by field is not same");
			json.put("field", this.keyToJson(this.field));
			json.put("result", this.compare.printResult(all));
			return json;
		}

		@Override
		protected final String getKey() {
			return String.valueOf(this.field);
		}
	}

	private static class NullResult extends CompareResult {
		private final Object key;
		private final boolean isA;

		public NullResult(Object key, JsonElement value, boolean isA) {
			super(value, null);
			this.key = key;
			this.isA = isA;
		}

		@Override
		protected JsonObject printResult(boolean all) {
			JsonObject json = new JsonObject();
			json.put("compare", "is not present in " + (this.isA ? "a" : "b"));
			json.put("key", this.keyToJson(this.key));
			return json;
		}

		@Override
		protected final String getKey() {
			return String.valueOf(this.key);
		}
	}

	private static class RootResult extends CompareResult {

		private final String key;
		private final CompareResult compare;

		public RootResult(String key, CompareResult compare) {
			super(compare);
			this.key = key;
			this.compare = compare;
		}

		@Override
		protected final JsonObject printResult(boolean all) {
			JsonObject json = new JsonObject();
			json.put("key", generateKey());
			json.put("value", this.getRecursiveRoot().print(all));
			return json;
		}

		private final RootResult getRecursiveRoot() {
			if (this.compare instanceof RootResult root) {
				return root.getRecursiveRoot();
			}
			return this;
		}

		private String generateKey() {
			String key = this.key;
			if (this.compare instanceof RootResult root) {
				key += "///" + root.generateKey();
			}
			return key;
		}
	}
}