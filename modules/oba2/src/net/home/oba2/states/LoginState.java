package net.home.oba2.states;

import net.home.oba2.OneBitState;
import net.home.oba2.OneBitAdventure.ObaHelper;

public class LoginState extends OneBitState {

	private static final int COLOR = -14935012;

	public static LoginState INSTANCE = new LoginState();

	private LoginState() {}

	public static final LoginState test(OneBitState oldState, ObaHelper helper) {
		if (helper.getGray().getRGB(0, 4) == COLOR) {
			return INSTANCE;
		}
		return null;
	}

	@Override
	public final void play(ObaHelper helper) {
		helper.debug("login");
		if (helper.isPlay()) {
			helper.focus();
			space();
			helper.focusBack();
		}
		sleep(1000);
	}
}
