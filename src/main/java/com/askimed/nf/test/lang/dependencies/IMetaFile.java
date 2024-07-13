package com.askimed.nf.test.lang.dependencies;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public interface IMetaFile {

    public Set<String> getDependencies();

    public MetaFileType getType();

    public TargetType getTarget();

    public String getFilename();

    public void parseDependencies() throws IOException;

    public static enum MetaFileType{
        SOURCE_FILE, TEST_FILE, SNAPSHOT_FILE
    }

    public static enum TargetType{
        PROCESS, WORKFLOW, PIPELINE, FUNCTION, UNDEFINED;

        public static Set<TargetType> parse(String targets) {
            Set<TargetType> result = new HashSet<TargetType>();
            String cleaned = targets.trim().toUpperCase();
            if (cleaned.isEmpty() || cleaned.equalsIgnoreCase("ALL")) {
                return result;
            }
            for (String target: cleaned.split(",")) {
                String cleanedTarget = target.trim();
                result.add(TargetType.valueOf(cleanedTarget));
            }
            return result;
        }

    }

}
