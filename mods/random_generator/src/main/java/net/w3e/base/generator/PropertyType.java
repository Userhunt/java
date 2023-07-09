package net.w3e.base.generator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import com.google.common.base.Strings;

import net.w3e.base.collection.ArraySet;
import net.w3e.base.collection.IdentityLinkedHashMap;
import net.w3e.base.collection.ArraySet.ArraySetStrict;
import net.w3e.base.collection.cache.CacheKeys;
import net.w3e.base.registry.Registry;
import net.w3e.base.registry.RegistryEntry;
import net.w3e.base.registry.Registry.CacheRegistry;

public class PropertyType implements RegistryEntry<PropertyType> {

	public static final Registry<PropertyType> REGISTRY = new CacheRegistry<>("property", "empty");

	private static final List<String> FLAGS_REGISTER = new ArraySet<>();
	public static final CacheKeys<String> FLAGS = new CacheKeys<>() {
		@Override
		protected void initIns() {
			for (String string : FLAGS_REGISTER) {
				this.register(string);
			}
		}
	};

	public static final String FLAG_GLOBAL = registerFlag("");

	public static String registerFlag(String flag) {
		FLAGS.register(flag);
		FLAGS_REGISTER.add(flag);
		return flag;
	}

	@SuppressWarnings("unchecked")
	public static final <T extends PropertyType> T register(T type) {
		return (T)REGISTRY.register(type.getRegistryName(), type);
	}

	public static final PropertyType EMPTY = register(new PropertyType(null, "empty", null));

	public final String type;
	public final String attribute;
	public final String id;
	public final String key;
	private final Map<String, List<String>> flags;

	public PropertyType(String type, String attribute, String id, String... flags) {
		this(type, attribute, id, new FlagsBuilder().read(flags).build());
	}

	public PropertyType(String type, String attribute, String id, Map<String, List<String>> flags) {
		if (Strings.isNullOrEmpty(attribute)) {
			throw new IllegalStateException("attribute is null");
		}
		this.type = type;
		this.attribute = attribute;
		this.id = id;
		this.key = (type == null ? "" : type + ":") + attribute + (id == null ? "" : "#" + id);
		if (flags == null || flags.isEmpty()) {
			this.flags = null;
		} else {
			this.flags = new IdentityLinkedHashMap<>();
			// read global
			List<String> global = flags.get(FLAG_GLOBAL);
			if (global == null) {
				global = new ArraySetStrict<>();
			} else {
				global = new ArraySetStrict<>(FLAGS.get(global));
			}
			this.flags.put(FLAG_GLOBAL, global);

			// read other
			for (Entry<String, List<String>> entry : flags.entrySet()) {
				List<String> set = new ArraySetStrict<>(FLAGS.get(entry.getValue()));
				this.flags.put(FLAGS.get(entry.getKey()), set);
				global.addAll(set);
			}
			// save global
			this.flags.put(FLAG_GLOBAL, global);
		}
	}

	public final boolean containsFlag(String flag) {
		return this.containsFlag(FLAG_GLOBAL, flag);
	}

	public final boolean containsFlag(String key, String flag) {
		return this.containsFlag(key, flag, true);
	}

	public final boolean containsFlag(String key, String flag, boolean strict) {
		if (this.flags == null) {
			return false;
		}
		List<String> list = null;
		if (strict) {
			list = this.flags.get(key);
		} else {
			for (Entry<String, List<String>> entry : this.flags.entrySet()) {
				if (Objects.equals(key, entry.getKey())) {
					list = entry.getValue();
				}
			}
		}
		if (list == null) {
			return false;
		}
		if (strict) {
			return list.contains(flag);
		} else {
			for (String string : list) {
				if (Objects.equals(string, flag)) {
					return true;
				}
			}
			return false;
		}
	}

	public static interface PropertyFlags extends Map<String, String[]> {}

	public static class FlagsBuilder {

		private final Map<String, List<String>> map = new LinkedHashMap<>();
		private List<String> last = null;
		private String lastKey;

		public FlagsBuilder() {
			this.push("gloabal");
		}

		public final FlagsBuilder global() {
			return this.push("gloabal");
		}

		protected final boolean remove(String flag) {
			return this.last.remove(flag);
		}

		protected final String lastKey() {
			return this.lastKey;
		}

		public final FlagsBuilder read(Map<String, List<String>> map) {
			return this.read(map, true);
		}

		public final FlagsBuilder read(Map<String, List<String>> map, boolean replace) {
			if (map != null) {
				if (replace) {
					this.map.clear();
				}
				for (Entry<String, List<String>> entry : map.entrySet()) {
					String key = entry.getKey();
					if (key == null) {
						continue;
					}
					this.push(key);
					this.last.addAll(entry.getValue());
				}
			}
			return this.global();
		}

		public final FlagsBuilder read(String... keys) {
			for (String key : keys) {
				if (key != null) {
					if (key.startsWith("#")) {
						this.push(key);
					} else {
						this.add(key);
					}
				}
			}
			return this;
		}

		public final FlagsBuilder push(String key) {
			if (key != null) {
				this.lastKey = key;
				this.last = this.map.computeIfAbsent(key, e -> new ArraySet<>());
			}
			return this;
		}

		public final FlagsBuilder add(String key) {
			if (key != null) {
				this.last.add(key);
			}
			return this;
		}

		public final Map<String, List<String>> build() {
			Map<String, List<String>> map = new LinkedHashMap<>();
			for (Entry<String, List<String>> entry : this.map.entrySet()) {
				map.put(entry.getKey(), new ArraySet<>(entry.getValue()));
			}
			return map;
		}
	}

	public static class FlagsTypedBuilder extends FlagsBuilder {

		private final String flagType;

		public FlagsTypedBuilder(String flagType) {
			this.flagType = flagType;
		}

		protected final String push(String previous, String flag) {
			String lastKey = this.lastKey();
			this.push(this.flagType);
			this.remove(previous);
			this.add(flag);
			if (lastKey != this.flagType) {
				this.push(lastKey);
			}
			return flag;
		}
	}

	@Override
	public final RegistryEntry<PropertyType> setRegistryName(String name) {
		if (name != this.key) {
			throw Registry.redifine(name, this.key);
		}
		return this;
	}

	@Override
	public String getRegistryName() {
		return this.key;
	}
}
