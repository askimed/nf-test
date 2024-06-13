package com.askimed.nf.test.lang.dependencies;

import com.askimed.nf.test.core.TestExecutionResult;
import com.askimed.nf.test.core.TestSuiteExecutionResult;
import com.askimed.nf.test.core.reports.CsvReportWriter;
import com.askimed.nf.test.util.AnsiColors;
import com.askimed.nf.test.util.AnsiText;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

public class Coverage {

    private int coveredItems = 0;

    private DependencyGraph graph;

    private List<CoverageItem> items = new Vector<CoverageItem>();

    private static Logger log = LoggerFactory.getLogger(Coverage.class);

    private File baseDir = null;

    public Coverage(DependencyGraph graph) {
        this.graph = graph;
    }

    public Coverage(DependencyResolver resolver) {
        this.graph = resolver.getGraph();
        baseDir = resolver.getBaseDir();
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
        items.clear();
        coveredItems=0;
        long time0 = System.currentTimeMillis();

        for (DependencyGraph.Node node: graph.getNodes()){
            if (node.getMetaFile().getType() != IMetaFile.MetaFileType.SOURCE_FILE) {
                continue;
            }

            add(new File(node.getMetaFile().getFilename()),
                    node.hasDependencyOfType(IMetaFile.MetaFileType.TEST_FILE));

        }

        items.sort(new CoverageItemSorter());

        long time1 = System.currentTimeMillis();

        log.info("Calculated coverage for {} files in {} sec", graph.size(), (time1 - time0) / 1000.0);

        return this;
    }

    public Coverage getByFiles(List<File> files){
        items.clear();
        coveredItems=0;
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

        items.sort(new CoverageItemSorter());

        long time1 = System.currentTimeMillis();

        log.info("Calculated coverage for {} files in {} sec", graph.size(), (time1 - time0) / 1000.0);

        return this;
    }

    public void print() {
        printLabel();
        System.out.println();
    }

    public void printDetails() {
        System.out.println();
        System.out.println("Files:");
        for (Coverage.CoverageItem item : getItems()) {
            String label = item.getFile().getAbsolutePath();
            if (baseDir != null) {
                label = Paths.get(baseDir.getAbsolutePath()).relativize(item.getFile().toPath()).toString();
            }
            System.out.println("  \u2022 " + (item.isCovered() ? AnsiColors.green(label) : AnsiColors.red(label)));
        }
        System.out.println();
        printLabel();
        System.out.println();
    }

    private void printLabel() {
        float coverage = getCoveredItems() / (float) getItems().size();
        System.out.print(getColor("COVERAGE:", coverage) + " " +  formatCoverage(coverage));
        System.out.println( " [" + getCoveredItems() + " of " + getItems().size() + " files]");
    }

    private String getColor(String label, float value) {
        if (value < 0.5) {
            return AnsiColors.red(label);
        } else if (value < 0.9) {
            return AnsiColors.yellow(label);
        } else {
            return AnsiColors.green(label);
        }
    }

    private String formatCoverage(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(value * 100) + "%";
    }

    public void exportAsCsv(String filename) throws IOException {
        String[] header = new String[]{
                "filename",
                "covered",
                "type"
        };

        CSVWriter writer = new CSVWriter(new FileWriter(new File(filename)));
        writer.writeNext(header);
        for (Coverage.CoverageItem item : getItems()) {
            String[] line = new String[]{
                item.getFile().getAbsolutePath(),
                item.isCovered() + "",
                "unknown"
            };

            writer.writeNext(line);
        }

        writer.close();
        System.out.println();
        printLabel();
        System.out.println();
        System.out.println("Wrote coverage report to file " + filename + "\n");

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
