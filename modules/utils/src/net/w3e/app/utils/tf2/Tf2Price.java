package net.w3e.app.utils.tf2;

import java.io.IOException;

import lombok.CustomLog;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.json.elements.JsonObject;
import net.skds.lib2.utils.HttpUtils;
import net.skds.lib2.utils.HttpUtils.DownloadProcess;

@CustomLog
@RequiredArgsConstructor
public class Tf2Price implements Comparable<Tf2Price>, Tf2Icon {

	@Getter(onMethod_ = @Override)
	private final Tf2RegistryObject reg;

	@Getter
	private float gun = -1;
	@Getter
	private float kit = -1;

	@Getter
	private float profit = -1000;

	public final boolean isReady() {
		return !(this.gun == -1 || this.kit == -1 || this.profit == -1000);
	}

	public final void load() throws IOException {
		String id = "https://steamcommunity.com/market/priceoverview/?appid=440&currency=5&market_hash_name=Killstreak%20" + this.reg.link();
		log.info(id);
		if (this.gun == -1) {
			this.gun = this.load(id);
		}
		if (this.kit == -1) {
			this.kit = this.load(id + "%20Kit");
		}
		if (this.gun != -1 && this.kit != -1) {
			this.profit = this.gun - this.kit / 20 * 23;
		}
	}

	private float load(String id) throws IOException {
		DownloadProcess process = HttpUtils.downloadFromNet(id);
		process.readAll();
		JsonObject json = JsonUtils.parseJson(new String(process.getContent()), JsonObject.class);
		JsonElement js;
		if (json.containsKey("lowest_price")) {
			js = json.get("lowest_price");
		} else {
			js = json.get("median_price");
		}
		String price = js.getAsString().replace(',', '.');
		price = price.substring(0, price.length() - 5);
		return Float.parseFloat(price);
	}

	public final String text() {
		return "%.2f".formatted(this.getGun()) + "/" + "%.2f".formatted(this.getProfit());
	}

	@Override
	public final int compareTo(Tf2Price o) {
		if (!this.isReady()) {
			return - 1;
		} else if (!o.isReady()) {
			return 1;
		} else {
			return Float.compare(this.getProfit(), o.getProfit());
		}
	}
}
