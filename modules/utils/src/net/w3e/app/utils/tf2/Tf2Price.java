package net.w3e.app.utils.tf2;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import lombok.CustomLog;
import net.w3e.app.FrameObject;
import net.w3e.app.api.ApiSharedConstant;
import net.w3e.app.api.window.IBackgroundExecutor;

@CustomLog
public record Tf2Price(Tf2RegistryObject reg, float weapon, float kit, float profit) implements Tf2IconImpl, Comparable<Tf2Price> {

	private static final String SELECTOR = ">div[class=\"market_listing_price_listings_block\"]>div[class=\"market_listing_right_cell market_listing_their_price\"]>span[class=\"market_table_value normal_price\"]>span[class=\"normal_price\"]";

	@Override
	public final Tf2RegistryObject self() {
		return this.reg;
	}

	@Override
	public final String more(float dollar) {
		float profit = ((float)(int)(this.profit * dollar * 100 + 0.5f))/100;
		float kit = ((float)(int)(this.kit * dollar * 100 + 0.5f))/100;
		return String.format("<table style=\"color:white;\"><tr><td>Цена: %s</td></tr><tr><td>Профит: %s</td></tr></table>", kit, profit);
	}

	@Override
	public int compareTo(Tf2Price o) {
		return (int)((o.profit - this.profit) * 100 + 0.5f);
	}

	public static Tf2Price get(Tf2RegistryObject reg, IBackgroundExecutor stop, int attempt) {
		URI uri = null;
		try {
			uri = URI.create("https://steamcommunity.com/market/search?q=%22Killstreak+" +
				reg.link() +
				"%22&category_440_Collection%5B%5D=any&category_440_Type%5B%5D=any&category_440_Quality%5B%5D=tag_Unique&appid=440#p1_price_asc"
			);
			System.out.println("calculating " + reg.id());

			HttpRequest request = HttpRequest.newBuilder()
				.uri(uri)
				.GET() // GET is default
			.build();

			int i = 0;
			while (true) {
				HttpResponse<String> dom = ApiSharedConstant.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
				Element rows = Jsoup.parse(dom.body()).getElementById("searchResultsRows");
				try {
					if (rows.select("div.market_listing_table_message").get(0).text().equals("There was an error performing your search. Please try again later.")) {
						if (stop.isStop()) {
							return null;
						}
						if (i >= attempt) {
							System.out.println("не прогрузилось " + reg.id());
							return null;
						}
						FrameObject.sleep(4000);
						i++;
						continue;
					}
				} catch (Exception e) {}

				float weapon = Float.valueOf(rows.select(String.format("div[%s]%s", "data-hash-name=\"Killstreak " + reg.id() + "\"", SELECTOR)).get(0).attr("data-price")) / 100;
				float kit = Float.valueOf(rows.select(String.format("div[%s]%s", "data-hash-name=\"Killstreak " + reg.id() + " Kit\"", SELECTOR)).get(0).attr("data-price")) / 100;

				return new Tf2Price(reg, weapon, kit, weapon - kit * 1.15f);
			}
		} catch (Exception e) {
			log.warn(reg);
			System.out.println(uri);
			if ((e instanceof IndexOutOfBoundsException) || (e instanceof NullPointerException)) {
				if (e instanceof IndexOutOfBoundsException) {
					System.out.println("Неизвестный идентификатор " + reg.id());
				} else {
					System.out.println("Задудосили");
				}

				return null;
			}
			e.printStackTrace();
			return null;
		}
	}
}
