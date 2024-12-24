package net.w3e.app.utils.cyberpunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ShopPage {

	private static final String ATLAS = "base/gameplay/gui/common/icons/items/item_icons5.inkatlas";
	private static final String TEXTURE = "upgradeshard";

	public final String name;
	private String atlas = ATLAS;
	private String texture = TEXTURE;
	public final List<GeneratorEntry> items = new ArrayList<>();

	public ShopPage(String name) {
		this.name = name;
	}

	public final ShopPage image(String atlas, String texture) {
		this.atlas = atlas;
		this.texture = texture;
		return this;
	}

	@Deprecated
	public final ShopPage add(String key) {
		return this.add(key, 5, rarity -> true);
	}

	public final ShopPage add(String key, int price, Predicate<Rarity> filter) {
		for (Rarity rarity : Rarity.iterableValues(filter)) {
			this.addUnique(key, price * rarity.lvl, rarity, 1);
		}
		return this;
	}

	public final ShopPage addUnique(String key, Rarity rarity) {
		return this.addUnique(key, rarity.lvl * 5, rarity);
	}

	public final ShopPage addUnique(String key, int price, Rarity rarity) {
		return this.addUnique(key, price, rarity, 1);
	}

	public final ShopPage addUnique(String key, int price, Rarity rarity, int count) {
		this.items.add(new GeneratorEntry(key, price, rarity, count));
		return this;
	}

	@Deprecated
	public final ShopPage addIconic(String key) {
		return this.add(key, 5, rarity -> true);
	}

	public final ShopPage remove(String name) {
		this.items.removeIf(item -> item.name.equals(name));
		return this;
	}

	public final void generate(StringBuilder pages) {
		new ShopGenerator().generate(pages);
	}

	private record GeneratorEntry(String name, int price, Rarity rarity, int count) {
		public final int getPrice() {
			return this.price * 1000;
		}
	}

	private class ShopGenerator {
		private final List<String> items;
		private final List<String> prices;
		private final List<String> rarities;
		private final List<String> counts;

		public ShopGenerator() {
			int size = ShopPage.this.items.size();
			this.items = new ArrayList<>(size);
			this.prices = new ArrayList<>(size);
			this.rarities = new ArrayList<>(size);
			this.counts = new ArrayList<>(size);
			for (GeneratorEntry item : ShopPage.this.items) {
				String key = "\"" + item.name() + "\"";
				this.items.add(key);
				this.prices.add(String.valueOf(item.getPrice()));
				this.rarities.add("\"" + item.rarity + "\"");
				this.counts.add(String.valueOf(item.count));
			}
		}

		private final void generate(StringBuilder pages) {
			if (this.items.isEmpty()) {
				return;
			}
			pages.append(
			String.format("""
					event.AddStore(
						n"W3E%s",
						"W3E %s Shop",
						%s,
						%s,
						r"%s",
						n"%s",
						%s,
						%s
					);

				""", ShopPage.this.name.toUpperCase(), ShopPage.this.name, this.items, this.prices, ShopPage.this.atlas, ShopPage.this.texture, this.rarities, this.counts
			));
			System.out.println(String.format("Сгенерировано %s предметов в магазине %s", this.items.size(), name));
		}
	}
}
