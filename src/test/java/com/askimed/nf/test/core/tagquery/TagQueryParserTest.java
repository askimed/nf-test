package com.askimed.nf.test.core.tagquery;

import com.askimed.nf.test.core.ITaggable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TagQueryParserTest {

    private ITaggable taggable(String name, List<String> tags) {
        return new ITaggable() {
            public String getName() { return name; }
            public List<String> getTags() { return tags; }
            public ITaggable getParent() { return null; }
        };
    }

    private ITaggable taggable(String name, List<String> tags, ITaggable parent) {
        return new ITaggable() {
            public String getName() { return name; }
            public List<String> getTags() { return tags; }
            public ITaggable getParent() { return parent; }
        };
    }

    // --- Parser structure ---

    @Test
    public void parseSingleTag() {
        TagExpression expr = TagQueryParser.parse("foo");
        assertInstanceOf(TagNode.class, expr);
        assertEquals("foo", ((TagNode) expr).getTag());
    }

    @Test
    public void parseDoubleQuotedTag() {
        TagExpression expr = TagQueryParser.parse("\"suite 1\"");
        assertInstanceOf(TagNode.class, expr);
        assertEquals("suite 1", ((TagNode) expr).getTag());
    }

    @Test
    public void parseSingleQuotedTag() {
        TagExpression expr = TagQueryParser.parse("'suite 1'");
        assertInstanceOf(TagNode.class, expr);
        assertEquals("suite 1", ((TagNode) expr).getTag());
    }

    @Test
    public void parseNotExpr() {
        TagExpression expr = TagQueryParser.parse("!foo");
        assertInstanceOf(NotNode.class, expr);
        assertInstanceOf(TagNode.class, ((NotNode) expr).getOperand());
    }

    @Test
    public void parseDoubleNot() {
        TagExpression expr = TagQueryParser.parse("!!foo");
        assertInstanceOf(NotNode.class, expr);
        assertInstanceOf(NotNode.class, ((NotNode) expr).getOperand());
    }

    @Test
    public void parseAndExpr() {
        TagExpression expr = TagQueryParser.parse("foo && bar");
        assertInstanceOf(AndNode.class, expr);
        assertInstanceOf(TagNode.class, ((AndNode) expr).getLeft());
        assertInstanceOf(TagNode.class, ((AndNode) expr).getRight());
    }

    @Test
    public void parseOrExpr() {
        TagExpression expr = TagQueryParser.parse("foo || bar");
        assertInstanceOf(OrNode.class, expr);
        assertInstanceOf(TagNode.class, ((OrNode) expr).getLeft());
        assertInstanceOf(TagNode.class, ((OrNode) expr).getRight());
    }

    @Test
    public void andBindsTighterThanOr() {
        // a || b && c should parse as a || (b && c)
        TagExpression expr = TagQueryParser.parse("a || b && c");
        assertInstanceOf(OrNode.class, expr);
        assertInstanceOf(TagNode.class, ((OrNode) expr).getLeft());
        assertInstanceOf(AndNode.class, ((OrNode) expr).getRight());
    }

    @Test
    public void parenthesesOverridePrecedence() {
        // (a || b) && c
        TagExpression expr = TagQueryParser.parse("(a || b) && c");
        assertInstanceOf(AndNode.class, expr);
        assertInstanceOf(OrNode.class, ((AndNode) expr).getLeft());
        assertInstanceOf(TagNode.class, ((AndNode) expr).getRight());
    }

    @Test
    public void parseComplexExpr() {
        // (!foo && bar) || baz
        TagExpression expr = TagQueryParser.parse("(!foo && bar) || baz");
        assertInstanceOf(OrNode.class, expr);
        AndNode and = (AndNode) ((OrNode) expr).getLeft();
        assertInstanceOf(NotNode.class, and.getLeft());
        assertInstanceOf(TagNode.class, and.getRight());
        assertInstanceOf(TagNode.class, ((OrNode) expr).getRight());
    }

    // --- Evaluation ---

    @Test
    public void evaluateSingleTag() {
        TagExpression expr = TagQueryParser.parse("foo");
        assertTrue(expr.evaluate(taggable("test", List.of("foo", "bar"))));
        assertFalse(expr.evaluate(taggable("test", List.of("bar"))));
    }

    @Test
    public void evaluateMatchesName() {
        TagExpression expr = TagQueryParser.parse("mytest");
        assertTrue(expr.evaluate(taggable("mytest", List.of())));
        assertFalse(expr.evaluate(taggable("other", List.of())));
    }

    @Test
    public void evaluateCaseInsensitive() {
        TagExpression expr = TagQueryParser.parse("FOO");
        assertTrue(expr.evaluate(taggable("test", List.of("foo"))));
        assertTrue(expr.evaluate(taggable("test", List.of("FOO"))));
        assertTrue(expr.evaluate(taggable("test", List.of("Foo"))));
    }

    @Test
    public void evaluateNot() {
        TagExpression expr = TagQueryParser.parse("!foo");
        assertFalse(expr.evaluate(taggable("test", List.of("foo"))));
        assertTrue(expr.evaluate(taggable("test", List.of("bar"))));
    }

    @Test
    public void evaluateAnd() {
        TagExpression expr = TagQueryParser.parse("foo && bar");
        assertTrue(expr.evaluate(taggable("test", List.of("foo", "bar"))));
        assertFalse(expr.evaluate(taggable("test", List.of("foo"))));
        assertFalse(expr.evaluate(taggable("test", List.of("bar"))));
        assertFalse(expr.evaluate(taggable("test", List.of())));
    }

    @Test
    public void evaluateOr() {
        TagExpression expr = TagQueryParser.parse("foo || bar");
        assertTrue(expr.evaluate(taggable("test", List.of("foo"))));
        assertTrue(expr.evaluate(taggable("test", List.of("bar"))));
        assertTrue(expr.evaluate(taggable("test", List.of("foo", "bar"))));
        assertFalse(expr.evaluate(taggable("test", List.of("baz"))));
    }

    @Test
    public void evaluateComplex() {
        TagExpression expr = TagQueryParser.parse("(!foo && bar) || baz");
        assertTrue(expr.evaluate(taggable("test", List.of("bar"))));           // bar, no foo → true
        assertTrue(expr.evaluate(taggable("test", List.of("baz"))));           // baz → true
        assertTrue(expr.evaluate(taggable("test", List.of("bar", "baz"))));    // both paths true
        assertFalse(expr.evaluate(taggable("test", List.of("foo", "bar"))));   // foo negates → false
        assertFalse(expr.evaluate(taggable("test", List.of("foo"))));          // foo only → false
    }

    @Test
    public void evaluateInheritsParentTags() {
        ITaggable parent = taggable("suite", List.of("suite-tag"));
        ITaggable child = taggable("test", List.of("child-tag"), parent);
        assertTrue(TagQueryParser.parse("suite-tag").evaluate(child));
        assertTrue(TagQueryParser.parse("child-tag").evaluate(child));
        assertFalse(TagQueryParser.parse("other").evaluate(child));
    }

    @Test
    public void evaluateDoubleQuotedMultiWordTag() {
        TagExpression expr = TagQueryParser.parse("\"suite 1\"");
        assertTrue(expr.evaluate(taggable("suite 1", List.of())));
        assertFalse(expr.evaluate(taggable("suite 2", List.of())));
    }

    @Test
    public void evaluateSingleQuotedMultiWordTag() {
        TagExpression expr = TagQueryParser.parse("'suite 1'");
        assertTrue(expr.evaluate(taggable("suite 1", List.of())));
        assertFalse(expr.evaluate(taggable("suite 2", List.of())));
    }

    // --- Error cases ---

    @Test
    public void throwsOnEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> TagQueryParser.parse(""));
        assertThrows(IllegalArgumentException.class, () -> TagQueryParser.parse("   "));
        assertThrows(IllegalArgumentException.class, () -> TagQueryParser.parse(null));
    }

    @Test
    public void throwsOnUnmatchedOpenParen() {
        TagQueryParseException ex = assertThrows(TagQueryParseException.class,
                () -> TagQueryParser.parse("(foo"));
        assertEquals(0, ex.getPosition());
    }

    @Test
    public void throwsOnUnmatchedCloseParen() {
        TagQueryParseException ex = assertThrows(TagQueryParseException.class,
                () -> TagQueryParser.parse("foo)"));
        assertEquals(3, ex.getPosition());
    }

    @Test
    public void throwsOnUnterminatedString() {
        TagQueryParseException ex1 = assertThrows(TagQueryParseException.class,
                () -> TagQueryParser.parse("\"foo"));
        assertEquals(0, ex1.getPosition());

        TagQueryParseException ex2 = assertThrows(TagQueryParseException.class,
                () -> TagQueryParser.parse("'foo"));
        assertEquals(0, ex2.getPosition());
    }

    @Test
    public void throwsOnSingleAmpersand() {
        TagQueryParseException ex = assertThrows(TagQueryParseException.class,
                () -> TagQueryParser.parse("foo & bar"));
        assertEquals(4, ex.getPosition());
    }

    @Test
    public void throwsOnSinglePipe() {
        TagQueryParseException ex = assertThrows(TagQueryParseException.class,
                () -> TagQueryParser.parse("foo | bar"));
        assertEquals(4, ex.getPosition());
    }

    @Test
    public void throwsOnTrailingOperator() {
        TagQueryParseException ex = assertThrows(TagQueryParseException.class,
                () -> TagQueryParser.parse("foo &&"));
        assertEquals(6, ex.getPosition());
    }

    @Test
    public void errorMessageContainsCaretPointer() {
        TagQueryParseException ex = assertThrows(TagQueryParseException.class,
                () -> TagQueryParser.parse("foo & bar"));
        String message = ex.getMessage();
        // Message should contain the query and a caret at position 4 (after "foo ")
        assertTrue(message.contains("foo & bar"), "message should contain original query");
        assertTrue(message.contains("^"), "message should contain caret pointer");
    }

}
