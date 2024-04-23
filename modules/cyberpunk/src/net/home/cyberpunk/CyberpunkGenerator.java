package net.home.cyberpunk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.w3e.base.PrintWrapper;
import net.w3e.base.json.FileUtil;

public class CyberpunkGenerator {

	public static void main(String[] args) {
		PrintWrapper.install();
		CyberpunkGenerator generator = new CyberpunkGenerator();
		new CyberwareGenerator(generator);
		new ShardsGenerator(generator);
		new QuickhackGenerator(generator);

		generator.save();
	}

	private final Map<String, ShopPage> items = new LinkedHashMap<>();

	public final ShopPage push(String key) {
		return this.items.computeIfAbsent(key, ShopPage::new);
	}

	public final String convertNumber(int i) {
		return switch (i) {
			case 1 -> "I";
			case 2 -> "II";
			case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
			case 10 -> "X";
			default -> throw new IllegalStateException("");
		};
	}

	private final void save() {
		StringBuilder pages = new StringBuilder();
		for (ShopPage page : this.items.values()) {
			page.generate(pages);
		}

		new File("cyberpunk").mkdirs();

		String result =
			String.format("""
			@addMethod(gameuiInGameMenuGameController)
			protected cb func RegisterW3ECYBERWAREStore(event: ref<VirtualShopRegistration>) -> Bool {
			%s
			}
			""", pages
		);
		FileUtil.writeString(new File("C:\\Games\\cyberpunk\\Cyberpunk 2077\\r6\\scripts\\W3ECyberware\\W3ECyberware-atelier-store.reds"), result);
		FileUtil.writeString(new File("cyberpunk\\W3ECyberware-atelier-store.reds"), result);

		{
			File f = new File("cyberpunk\\w3e_shop.zip");
			ZipOutputStream out;
			try {
				out = new ZipOutputStream(new FileOutputStream(f));

				ZipEntry e = new ZipEntry("r6/scripts/W3ECyberware/W3ECyberware-atelier-store.reds");
				out.putNextEntry(e);
				byte[] data = result.getBytes();
				out.write(data, 0, data.length);
				out.closeEntry();

				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		{
			File f = new File("cyberpunk\\w3e_holo_arm.zip");
			ZipOutputStream out;
			try {
				out = new ZipOutputStream(new FileOutputStream(f));

				ZipEntry e = new ZipEntry("bin/x64/plugins/cyber_engine_tweaks/scripts/w3e_holo_arm.lua");
				out.putNextEntry(e);
				byte[] data = """
				local GameSession = require('GameSession')

				registerForEvent('onInit', function() 
					GameSession.OnLoad(function()
						RPGManager.ForceEquipItemOnPlayer(GetPlayer(),"Items.PlayerSilverhandArm", true)
					end)
				end)
				""".getBytes();
				out.write(data, 0, data.length);
				out.closeEntry();

				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
