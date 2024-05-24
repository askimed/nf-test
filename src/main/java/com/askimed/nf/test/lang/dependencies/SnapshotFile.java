package com.askimed.nf.test.lang.dependencies;

import com.askimed.nf.test.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnapshotFile implements  IMetaFile {

    private File file;

    private Set<String> dependencies = new HashSet<String>();

    public SnapshotFile(File file) {
        this.file = file;
    }

    public void parseDependencies() throws IOException {


    }

    public static boolean accepts(Path path) {
        return path.getFileName().toString().endsWith(".nf.test.snap") || path.getFileName().toString().endsWith(".groovy.test.snap");
    }

    @Override
    public String getFilename() {
        return file.getAbsolutePath();
    }

    @Override
    public MetaFileType getType() {
        return MetaFileType.SNAPSHOT_FILE;
    }

    @Override
    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public TargetType getTarget() {
        return TargetType.UNDEFINED;
    }

}
