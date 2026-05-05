package com.askimed.nf.test.core.tagquery;

public class TagQueryParser {

    private enum TokenType {
        LPAREN, RPAREN, AND, OR, NOT, TAG, EOF
    }

    private static class Token {
        final TokenType type;
        final String value;
        final int pos;

        Token(TokenType type, String value, int pos) {
            this.type = type;
            this.value = value;
            this.pos = pos;
        }
    }

    private final String input;
    private int pos;
    private Token lookahead;

    private TagQueryParser(String input) {
        this.input = input;
        this.pos = 0;
        this.lookahead = nextToken();
    }

    public static TagExpression parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag query must not be empty");
        }
        String trimmed = input.trim();
        TagQueryParser parser = new TagQueryParser(trimmed);
        TagExpression expr = parser.parseOrExpr();
        if (parser.lookahead.type != TokenType.EOF) {
            throw new TagQueryParseException(trimmed, parser.lookahead.pos,
                    "Unexpected token: '" + parser.lookahead.value + "'");
        }
        return expr;
    }

    // or_expr := and_expr ('||' and_expr)*
    private TagExpression parseOrExpr() {
        TagExpression left = parseAndExpr();
        while (lookahead.type == TokenType.OR) {
            consume(TokenType.OR);
            TagExpression right = parseAndExpr();
            left = new OrNode(left, right);
        }
        return left;
    }

    // and_expr := not_expr ('&&' not_expr)*
    private TagExpression parseAndExpr() {
        TagExpression left = parseNotExpr();
        while (lookahead.type == TokenType.AND) {
            consume(TokenType.AND);
            TagExpression right = parseNotExpr();
            left = new AndNode(left, right);
        }
        return left;
    }

    // not_expr := '!' not_expr | primary
    private TagExpression parseNotExpr() {
        if (lookahead.type == TokenType.NOT) {
            consume(TokenType.NOT);
            return new NotNode(parseNotExpr());
        }
        return parsePrimary();
    }

    // primary := '(' expr ')' | tag
    private TagExpression parsePrimary() {
        if (lookahead.type == TokenType.LPAREN) {
            int openPos = lookahead.pos;
            consume(TokenType.LPAREN);
            TagExpression expr = parseOrExpr();
            if (lookahead.type != TokenType.RPAREN) {
                String got = lookahead.type == TokenType.EOF
                        ? "reached end of input"
                        : "got '" + lookahead.value + "'";
                throw new TagQueryParseException(input, openPos,
                        "Expected ')' to close '(' but " + got);
            }
            consume(TokenType.RPAREN);
            return expr;
        }
        if (lookahead.type == TokenType.TAG) {
            String tag = lookahead.value;
            consume(TokenType.TAG);
            return new TagNode(tag);
        }
        String got = lookahead.type == TokenType.EOF
                ? "reached end of input"
                : "got '" + lookahead.value + "'";
        throw new TagQueryParseException(input, lookahead.pos,
                "Expected a tag or '(' but " + got);
    }

    private void consume(TokenType expected) {
        // Only called after confirming lookahead.type matches — should never throw
        lookahead = nextToken();
    }

    private Token nextToken() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }

        if (pos >= input.length()) {
            return new Token(TokenType.EOF, "", pos);
        }

        int tokenStart = pos;
        char c = input.charAt(pos);

        switch (c) {
            case '(':
                pos++;
                return new Token(TokenType.LPAREN, "(", tokenStart);
            case ')':
                pos++;
                return new Token(TokenType.RPAREN, ")", tokenStart);
            case '!':
                pos++;
                return new Token(TokenType.NOT, "!", tokenStart);
            case '&':
                if (pos + 1 < input.length() && input.charAt(pos + 1) == '&') {
                    pos += 2;
                    return new Token(TokenType.AND, "&&", tokenStart);
                }
                throw new TagQueryParseException(input, tokenStart,
                        "Expected '&&' but got '&' — did you mean '&&'?");
            case '|':
                if (pos + 1 < input.length() && input.charAt(pos + 1) == '|') {
                    pos += 2;
                    return new Token(TokenType.OR, "||", tokenStart);
                }
                throw new TagQueryParseException(input, tokenStart,
                        "Expected '||' but got '|' — did you mean '||'?");
            case '"':
            case '\'': {
                char quote = c;
                pos++;
                StringBuilder sb = new StringBuilder();
                while (pos < input.length() && input.charAt(pos) != quote) {
                    sb.append(input.charAt(pos++));
                }
                if (pos >= input.length()) {
                    throw new TagQueryParseException(input, tokenStart,
                            "Unterminated string literal — missing closing " + quote);
                }
                pos++;
                return new Token(TokenType.TAG, sb.toString(), tokenStart);
            }
            default:
                if (Character.isLetterOrDigit(c) || c == '_' || c == '-') {
                    StringBuilder sb = new StringBuilder();
                    while (pos < input.length() &&
                            (Character.isLetterOrDigit(input.charAt(pos)) ||
                                    input.charAt(pos) == '_' ||
                                    input.charAt(pos) == '-')) {
                        sb.append(input.charAt(pos++));
                    }
                    return new Token(TokenType.TAG, sb.toString(), tokenStart);
                }
                throw new TagQueryParseException(input, tokenStart,
                        "Unexpected character '" + c + "'");
        }
    }

}
