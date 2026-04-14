package com.askimed.nf.test.core.tagquery;

public class TagQueryParseException extends RuntimeException {

    private final String query;
    private final int position;

    public TagQueryParseException(String query, int position, String message) {
        super(format(query, position, message));
        this.query = query;
        this.position = position;
    }

    public String getQuery() {
        return query;
    }

    public int getPosition() {
        return position;
    }

    private static String format(String query, int position, String message) {
        int clampedPos = Math.min(Math.max(position, 0), query.length());
        String prefix = "Invalid tag query: ";
        String pointer = " ".repeat(prefix.length() + clampedPos) + "^";
        return String.format("%s%s%n%s%n%s", prefix, query, pointer, message);
    }

}
