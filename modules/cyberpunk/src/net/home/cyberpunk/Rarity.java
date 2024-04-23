package net.home.cyberpunk;

import java.util.function.Predicate;
import java.util.stream.Stream;

public enum Rarity {
	Common(1),
	Uncommon(2),
	Rare(3),
	Epic(4),
	Legendary(5),
	Iconic(10, false);

	public final int lvl;
	public final boolean iterable;

	private Rarity(int price) {
		this(price, true);
	}

	private Rarity(int price, boolean iterable) {
		this.lvl = price;
		this.iterable = iterable;
	}

	public static final Predicate<Rarity> HAVE_1Common = rarity -> rarity != Iconic && rarity.lvl >= 1;
	public static final Predicate<Rarity> HAVE_2Uncommon = rarity -> rarity != Iconic && rarity.lvl >= 2;
	public static final Predicate<Rarity> HAVE_3Rare = rarity -> rarity != Iconic && rarity.lvl >= 3;
	public static final Predicate<Rarity> HAVE_4Epic = rarity -> rarity != Iconic && rarity.lvl >= 4;
	public static final Predicate<Rarity> HAVE_5Legendary = rarity -> rarity != Iconic && rarity.lvl >= 5;

	public static final Rarity[] iterableValues() {
		return iterableValues(HAVE_1Common);
	}

	public static final Rarity[] iterableValues(Predicate<Rarity> filter) {
		return Stream.of(values()).filter(rarity -> rarity.iterable && filter.test(rarity)).toArray(Rarity[]::new);
	}
}
