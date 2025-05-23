package net.w3e.app.utils.registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.CustomLog;
import net.skds.lib2.utils.AutoString;
import net.skds.lib2.utils.StringUtils;
import net.w3e.app.old.MainFrame;
import net.w3e.wlib.collection.ArraySet;
import net.w3e.wlib.collection.cache.CacheKeys;
import net.w3e.wlib.collection.cache.CacheKeys.CacheKeysEmpty;
import net.w3e.wlib.collection.identity.IdentityLinkedHashMap;

@CustomLog
public class DynamicRegistry<T> extends Registry<T> {

	private final List<DynamicRegisterEntry<T>> dynamic = new ArraySet<>();

	public DynamicRegistry(String key, String def) {
		super(key, def);
	}

	public final DynamicRegisterResult<T> registerDynamicByKey(String key, String... dynamicReason) {
		if (key == null || dynamicReason == null || dynamicReason.length == 0) {
			return new DynamicRegisterResult<>(toEntry(key, null), false);
		}
		for (DynamicRegisterEntry<T> entry : this.dynamic) {
			if (entry.key.equals(key)) {
				return this.register(entry, dynamicReason);
			}
		}
		return new DynamicRegisterResult<>(toEntry(key, null), false);
	}

	public final DynamicRegisterResult<T> registerDynamicByValue(T value, String... dynamicReason) {
		if (value == null || dynamicReason == null || dynamicReason.length == 0) {
			RegistryEntry<T> entry = toEntry(value);
			return new DynamicRegisterResult<>(entry, false);
		}
		for (DynamicRegisterEntry<T> entry : this.dynamic) {
			if (entry.value == value) {
				return this.register(entry, dynamicReason);
			}
		}
		return new DynamicRegisterResult<>(toEntry(value), false);
	}

	public final DynamicRegisterResult<T> registerDynamic(String key, T value, String... dynamicReason) {
		if (key == null || value == null || dynamicReason == null || dynamicReason.length == 0) {
			RegistryEntry<T> entry = toEntry(value);
			if (key != null) {
				entry.setRegistryName(key);
			}
			return new DynamicRegisterResult<>(entry, false);
		}
		for (DynamicRegisterEntry<T> entry : this.dynamic) {
			if (entry.key.equals(key)) {
				return register(entry, dynamicReason);
			}
		}
		if (get(key) != null) {
			return new DynamicRegisterResult<>(toEntry(key, value), false);
		} else {
			return register(new DynamicRegisterEntry<T>(key, value, new HashSet<>()), dynamicReason);
		}
	}

	private final DynamicRegisterResult<T> register(DynamicRegisterEntry<T> entry, String... dynamicReason) {
		if (entry.reasons.isEmpty()) {
			this.dynamic.add(entry);
			this.register(entry.key, entry.value);
		}
		return new DynamicRegisterResult<>(toEntry(entry.key, entry.value), entry.add(dynamicReason));
	}

	public final boolean unregisterReason(String... dynamicReason) {
		if (dynamicReason == null || dynamicReason.length == 0) {
			return false;
		}
		boolean bl = false;
		for (DynamicRegisterEntry<T> entry : new ArrayList<>(this.dynamic)) {
			bl = tryRemove(entry, dynamicReason) || bl;
		}
		return bl;
	}

	public final boolean unregisterByKey(String key, String... dynamicReason) {
		if (key == null || dynamicReason == null || dynamicReason.length == 0) {
			return false;
		}
		for (DynamicRegisterEntry<T> entry : this.dynamic) {
			if (entry.key().equals(key)) {
				return tryRemove(entry, dynamicReason);
			}
		}
		return false;
	}

	public final boolean unregisterByValue(T value, String... dynamicReason) {
		if (value == null || dynamicReason == null || dynamicReason.length == 0) {
			return false;
		}
		for (DynamicRegisterEntry<T> entry : this.dynamic) {
			if (entry.value == value) {
				return tryRemove(entry, dynamicReason);
			}
		}
		return false;
	}

	private final boolean tryRemove(DynamicRegisterEntry<T> entry, String... dynamicReason) {
		boolean bl = false;
		Set<String> reasons = entry.reasons();
		for (String reason : dynamicReason) {
			bl = reasons.remove(reason) || bl;
		}
		if (reasons.isEmpty()) {
			this.remove(entry, key);
		}
		return bl;
	}

	protected void remove(DynamicRegisterEntry<T> entry, String key) {
		this.dynamic.remove(entry);
		this.remove(key);
	}

	public static record DynamicRegisterResult<T>(RegistryEntry<T> entry, boolean registred) {}

	private static record DynamicRegisterEntry<T>(String key, T value, Set<String> reasons) {
		public final boolean add(String... dynamicReason) {
			boolean bl = false;
			for (String string : dynamicReason) {
				if (string != null) {
					bl = this.reasons.add(string) || bl;
				}
			}
			return bl;
		}

		@Override
		public String toString() {
			return String.format("{key:%s,value:%s,reason:%s}", StringUtils.quote(this.key), this.value, AutoString.autoString(this.reasons));
		}
	}

	public static class CacheDynamicRegistry<T> extends DynamicRegistry<T> {

		private final CacheKeys<String> keys = new CacheKeysEmpty<>();

		public CacheDynamicRegistry(String key, String def) {
			super(key, def);
			new CacheKeysEmpty<>();
		}

		@Override
		protected final Map<String, RegistryEntry<T>> map() {
			return new IdentityLinkedHashMap<>();
		}

		@Override
		protected final void registerKey(String key, RegistryEntry<T> entry) {
			super.registerKey(this.keys.get(key), entry);
		}

		@Override
		protected final RegistryEntry<T> getEntry(String key) {
			return super.getEntry(this.keys.getNoCache(key));
		}

		@Override
		protected void remove(DynamicRegisterEntry<T> entry, String key) {
			super.remove(entry, key);
			this.keys.remove(key);
		}
	}

	public String dynamic() {
		return this.dynamic.toString();
	}

	public static void main(String[] args) {
		//init
		DynamicRegistry<String> registry = new CacheDynamicRegistry<>("key", null);
		registry.register("key1", "key1");
		registry.register("key2", "key2");
		registry.register("key3", "key3");
		String dynamic1 = "dynamic1";
		String dynamic2 = "dynamicB";
		registry.registerDynamic(dynamic1, "dynamicA", "1", "dynamic");
		registry.registerDynamic("dynamic2", dynamic2, "2", "dynamic");
		registry.registerDynamic("dynamic3", "dynamicC", "3", "dynamic");
		log.info("start registry");
		System.out.println(registry.keys());
		System.out.println(registry.dynamic());

		// register existes
		log.info("register existes");
		registry.registerDynamic("dynamic3", "dynamicC", "null/1");
		System.out.println(registry.dynamic());

		//modiy exists
		log.info("modiy exists");
		registry.registerDynamicByKey(dynamic1, "null/2");
		registry.registerDynamicByValue(dynamic2, "null/3", null);
		System.out.println(registry.dynamic());

		//modify not exists
		log.info("modify not exists");
		registry.registerDynamicByKey(dynamic2, "fake");
		System.out.println(registry.dynamic());

		//remove reasons
		log.info("remove reasons");
		registry.unregisterByKey(dynamic1, "1", "null/4");
		System.out.println(registry.dynamic());
		registry.unregisterByValue(dynamic2, "2", null);
		System.out.println(registry.dynamic());
		registry.unregisterReason("dynamic");
		System.out.println(registry.dynamic());
	}
}