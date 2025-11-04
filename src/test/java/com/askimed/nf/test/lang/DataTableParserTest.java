package com.askimed.nf.test.lang;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.askimed.nf.test.lang.DataTableParser.DataTable;

public class DataTableParserTest {

    @Test
    public void testDataTableParsing() {
        String dataTableText = 
            "name      | number | expected\n" +
            "\"alice\"   | 1      | true\n" +
            "\"bob\"     | 2      | false\n" +
            "\"charlie\" | 3      | true";

        DataTable dataTable = DataTableParser.parseWhereBlock(dataTableText);
        
        assertEquals(3, dataTable.size());
        assertEquals(3, dataTable.getParameterNames().size());
        assertTrue(dataTable.getParameterNames().contains("name"));
        assertTrue(dataTable.getParameterNames().contains("number"));
        assertTrue(dataTable.getParameterNames().contains("expected"));

        Map<String, Object> firstRow = dataTable.getRows().get(0);
        assertEquals("alice", firstRow.get("name"));
        assertEquals(1, firstRow.get("number"));
        assertEquals(true, firstRow.get("expected"));
    }

    @Test
    public void testDataPipesParsing() {
        String dataPipesText = 
            "name << [\"alice\", \"bob\", \"charlie\"]\n" +
            "number << [1, 2, 3]\n" +
            "expected << [true, false, true]";

        DataTable dataTable = DataTableParser.parseWhereBlock(dataPipesText);
        
        assertEquals(3, dataTable.size());
        assertEquals(3, dataTable.getParameterNames().size());

        Map<String, Object> firstRow = dataTable.getRows().get(0);
        assertEquals("alice", firstRow.get("name"));
        assertEquals(1, firstRow.get("number"));
        assertEquals(true, firstRow.get("expected"));
    }

    @Test
    public void testMixedDataPipesAndAssignments() {
        String mixedText = 
            "name << [\"test1\", \"test2\"]\n" +
            "number << [10, 20]\n" +
            "result = name + \"-\" + number";

        DataTable dataTable = DataTableParser.parseWhereBlock(mixedText);
        
        assertEquals(2, dataTable.size());
        assertEquals(3, dataTable.getParameterNames().size());

        Map<String, Object> firstRow = dataTable.getRows().get(0);
        assertEquals("test1", firstRow.get("name"));
        assertEquals(10, firstRow.get("number"));
        assertEquals("test1-10", firstRow.get("result"));
    }
}