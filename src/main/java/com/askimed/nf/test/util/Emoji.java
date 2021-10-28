package com.askimed.nf.test.util;

public enum Emoji {

	ROBOT(0x1F916),

	BACKHAND_INDEX_POINTING_RIGHT(0x1F449),
	
	WAVING_HAND(0x1F44B),
	
	THUMBS_UP(0x1F44D),
	
	LIGHT_BULB(0x1F4A1),
	
	ROCKET(0x1F680);
	
	public static boolean active = true;

	private int unicode;

	private Emoji(int unicode) {
		this.unicode = unicode;
	}

	public static void enable() {
		active = true;
	}

	public static void disable() {
		active = false;
	}

	public static boolean isEnabled() {
		return active;
	}

	public static boolean isDisabled() {
		return !active;
	}

	@Override
	public String toString() {
		if (active) {
			return new String(Character.toChars(unicode));
		} else {
			return "";
		}
	}

}
