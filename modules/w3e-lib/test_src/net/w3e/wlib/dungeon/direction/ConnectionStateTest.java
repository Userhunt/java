package net.w3e.wlib.dungeon.direction;

import net.skds.lib2.utils.logger.SKDSLogger;

public class ConnectionStateTest {
	
	public static void main(String[] args) throws InterruptedException {
		SKDSLogger.replaceOuts();

		while (true) {
			System.out.println();
			System.out.println();
			System.out.println();
			for (ConnectionState a : ConnectionState.values()) {
				for (ConnectionState b : ConnectionState.values()) {
					try {
						//System.out.println(a + " + " + b + " = " + a.add(b));
						System.out.println(a + " - " + b + " = " + a.remove(b));
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			}
			Thread.sleep(4000);
		}
	}
}
