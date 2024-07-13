package com.askimed.nf.test.util;

import java.util.List;

public class AnsiText {

	public static boolean active = true;

	public static final String ANSI_RESET = "\u001B[0m";

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

	public static String bold(String string) {
		if (active) {
			return "\u001B[1m" + string + ANSI_RESET;
		} else {
			return string;
		}
	}

	public static String padding(String string, int count) {
		String padding = "";
		for (int i = 0; i < count; i++) {

			padding += " ";
		}
		String result = padding + string;
		result = result.replaceAll("\n", "\n" + padding);
		return result;
	}

	public static void printBulletList(List list) {
		for (Object item : list) {
			System.out.println("  \u2022 " + item);
		}
	}

}
