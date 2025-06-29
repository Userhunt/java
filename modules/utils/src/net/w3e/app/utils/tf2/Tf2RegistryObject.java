package net.w3e.app.utils.tf2;

import java.lang.reflect.Type;
import java.util.Arrays;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.w3e.wlib.json.WJsonBuilder;

@DefaultJsonCodec(Tf2RegistryObject.JsonAdapter.class)
public record Tf2RegistryObject(String id, String link, String image, String[] group) implements Tf2Icon {

	@Override
	public Tf2RegistryObject getReg() {
		return this;
	}

	@Override
	public String text() {
		return "";
	}

	@Override
	public final boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (object == null) {
			return false;
		} else if (!(object instanceof Tf2RegistryObject reg)) {
			return false;
		} else {
			return this.id.equals(reg.id) && this.link.equals(reg.link) && this.image.equals(reg.image) && Arrays.equals(this.group, reg.group);
		}
	}

	@Override
	public final int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public final String toString() {
		return String.format("{class:%s,hash:%s,id:\"%s\",link:\"%s\",image:%s}", this.getClass().getSimpleName(), this.hashCode(), this.id, this.link, this.image != null);
	}

	static class JsonAdapter extends JsonReflectiveBuilderCodec<Tf2RegistryObject> {

		public JsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, Tf2RegistryObjectData.class, registry);
		}

		private static class Tf2RegistryObjectData implements WJsonBuilder<Tf2RegistryObject> {

			private static final String[] GROUP = new String[]{"all"};

			private String id;
			private String[] group = GROUP;
			private String link;
			private String image;

			@Override
			public Tf2RegistryObject build() {
				this.nonNull("id", this.id);
				if (this.link == null) {
					this.link = id.replace(" ", "%20");
				}
				this.nonNull("image " + this.id, this.image);
				
				return new Tf2RegistryObject(this.id, this.link, this.image, this.group);
			}
		}
	}
}
