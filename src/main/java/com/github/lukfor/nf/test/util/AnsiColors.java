package com.github.lukfor.nf.test.util;

public class AnsiColors {

	public static boolean active = true;

	public static final String ANSI_RESET = "\u001B[0m";

	public static final String ANSI_BLACK = "\u001B[30m";

	public static final String ANSI_RED = "\u001B[31m";

	public static final String ANSI_GREEN = "\u001B[32m";

	public static final String ANSI_YELLOW = "\u001B[33m";

	public static final String ANSI_BLUE = "\u001B[34m";

	public static final String ANSI_PURPLE = "\u001B[35m";

	public static final String ANSI_CYAN = "\u001B[36m";

	public static final String ANSI_WHITE = "\u001B[37m";

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

	public static String color(String string, String color) {
		if (active) {
			return color + string + ANSI_RESET;
		} else {
			return string;
		}
	}

	public static String black(String string) {
		return color(string, ANSI_BLACK);
	}

	public static String red(String string) {
		return color(string, ANSI_RED);
	}

	public static String green(String string) {
		return color(string, ANSI_GREEN);
	}

	public static String yellow(String string) {
		return color(string, ANSI_YELLOW);
	}

	public static String blue(String string) {
		return color(string, ANSI_BLUE);
	}

	public static String purple(String string) {
		return color(string, ANSI_PURPLE);
	}

	public static String cyan(String string) {
		return color(string, ANSI_CYAN);
	}

	public static String white(String string) {
		return color(string, ANSI_WHITE);
	}

}
