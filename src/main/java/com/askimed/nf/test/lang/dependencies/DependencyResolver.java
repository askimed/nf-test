package com.askimed.nf.test.lang.dependencies;

import com.askimed.nf.test.nextflow.NextflowScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.function.Consumer;

public class DependencyResolver {

    private File baseDir;

    private DependencyGraph graph = new DependencyGraph();

    private static Logger log = LoggerFactory.getLogger(DependencyResolver.class);

    public DependencyResolver(File baseDir) {
        this.baseDir = baseDir;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public DependencyGraph getGraph() {
        return graph;
    }

    public Set<File> findAllTests() throws Exception {

        Set<File> results = new HashSet<File>();
        for (IMetaFile metaFile: graph.getFiles()) {
            if (metaFile.getType() == IMetaFile.MetaFileType.TEST_FILE) {
                results.add(new File(metaFile.getFilename()));
            }
        }

        log.info("Found {} tests.", results.size());

        return results;
    }

    public List<File> findRelatedTestsByFiles(List<File> files) throws Exception {
        File[] array = new File[files.size()];
        for (int i = 0 ; i < array.length; i++) {
            array[i] = new File(files.get(i).getAbsolutePath());
        }
        return new Vector<File>(findRelatedTestsByFiles(array));
    }

    public List<File> findRelatedTestsByFiles(File ... files) throws Exception {

        Set<File> results = new HashSet<File>();

        long time0 = System.currentTimeMillis();
        for (File file: files) {
            results.addAll(findRelatedTestsByFile(file.getAbsoluteFile()));
        }
        long time1 = System.currentTimeMillis();
        log.info("Found {} tests for file {} in {} sec", results.size(), files, (time1 - time0) / 1000.0);

        return new Vector<File>(results);
    }

    private Set<File> findRelatedTestsByFile(File file) throws Exception {

        Set<File> results = new HashSet<File>();

        IMetaFile metaFile = graph.getFile(file.toPath().normalize().toFile().getAbsolutePath());

        //not in graph -> no tests available
        if (metaFile == null) {
            return results;
        }

        // the file is a test file
        if (metaFile.getType() == IMetaFile.MetaFileType.TEST_FILE){
            results.add(new File(metaFile.getFilename()));
            return results;
        }

        // the file is a source file

        List<IMetaFile> dependencies = graph.getDependencies(metaFile.getFilename());
        for (IMetaFile dependency: dependencies) {
            File dependencyFile = new File(dependency.getFilename());
            if (dependency.getType() == IMetaFile.MetaFileType.TEST_FILE) {
                // is a test file --> return
                results.add(dependencyFile);
            } else {
                // if a source file -> find related tests in a recursive way
                results.addAll(findRelatedTestsByFile(dependencyFile));
            }
        }

        return results;
    }


    public void buildGraph() throws Exception {
        buildGraph(new Vector<String>());
    }

    public void buildGraph(List<String> ignoreGlobs) throws Exception {

        List<PathMatcher> ignorePatterns = new Vector<PathMatcher>();
        ignorePatterns.add(globToPathMatcher(".nf-test/**"));
        for (String ignoreGlob: ignoreGlobs) {
            ignorePatterns.add(globToPathMatcher(ignoreGlob));
        }

        if (!baseDir.exists()) {
            throw new Exception("Test directory '" + baseDir.getAbsolutePath() + "' not found.");
        }

        if (!baseDir.isDirectory()) {
            throw new IOException("Root '" + baseDir.getAbsolutePath() + "' is not a directory");
        }

        long time0 = System.currentTimeMillis();

        Files.walk(Paths.get(baseDir.getAbsolutePath())).forEach(new Consumer<Path>() {

            @Override
            public void accept(Path path) {

                if (isIgnored(path)) {
                    log.warn("Ignored file " + path);
                    return;
                }

                if (!Files.isRegularFile(path)) {
                    return;
                }

                IMetaFile file = null;
                if (TestFile.accepts(path)) {
                    file = new TestFile(baseDir, path.toFile());
                } else if (NextflowScript.accepts(path)) {
                    file= new NextflowScript(path.toFile());
                }
                if (file != null) {
                    try {
                        file.parseDependencies();
                        graph.addFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            public boolean isIgnored(Path path) {
                PathMatcher pathMatcher;
                for (PathMatcher pattern : ignorePatterns) {
                    pathMatcher = pattern;
                    if (pathMatcher.matches(path)) {
                        return true;
                    }
                }
                return false;
            }

        });

        graph.connectDependencies();

        long time1 = System.currentTimeMillis();

        log.info("Loaded {} files from directory {} in {} sec", graph.size(), baseDir.getAbsolutePath(), (time1 - time0) / 1000.0);

    }

    public PathMatcher globToPathMatcher(String glob) {
        return FileSystems.getDefault().getPathMatcher("glob:" + baseDir.getAbsolutePath() + "/" + glob);
    }

}
