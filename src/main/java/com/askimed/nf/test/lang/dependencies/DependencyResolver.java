package com.askimed.nf.test.lang.dependencies;

import com.askimed.nf.test.nextflow.NextflowScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public List<File> findRelatedTestsByFile(List<File> files) throws Exception {
        File[] array = new File[files.size()];
        for (int i = 0 ; i < array.length; i++) {
            array[i] = new File(files.get(i).getAbsolutePath());
        }
        return new Vector<File>(findRelatedTestsByFile(array));
    }

    public List<File> findRelatedTestsByFile(File ... files) throws Exception {

        Set<File> results = new HashSet<File>();

        long time0 = System.currentTimeMillis();
        for (File file: files) {
            results.addAll(_findRelatedTestsByFile(file.getAbsoluteFile()));
        }
        long time1 = System.currentTimeMillis();
        log.info("Found {} tests for file {} in {} sec", results.size(), files, (time1 - time0) / 1000.0);

        return new Vector<File>(results);
    }

    public Set<File> _findRelatedTestsByFile(File file) throws Exception {

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
                results.addAll(_findRelatedTestsByFile(dependencyFile));
            }
        }

        return results;
    }

    //TODO: own class.
    public void buildGraph() throws Exception {
        //TODO: add type: all, tests, ...
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

                if (path.toString().contains(".nf-test")) {
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

        });

        graph.connectDependencies();

        long time1 = System.currentTimeMillis();

        log.info("Loaded {} files from directory {} in {} sec", graph.size(), baseDir.getAbsolutePath(), (time1 - time0) / 1000.0);

    }


    public Coverage getConverage(){

        long time0 = System.currentTimeMillis();

        Coverage coverage = new Coverage();
        for (DependencyGraph.Node node: graph.getNodes()){
            if (node.getMetaFile().getType() != IMetaFile.MetaFileType.SOURCE_FILE) {
                continue;
            }

            coverage.add(new File(node.getMetaFile().getFilename()),
                    node.hasDependencyOfType(IMetaFile.MetaFileType.TEST_FILE));

        }

        long time1 = System.currentTimeMillis();

        log.info("Calculated coverage for {} files in {} sec", graph.size(), (time1 - time0) / 1000.0);

        return coverage;
    }


    public static class Coverage {

        private int coveredItems = 0;

        private List<CoverageItem> items = new Vector<CoverageItem>();

        public List<CoverageItem> getItems() {
            return items;
        }

        public int getCoveredItems() {
            return coveredItems;
        }

        public void add(File file, boolean covered) {
            items.add(new CoverageItem(file, covered));
            if (covered) {
                coveredItems++;
            }
        }

    }

    public static class CoverageItem {

        private File file;

        private boolean covered = false;

        public CoverageItem(File file, boolean covered) {
            this.file = file;
            this.covered = covered;
        }

        public File getFile() {
            return file;
        }

        public boolean isCovered() {
            return covered;
        }
    }

}
