package net.api.network;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.api.StopEvent;
import net.api.network.Packet.PacketHandler;
import net.w3e.base.PrintWrapper;

public class Network {

	private static final Map<String, Function<String, Packet>> MAP = new HashMap<>();

	public static void register(Packet packet) {
		MAP.put(packet.getClass().getSimpleName(), packet::read);
	}

	private static final int PORT = 29055;

	public static final ThreadGroup THREAD_GROUP = new ThreadGroup("network");

	private static NetworkResiver RESIVER;
	private static NetworkSender SENDER;

	public static void set(String adress) {
		if (adress == null) {
			if (RESIVER != null) {
				RESIVER.close();
			}
			if (SENDER != null) {
				SENDER.close();
			}
			return;
		}
		NetworkResiver oldResiver = RESIVER;
		NetworkSender oldSender = SENDER;
		try {
			InetSocketAddress socket = new InetSocketAddress(adress, PORT);

			RESIVER = new NetworkResiver(socket);
			SENDER = new NetworkSender(socket);

			if (oldResiver != null) {
				oldResiver.close();
			}
			if (oldSender != null) {
				oldSender.close();
			}
		} catch (Exception e) {
			RESIVER = oldResiver;
			SENDER = oldSender;
			e.printStackTrace();
		}
	}

	public static void init() {
		set("0.0.0.0");
		StopEvent.register(() -> {
			set(null);
		});
		//example();
	}

	public static void example() {
		register(new Packet());
		try {
			long dl = System.currentTimeMillis() + 500;
			LockSupport.parkUntil(dl);

			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						while (true) {
							long dl = System.currentTimeMillis() + 10000;
							SENDER.send(new Packet());
							SENDER.send(new Packet());
							SENDER.send(new Packet());

							LockSupport.parkUntil(dl);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			t.setName("Client");
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class NetworkResiver extends Thread {

		private final Selector selector;
		private final ServerSocketChannel channel;
		private final ByteBuffer buf;
		private boolean run = true;
		private int last = -1;

		public NetworkResiver(InetSocketAddress address) {
			super(THREAD_GROUP, THREAD_GROUP.getName());
			this.selector = initSelector();
			this.channel = initChannel(address);

			this.buf = ByteBuffer.allocate(1024);
			start();
		}

		public final void close() {
			this.run = false;
		}

		@Override
		@SuppressWarnings("deprecation")
		public final void run() {
			while (run) {
				try {
					selector.select(key -> {
						try {
							if (key.isAcceptable()) {
								SocketChannel client = channel.accept();

								client.configureBlocking(false);
								client.register(selector, SelectionKey.OP_READ);
								Socket socket = client.socket();
								socket.setSendBufferSize(128);
								socket.setReceiveBufferSize(128);
								socket.setTcpNoDelay(false);
								socket.setSoTimeout(30 * 1000); // 30 seconds
							} else {
								final SocketChannel channel = (SocketChannel)key.channel();
								if (!channel.isOpen() || !key.isReadable()) {
									return;
								}
								buf.rewind();
								//channel.read(buf);
								channel.read(buf);

								String string = new String(buf.array());
								string = string.substring(0, string.lastIndexOf("}") + 1);

								JsonReader jsonReader = new JsonReader(new StringReader(string));
								jsonReader.setLenient(true);
								List<PacketHandler> list = new ArrayList<>();
								try {
									while(true) {
										JsonObject json = (JsonObject)JsonParser.parseReader(jsonReader);
										list.add(new PacketHandler(json.get("c").getAsString(), json.get("i").getAsInt(), json.get("m").getAsString()));
									}
								} catch (Exception e) {
									//e.printStackTrace();
								}
								Collections.sort(list);
								for (PacketHandler handler : list) {
									if (this.last < handler.i()) {
										this.last = handler.i();
										Function<String, Packet> function = MAP.get(handler.clazz());
										if (function != null) {
											function.apply(handler.message()).run();
										} else {
											PrintWrapper.LOGGER.error("unknown packet " + handler.clazz());
										}
									}
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				this.selector.close();
				this.channel.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private final Selector initSelector() {
			try {
				return Selector.open();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		private final ServerSocketChannel initChannel(InetSocketAddress address) {
			ServerSocketChannel server = null;
			try {
				server = ServerSocketChannel.open();
				server.bind(address);
				server.configureBlocking(false);
				server.register(selector, SelectionKey.OP_ACCEPT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return server;
		}
	}

	private static class NetworkSender {

		private final SocketChannel channel;
		private int i = 0;

		public NetworkSender(InetSocketAddress address) {
			this.channel = initChannel(address);
		}

		public void close() {
			try {
				this.channel.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void send(Packet packet) {
			try {
				this.channel.write(packet.write(i));
				i++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private SocketChannel initChannel(InetSocketAddress address) {
			try {
				return SocketChannel.open(address);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static void send(Packet packet) {
		if (SENDER != null) {
			SENDER.send(packet);
		}
	}

	/*
	public static void testNet() throws Exception {
		testNetS();

		long dl = System.currentTimeMillis() + 500;
		LockSupport.parkUntil(dl);

		testNetC();
	}

	private static void testNetS() throws Exception {

		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Selector selector = Selector.open();
					SocketAddress address = new InetSocketAddress("0.0.0.0", 25565);
					ServerSocketChannel server = ServerSocketChannel.open();
					server.bind(address);
					server.configureBlocking(false);
					server.register(selector, SelectionKey.OP_ACCEPT);

					ByteBuffer buf = ByteBuffer.allocate(1024);

					while (true) {
						buf.rewind();
						selector.select(key -> {
							try {
								if (key.isAcceptable()) {
									SocketChannel client = server.accept();
									System.out.println(client.isConnected());

									client.configureBlocking(false);
									client.register(selector, SelectionKey.OP_READ);
									Socket socket = client.socket();
									socket.setSendBufferSize(128);
									socket.setReceiveBufferSize(128);
									socket.setTcpNoDelay(false);
									socket.setSoTimeout(30 * 1000); // 30 seconds
								} else {
									final SocketChannel channel = (SocketChannel) key.channel();
									if (!channel.isOpen())
										return;
									if (!key.isReadable())
										return;
									channel.read(buf);
									System.out.println("======");
									System.out.println(new String(buf.array()));
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						});

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.setName("Serv");
		t.start();

	}

	private static void tick(SocketChannel sc) throws Exception {
		ByteBuffer bb = ByteBuffer.wrap("Tick".getBytes());
		sc.write(bb);
	}

	private static void testNetC() throws Exception {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					//Selector selector = Selector.open();
					SocketAddress address = new InetSocketAddress("0.0.0.0", 25565);
					SocketChannel sc = SocketChannel.open(address);
					//sc.bind(address);
					//sc.configureBlocking(false);
					//sc.register(selector, SelectionKey.OP_ACCEPT);

					while (true) {
						long dl = System.currentTimeMillis() + 500;
						tick(sc);
						//System.out.println("C: " + res);

						LockSupport.parkUntil(dl);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.setName("Client");
		t.start();
	}
	*/
}
