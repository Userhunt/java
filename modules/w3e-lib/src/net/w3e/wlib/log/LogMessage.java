package net.w3e.wlib.log;

import lombok.AllArgsConstructor;
import net.skds.lib2.utils.AutoString;

@AllArgsConstructor
public class LogMessage {

	private final String message;

	public final String createMsg(Object... args) {
		Object[] realArg = new Object[args.length];
		int i = 0;
		for (Object arg : args) {
			realArg[i] = AutoString.autoString(arg);
			i++;
		}
		return String.format(this.message, realArg);
	}
}
