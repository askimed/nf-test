package com.askimed.nf.test.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTableParser {

    private static final Pattern PIPE_SEPARATOR = Pattern.compile("\\s*\\|{1,2}\\s*");
    private static final Pattern DATA_PIPE = Pattern.compile("(\\w+)\\s*<<\\s*(.+)");
    private static final Pattern VARIABLE_ASSIGNMENT = Pattern.compile("(\\w+)\\s*=\\s*(.+)");

    public static class DataTable {
        private List<String> parameterNames;
        private List<Map<String, Object>> rows;

        public DataTable(List<String> parameterNames, List<Map<String, Object>> rows) {
            this.parameterNames = parameterNames;
            this.rows = rows;
        }

        public List<String> getParameterNames() {
            return parameterNames;
        }

        public List<Map<String, Object>> getRows() {
            return rows;
        }

        public int size() {
            return rows.size();
        }
    }

    /**
     * Parse a Spock-style where block that can contain:
     * 1. Data tables (a | b || c)
     * 2. Data pipes (a << [1,2,3])
     * 3. Variable assignments (c = a + b)
     */
    public static DataTable parseWhereBlock(String whereBlockText) {
        if (whereBlockText == null || whereBlockText.trim().isEmpty()) {
            throw new IllegalArgumentException("Where block text cannot be empty");
        }

        String[] lines = whereBlockText.trim().split("\\n");
        
        // Check if this is a data table (contains | characters)
        if (containsDataTable(lines)) {
            return parseDataTable(lines);
        }
        
        // Otherwise, parse as data pipes and variable assignments
        return parseDataPipesAndAssignments(lines);
    }

    private static boolean containsDataTable(String[] lines) {
        for (String line : lines) {
            if (line.trim().contains("|")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parse traditional data table format:
     * name     | number | expected
     * "alice"  | 1      | true
     * "bob"    | 2      | false
     */
    private static DataTable parseDataTable(String[] lines) {
        if (lines.length < 2) {
            throw new IllegalArgumentException("Data table must have at least a header row and one data row");
        }

        // Parse header row to get parameter names
        String headerLine = lines[0].trim();
        String[] headers = PIPE_SEPARATOR.split(headerLine);
        List<String> parameterNames = new ArrayList<String>();
        for (String header : headers) {
            String trimmed = header.trim();
            if (!trimmed.isEmpty()) {
                parameterNames.add(trimmed);
            }
        }

        // Parse data rows
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (int i = 1; i < lines.length; i++) {
            String dataLine = lines[i].trim();
            if (dataLine.isEmpty()) {
                continue; // Skip empty lines
            }

            String[] values = PIPE_SEPARATOR.split(dataLine);
            if (values.length != parameterNames.size()) {
                throw new IllegalArgumentException("Data row " + i + " has " + values.length + 
                    " values but header has " + parameterNames.size() + " parameters");
            }

            Map<String, Object> row = new HashMap<String, Object>();
            for (int j = 0; j < parameterNames.size(); j++) {
                String rawValue = values[j].trim();
                Object parsedValue = parseValue(rawValue);
                row.put(parameterNames.get(j), parsedValue);
            }
            rows.add(row);
        }

        return new DataTable(parameterNames, rows);
    }

    /**
     * Parse data pipes and variable assignments:
     * a << [1, 2, 3]
     * b << ["x", "y", "z"]
     * c = a + b.length()
     */
    private static DataTable parseDataPipesAndAssignments(String[] lines) {
        Map<String, List<Object>> dataPipes = new HashMap<String, List<Object>>();
        Map<String, String> assignments = new HashMap<String, String>();
        List<String> parameterNames = new ArrayList<String>();

        // First pass: parse data pipes and assignments
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            Matcher dataPipeMatcher = DATA_PIPE.matcher(line);
            Matcher assignmentMatcher = VARIABLE_ASSIGNMENT.matcher(line);

            if (dataPipeMatcher.matches()) {
                String varName = dataPipeMatcher.group(1).trim();
                String dataExpr = dataPipeMatcher.group(2).trim();
                List<Object> values = parseDataPipeExpression(dataExpr);
                dataPipes.put(varName, values);
                if (!parameterNames.contains(varName)) {
                    parameterNames.add(varName);
                }
            } else if (assignmentMatcher.matches()) {
                String varName = assignmentMatcher.group(1).trim();
                String expression = assignmentMatcher.group(2).trim();
                assignments.put(varName, expression);
                if (!parameterNames.contains(varName)) {
                    parameterNames.add(varName);
                }
            }
        }

        // Determine number of iterations (max size of data pipes)
        int maxIterations = 0;
        for (List<Object> values : dataPipes.values()) {
            maxIterations = Math.max(maxIterations, values.size());
        }

        if (maxIterations == 0 && assignments.isEmpty()) {
            throw new IllegalArgumentException("No data pipes or assignments found in where block");
        }

        // Generate rows
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < Math.max(maxIterations, 1); i++) {
            Map<String, Object> row = new HashMap<String, Object>();
            
            // Add data pipe values
            for (Map.Entry<String, List<Object>> entry : dataPipes.entrySet()) {
                String varName = entry.getKey();
                List<Object> values = entry.getValue();
                Object value = (i < values.size()) ? values.get(i) : values.get(values.size() - 1);
                row.put(varName, value);
            }
            
            // Evaluate assignments (simplified - in real implementation would need expression evaluator)
            for (Map.Entry<String, String> entry : assignments.entrySet()) {
                String varName = entry.getKey();
                String expression = entry.getValue();
                Object value = evaluateSimpleExpression(expression, row);
                row.put(varName, value);
            }
            
            rows.add(row);
        }

        return new DataTable(parameterNames, rows);
    }

    /**
     * Parse data pipe expressions like [1, 2, 3] or ["a", "b", "c"]
     */
    private static List<Object> parseDataPipeExpression(String expression) {
        List<Object> values = new ArrayList<Object>();
        
        // Remove brackets if present
        expression = expression.trim();
        if (expression.startsWith("[") && expression.endsWith("]")) {
            expression = expression.substring(1, expression.length() - 1);
        }
        
        // Split by comma and parse each value
        String[] parts = expression.split(",");
        for (String part : parts) {
            values.add(parseValue(part.trim()));
        }
        
        return values;
    }

    /**
     * Simple expression evaluator for basic arithmetic and string operations
     */
    private static Object evaluateSimpleExpression(String expression, Map<String, Object> variables) {
        // This is a very simplified evaluator
        // Handle basic string concatenation with + operator
        
        // Replace variables in the expression
        for (Map.Entry<String, Object> var : variables.entrySet()) {
            String varName = var.getKey();
            Object varValue = var.getValue();
            // Use word boundaries to avoid partial replacements
            expression = expression.replaceAll("\\b" + varName + "\\b", 
                varValue instanceof String ? "\"" + varValue + "\"" : String.valueOf(varValue));
        }
        
        // Handle simple string concatenation: "a" + "b" + "c"
        if (expression.contains(" + ")) {
            return evaluateStringConcatenation(expression);
        }
        
        // Try to evaluate as a simple value
        return parseValue(expression);
    }
    
    /**
     * Evaluate simple string concatenation expressions
     */
    private static String evaluateStringConcatenation(String expression) {
        String[] parts = expression.split("\\s*\\+\\s*");
        StringBuilder result = new StringBuilder();
        
        for (String part : parts) {
            part = part.trim();
            // Remove quotes if present
            if ((part.startsWith("\"") && part.endsWith("\"")) || 
                (part.startsWith("'") && part.endsWith("'"))) {
                result.append(part.substring(1, part.length() - 1));
            } else {
                // It's a number or unquoted value
                result.append(part);
            }
        }
        
        return result.toString();
    }

    /**
     * Parse individual values, handling strings, numbers, booleans, etc.
     */
    private static Object parseValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        value = value.trim();

        // Handle quoted strings
        if ((value.startsWith("\"") && value.endsWith("\"")) || 
            (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }

        // Handle booleans
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }

        // Handle null
        if ("null".equalsIgnoreCase(value)) {
            return null;
        }

        // Try to parse as integer
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Not an integer, continue
        }

        // Try to parse as double
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // Not a number, return as string
        }

        // Return as string
        return value;
    }

    /**
     * Parse variable assignments like "x = 1, y = 2"
     */
    public static Map<String, Object> parseVariableAssignments(String assignments) {
        Map<String, Object> variables = new HashMap<String, Object>();
        
        if (assignments == null || assignments.trim().isEmpty()) {
            return variables;
        }

        String[] parts = assignments.split(",");
        for (String part : parts) {
            Matcher matcher = VARIABLE_ASSIGNMENT.matcher(part.trim());
            if (matcher.matches()) {
                String varName = matcher.group(1).trim();
                String varValue = matcher.group(2).trim();
                variables.put(varName, parseValue(varValue));
            }
        }

        return variables;
    }
}