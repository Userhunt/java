package net.home.oba2;

import net.api.window.Inputs;
import net.home.oba2.OneBitAdventure.ObaHelper;

public abstract class OneBitState extends Inputs {

	public static final int SLEEP = 20;

	public static final void sleep() {
		sleep(SLEEP);
	}

	public abstract void play(ObaHelper helper);

	public void handleKey(OneBitAdventure main, String text) {}

	@FunctionalInterface
	public static interface OBAStatePredicate {
		OneBitState test(OneBitState oldState, ObaHelper helper);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
	}

}
