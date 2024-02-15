package com.askimed.nf.test.lang.dependencies;

import java.io.IOException;
import java.util.Set;

public interface IMetaFile {

    public Set<String> getDependencies();

    public MetaFileType getType();

    public String getFilename();

    public void parseDependencies() throws IOException;

    public static enum MetaFileType{
        SOURCE_FILE, TEST_FILE
    }

}
