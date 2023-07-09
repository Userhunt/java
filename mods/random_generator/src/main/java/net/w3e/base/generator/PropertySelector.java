package net.w3e.base.generator;

import java.util.function.Predicate;

public record PropertySelector(Predicate<String> type, Predicate<String> attribute, Predicate<String> id, Predicate<PropertyType> flags) {

	public boolean test(PropertyType property) {
		return !(
			property == null ||
			this.type != null && !this.type.test(property.type) ||
			this.attribute != null && !this.attribute.test(property.attribute) ||
			this.id != null && !this.id.test(property.id) ||
			this.flags != null && !this.flags.test(property)
		);
	}

	public boolean test(IProperty property) {
		return this.test(property.type());
	}

	public boolean inversedTest(IProperty property) {
		return !this.test(property);
	}
}
