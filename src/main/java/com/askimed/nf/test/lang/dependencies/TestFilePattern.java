package com.askimed.nf.test.lang.dependencies;

import java.io.File;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public  class TestFilePattern {

        private PathMatcher pathMatcher;

        private String testId = null;

        public TestFilePattern (File baseDir, String glob) {
            pathMatcher = pathMatcher("glob:" + baseDir.getAbsolutePath() + "/" + glob);
        }

        public TestFilePattern (File file) {
            String path = file.toPath().toAbsolutePath().normalize().toString();
            testId = extractTestId(path);
            if (testId != null) {
                path = removeTestId(path);
            }
            if (file.isDirectory()) {
                pathMatcher = pathMatcher("glob:" + path + "/**");
            } else {
                pathMatcher = pathMatcher("glob:" + path);
            }
        }

       public PathMatcher pathMatcher(String pattern) {
            return FileSystems.getDefault().getPathMatcher(pattern);
        }

        public String removeTestId(String string) {
            String regex = "@[a-fA-F0-9]{8}\\b";
            return string.replaceAll(regex, "");
        }

       private String extractTestId(String path) {
           String regex = "@[a-fA-F0-9]{8}\\b";
           Pattern pattern = Pattern.compile(regex);
           Matcher matcher = pattern.matcher(path);

           if (matcher.find()) {
               // Extract hash without the "@" character
               return matcher.group().substring(1);
           } else {
               return null;
           }
       }

       public boolean matches(Path path) {
            return pathMatcher.matches(path);
        }

       public String getTestId() {
           return testId;
       }

       public boolean hasTestId() {
            return testId != null;
       }

   }
