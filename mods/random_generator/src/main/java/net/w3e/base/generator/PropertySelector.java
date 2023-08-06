package net.w3e.base.generator;

import java.util.function.Predicate;

public class PropertySelector {

	private final Predicate<String> type;
	private final Predicate<String> attribute;
	private final Predicate<String> id;
	private final Predicate<PropertyType> flags;

	public PropertySelector(Predicate<String> type, Predicate<String> attribute, Predicate<String> id, Predicate<PropertyType> flags) {
		this.type = type;
		this.attribute = attribute;
		this.id = id;
		this.flags = flags;
	}

	public final boolean test(PropertyType property) {
		return !(
			property == null ||
			this.type != null && !this.type.test(property.type) ||
			this.attribute != null && !this.attribute.test(property.attribute) ||
			this.id != null && !this.id.test(property.id) ||
			this.flags != null && !this.flags.test(property)
		);
	}

	public final boolean test(IProperty property) {
		return this.test(property.type());
	}

	public final boolean inversedTest(IProperty property) {
		return !this.test(property);
	}
}
