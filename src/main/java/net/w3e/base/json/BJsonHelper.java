package net.w3e.base.json;

import net.w3e.base.json.system.convert.BaseJsonHelperConvert;
import net.w3e.base.json.system.read.BaseJsonHelperRead;
import net.w3e.base.message.MessageUtil;

public interface BJsonHelper extends BaseJsonHelperRead, BaseJsonHelperConvert {

	/*========================================= min/max ======================================*/
	default float minMax(float value, Float min, Float max, String arg, float def) {
		if (min != null && value < min) {
			this.throwMessage(MessageUtil.LESS_THAN.createMsg(arg, value, min));
			value = def;
		}
		if (max != null && value > max) {
			this.throwMessage(MessageUtil.MORE_THAN.createMsg(arg, value, max));
			value = def;
		}
		return value;
	}

	default int minMax(int value, Integer min, Integer max, String arg, int def) {
		if (min != null && value < min) {
			this.throwMessage(MessageUtil.LESS_THAN.createMsg(arg, value, min));
			value = def;
		}
		if (max != null && value > max) {
			this.throwMessage(MessageUtil.MORE_THAN.createMsg(arg, value, max));
			value = def;
		}
		return value;
	}
}
