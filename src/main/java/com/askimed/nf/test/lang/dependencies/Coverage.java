package com.askimed.nf.test.lang.dependencies;

import com.askimed.nf.test.util.AnsiColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

public class Coverage {

    private int coveredItems = 0;

    private DependencyGraph graph;

    private List<CoverageItem> items = new Vector<CoverageItem>();

    private static Logger log = LoggerFactory.getLogger(Coverage.class);

    public Coverage(DependencyGraph graph) {
        this.graph = graph;
    }

    public Coverage(DependencyResolver resolver) {
        this.graph = resolver.getGraph();
    }

    public void add(File file, boolean covered) {
        items.add(new CoverageItem(file, covered));
        if (covered) {
            coveredItems++;
        }
    }

    public int getCoveredItems() {
        return coveredItems;
    }

    public List<CoverageItem> getItems() {
        return items;
    }

    public Coverage getAll(){

        long time0 = System.currentTimeMillis();

        for (DependencyGraph.Node node: graph.getNodes()){
            if (node.getMetaFile().getType() != IMetaFile.MetaFileType.SOURCE_FILE) {
                continue;
            }

            add(new File(node.getMetaFile().getFilename()),
                    node.hasDependencyOfType(IMetaFile.MetaFileType.TEST_FILE));

        }

        long time1 = System.currentTimeMillis();

        log.info("Calculated coverage for {} files in {} sec", graph.size(), (time1 - time0) / 1000.0);

        return this;
    }

    public Coverage getByFiles(List<File> files){

        long time0 = System.currentTimeMillis();

        for (File file: files){

            DependencyGraph.Node node = graph.getNode(file.getAbsolutePath());
            add(new File(node.getMetaFile().getFilename()),
                    node.hasDependencyOfType(IMetaFile.MetaFileType.TEST_FILE));

            for (DependencyGraph.Node dependency: node.getDependencies()) {

                if (dependency.getMetaFile().getType() != IMetaFile.MetaFileType.SOURCE_FILE) {
                    continue;
                }

                add(new File(dependency.getMetaFile().getFilename()),
                        dependency.hasDependencyOfType(IMetaFile.MetaFileType.TEST_FILE));
            }

        }

        long time1 = System.currentTimeMillis();

        log.info("Calculated coverage for {} files in {} sec", graph.size(), (time1 - time0) / 1000.0);

        return this;
    }

    public void print() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        System.out.println();
        System.out.print("Coverage: " + getCoveredItems() + "/" + getItems().size());
        System.out.println(" (" + decimalFormat.format(getCoveredItems() / (float) getItems().size() * 100) + "%)");
        for (Coverage.CoverageItem item : getItems()) {
            System.out.println("  - " + (item.isCovered() ? AnsiColors.green(item.getFile().getAbsolutePath()) : AnsiColors.red(item.getFile().getAbsolutePath())));
        }
        System.out.println();
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
