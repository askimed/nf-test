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

public class TestFile implements  IMetaFile {

    private File baseDir;

    private File file;

    private Set<String> dependencies = new HashSet<String>();

    public TestFile(File baseDir, File file) {
        this.baseDir = baseDir;
        this.file = file;
    }

    public void parseDependencies() throws IOException {
        String script = FileUtil.readFileAsString(file);
        String regex = "(?i)script\\s+\"(.+)\"";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(script);

        while (matcher.find()) {
            String dependency = matcher.group(1).trim();
            Path path = null;
            if (dependency.startsWith("./") || dependency.startsWith("../")) {
                path = Paths.get(file.getAbsoluteFile().getParentFile().getAbsolutePath()).resolve(dependency);
            } else {
                path = new File(baseDir, dependency).toPath();
            }
            if (!path.toFile().exists()){
                System.out.println("Warning: Module " + file.getAbsolutePath() + ": Dependency '" + path.toAbsolutePath() + "' not found." );
                continue;
            }
            dependencies.add(path.normalize().toFile().getAbsolutePath());
        }

    }

    public static boolean accepts(Path path) {
        return path.getFileName().toString().endsWith(".nf.test");
    }

    @Override
    public String getFilename() {
        return file.getAbsolutePath();
    }

    @Override
    public MetaFileType getType() {
        return MetaFileType.TEST_FILE;
    }

    @Override
    public Set<String> getDependencies() {
        return dependencies;
    }

}
