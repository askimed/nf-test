package com.askimed.nf.test.lang;

import com.askimed.nf.test.core.ITestSuite;
import com.askimed.nf.test.lang.DataTableParser.DataTable;

import groovy.lang.Closure;

public class DataDrivenTest {

    private ITestSuite testSuite;
    private DataTable dataTable;
    private Closure setupClosure;
    private Closure whenClosure;
    private Closure thenClosure;
    private Closure cleanupClosure;

    public DataDrivenTest(ITestSuite testSuite) {
        this.testSuite = testSuite;
    }

    public void setup(Closure closure) {
        this.setupClosure = closure;
    }

    public void when(Closure closure) {
        this.whenClosure = closure;
    }

    public void then(Closure closure) {
        this.thenClosure = closure;
    }

    public void cleanup(Closure closure) {
        this.cleanupClosure = closure;
    }

    /**
     * Parse the where block using Spock-style syntax supporting:
     * 1. Data tables: a | b || c
     * 2. Data pipes: a << [1,2,3]
     * 3. Variable assignments: c = a + b
     */
    public void where(String whereBlockText) {
        this.dataTable = DataTableParser.parseWhereBlock(whereBlockText);
    }

    /**
     * Alternative where method that accepts a closure for Groovy DSL style
     * This would capture the closure content and parse it
     */
    public void where(Closure closure) {
        // TODO: Implement closure-based where blocks
        // This would require more sophisticated AST parsing of the closure
        throw new UnsupportedOperationException("Closure-based where blocks not yet implemented. Please use string-based where blocks.");
    }

    // Getters
    public DataTable getDataTable() {
        return dataTable;
    }

    public Closure getSetupClosure() {
        return setupClosure;
    }

    public Closure getWhenClosure() {
        return whenClosure;
    }

    public Closure getThenClosure() {
        return thenClosure;
    }

    public Closure getCleanupClosure() {
        return cleanupClosure;
    }

    public ITestSuite getTestSuite() {
        return testSuite;
    }
}