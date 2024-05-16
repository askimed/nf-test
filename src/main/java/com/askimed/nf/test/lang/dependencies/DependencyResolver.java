package com.askimed.nf.test.lang.dependencies;

import com.askimed.nf.test.nextflow.NextflowScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;

public class DependencyResolver {

    private File baseDir;

    private DependencyGraph graph = new DependencyGraph();

    private List<PathMatcher> triggerPatterns = new Vector<PathMatcher>();

    private boolean followingDependencies = false;

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

    public void setFollowingDependencies(boolean followingDependencies) {
        this.followingDependencies = followingDependencies;
    }

    public List<File> findAllTests() throws Exception {

        List<File> results = new Vector<File>();
        for (IMetaFile metaFile: graph.getFiles()) {
            if (metaFile.getType() == IMetaFile.MetaFileType.TEST_FILE) {
                results.add(new File(metaFile.getFilename()));
            }
        }

        log.info("Found {} tests.", results.size());

        return results;
    }

    public List<File> findTestsByFiles(List<File> files) throws Exception {

        List<TestFilePattern> patterns = new Vector<TestFilePattern>();
        for (File file: files) {
            patterns.add(fileToPathMatcher(file));
        }

        List<File> results = new Vector<File>();
        for (IMetaFile metaFile: graph.getFiles()) {
            if (metaFile.getType() == IMetaFile.MetaFileType.TEST_FILE) {
                File file = new File(metaFile.getFilename());
                TestFilePattern matchedPattern = matches(file.toPath(), patterns);
                if (matchedPattern != null) {
                    if (matchedPattern.hasTestId()) {
                        results.add(new File(file + "@" + matchedPattern.getTestId()));
                    } else {
                        results.add(file);
                    }
                }
            }
        }

        log.info("Found {} tests.", results.size());
        log.debug("Found tests: " + results);

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

        for (File file: files) {
            if (matches2(file.getAbsoluteFile().toPath(), triggerPatterns)) {
                log.info("File " + file.getAbsolutePath() + " triggers full test run.");
                return findAllTests();
            }
        }

        Set<File> results = new HashSet<File>();

        long time0 = System.currentTimeMillis();
        for (File file: files) {
            results.addAll(findRelatedTestsByFile(file.getAbsoluteFile(), followingDependencies));
        }
        long time1 = System.currentTimeMillis();
        log.info("Found {} tests for file {} in {} sec", results.size(), files, (time1 - time0) / 1000.0);

        return new Vector<File>(results);
    }

    private Set<File> findRelatedTestsByFile(File file, boolean followingDependencies) throws Exception {

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
                // if a source file
                DependencyGraph.Node node = graph.getNode(dependency.getFilename());
                //TODO: add && !followingDependencies
                if (node.hasDependencyOfType(IMetaFile.MetaFileType.TEST_FILE) && !followingDependencies) {
                    //has a test --> add all test and then stop
                    for (DependencyGraph.Node dependencyOfDependency: node.getDependencies()) {
                        if (dependencyOfDependency.getMetaFile().getType() == IMetaFile.MetaFileType.TEST_FILE) {
                            results.add(new File(dependencyOfDependency.getFilename()));
                        }
                    }
                } else {
                     //has no tests --> find related tests in a recursive way
                    results.addAll(findRelatedTestsByFile(dependencyFile, followingDependencies));
                }
            }
        }

        return results;
    }


    public void buildGraph() throws Exception {
        buildGraph(new Vector<String>(), new Vector<String>());
    }

    public void buildGraph(String ... ignoreGlobs) throws Exception {
        List<String> list = new Vector<>();
        Collections.addAll(list, ignoreGlobs);
        buildGraph(list, new Vector<>());
    }

    public void buildGraph(List<String> ignoreGlobs, List<String> triggerPatterns) throws Exception {

        for (String glob: triggerPatterns) {
            this.triggerPatterns.add(pathMatcher("glob:" + baseDir.getAbsolutePath() + "/" + glob));
        }

        List<TestFilePattern> ignorePatterns = new Vector<TestFilePattern>();
        ignorePatterns.add(fileToPathMatcher(".nf-test/**"));
        ignorePatterns.add(fileToPathMatcher("src/**"));
        ignorePatterns.add(fileToPathMatcher("target/**"));
        for (String ignoreGlob: ignoreGlobs) {
            ignorePatterns.add(fileToPathMatcher(ignoreGlob));
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

                if (matches(path, ignorePatterns) != null) {
                    //log.warn("Ignored file " + path);
                    return;
                }

                if (!Files.isRegularFile(path)) {
                    return;
                }

                IMetaFile file = null;
                if (TestFile.accepts(path)) {
                    file = new TestFile(baseDir, path.toFile());
                } else if (NextflowScript.accepts(path)) {
                    file = new NextflowScript(path.toFile());
                } else if (SnapshotFile.accepts(path)) {
                   file = new SnapshotFile(path.toFile());
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

        });

        graph.connectDependencies();

        long time1 = System.currentTimeMillis();

        log.info("Loaded {} files from directory {} in {} sec", graph.size(), baseDir.getAbsolutePath(), (time1 - time0) / 1000.0);

    }

    public TestFilePattern fileToPathMatcher(String glob) {
        return new TestFilePattern(baseDir, glob);
    }

    public TestFilePattern fileToPathMatcher(File file) {
        return new TestFilePattern(file);
    }


    public PathMatcher pathMatcher(String pattern) {
        System.out.println(pattern);
        return FileSystems.getDefault().getPathMatcher(pattern);
    }

    public TestFilePattern matches(Path path, List<TestFilePattern> ignorePatterns) {
        PathMatcher pathMatcher;
        for (TestFilePattern pattern : ignorePatterns) {
            if (pattern.matches(path)) {
                return pattern;
            }
        }
        return null;
    }

    public boolean matches2(Path path, List<PathMatcher> ignorePatterns) {
        PathMatcher pathMatcher;
        for (PathMatcher pattern : ignorePatterns) {
            if (pattern.matches(path)) {
                return true;
            }
        }
        return false;
    }


}
