package net.w3e.app.utils.tf2;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.skds.lib2.io.json.JsonPostDeserializeCall;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.reflection.ReflectUtils;

public class Tf2Config implements JsonPostDeserializeCall {

	@Getter
	private float min = 5;
	@Getter
	private int await = 5000;
	@Getter
	private List<Tf2RegistryObject> items;

	@Override
	public void postDeserializedJson() {
		this.min = Math.max(1, this.min);
		if (this.items == null) {
			this.items = new ArrayList<>();
		}
	}

	public void reload() {
		Tf2Config config = JsonUtils.readJson("tf2/config.json", Tf2Config.class);
		ReflectUtils.fillInstanceFields(this, (field, accept) -> {
			try {
				accept.accept(field.get(config));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}
}
