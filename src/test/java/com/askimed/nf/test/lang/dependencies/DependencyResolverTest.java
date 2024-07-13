package com.askimed.nf.test.lang.dependencies;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DependencyResolverTest {

    public File getNfGwas() throws IOException {
        String url = "https://github.com/genepi/nf-gwas/archive/refs/tags/v1.0.7.zip";
        return downloadAndExtractZip(url, "temp", "nf-gwas-1.0.7");
    }

    public File getFetchNgs() throws IOException {
        String url = "https://github.com/nf-core/fetchngs/archive/refs/tags/1.12.0.zip";
        return downloadAndExtractZip(url, "temp", "fetchngs-1.12.0");
    }

    @Test
    void findAllTests() throws Exception {
        File root = getNfGwas();
        DependencyResolver resolver = new DependencyResolver(root);
        resolver.buildGraph();
        assertEquals(10, resolver.findAllTests().size());

        Coverage coverage = new Coverage(resolver);
        assertEquals(10, coverage.getAll().getCoveredItems());
        assertEquals(39, coverage.getAll().getItems().size());
    }

    @Test
    void findRelatedTests() throws Exception {

        File root = getNfGwas();
        DependencyResolver resolver = new DependencyResolver(root);
        resolver.buildGraph();
        assertEquals(2, resolver.findRelatedTestsByFiles(
                new File(root, "modules/../modules/local/regenie/regenie_step1_run.nf"),
                new File(root, "nextflow.config")
        ).size());

        assertEquals(3, resolver.findRelatedTestsByFiles(
                new File(root, "modules/../modules/local/regenie/regenie_step1_run.nf"),
                new File(root, "modules/../modules/local/regenie/regenie_step2_run.nf")
        ).size());
    }

    @Test
    void findRelatedTestAndFollowDependencies() throws Exception {

        File root = getFetchNgs();

        DependencyResolver resolver = new DependencyResolver(root);
        resolver.setFollowingDependencies(false);

        resolver.buildGraph();
        assertEquals(10, resolver.findRelatedTestsByFiles(
                new File(root, "modules/local/sra_to_samplesheet/main.nf")
        ).size());

        resolver = new DependencyResolver(root);
        resolver.setFollowingDependencies(true);

        resolver.buildGraph();
        assertEquals(11, resolver.findRelatedTestsByFiles(
                new File(root, "modules/local/sra_to_samplesheet/main.nf")
        ).size());

        Coverage coverage = new Coverage(resolver);
        assertEquals(17, coverage.getAll().getCoveredItems());
        assertEquals(17, coverage.getAll().getItems().size());
    }

    @Test
    void findRelatedTestAndFilterDependencies() throws Exception {

        File root = getFetchNgs();

        DependencyResolver resolver = new DependencyResolver(root);
        resolver.setFollowingDependencies(false);

        resolver.buildGraph();
        assertEquals(10, resolver.findRelatedTestsByFiles(
                new File(root, "modules/local/sra_to_samplesheet/main.nf")
        ).size());

        resolver = new DependencyResolver(root);
        Set<IMetaFile.TargetType> targets = new HashSet<IMetaFile.TargetType>();
        targets.add(IMetaFile.TargetType.PROCESS);
        resolver.setTargets(targets);

        resolver.buildGraph();
        assertEquals(1, resolver.findRelatedTestsByFiles(
                new File(root, "modules/local/sra_to_samplesheet/main.nf")
        ).size());

        resolver = new DependencyResolver(root);
         targets = new HashSet<IMetaFile.TargetType>();
        targets.add(IMetaFile.TargetType.PROCESS);
        targets.add(IMetaFile.TargetType.WORKFLOW);
        resolver.setTargets(targets);

        resolver.buildGraph();
        assertEquals(10, resolver.findRelatedTestsByFiles(
                new File(root, "modules/local/sra_to_samplesheet/main.nf")
        ).size());

        resolver = new DependencyResolver(root);
        targets = new HashSet<IMetaFile.TargetType>();
        targets.add(IMetaFile.TargetType.WORKFLOW);
        resolver.setTargets(targets);

        resolver.buildGraph();
        assertEquals(9, resolver.findRelatedTestsByFiles(
                new File(root, "modules/local/sra_to_samplesheet/main.nf")
        ).size());

        resolver = new DependencyResolver(root);
        targets = new HashSet<IMetaFile.TargetType>();
        targets.add(IMetaFile.TargetType.PIPELINE);
        resolver.setTargets(targets);
        resolver.setFollowingDependencies(true);
        resolver.buildGraph();
        assertEquals(1, resolver.findRelatedTestsByFiles(
                new File(root, "modules/local/sra_to_samplesheet/main.nf")
        ).size());

        resolver = new DependencyResolver(root);
        targets = new HashSet<IMetaFile.TargetType>();
        targets.add(IMetaFile.TargetType.PIPELINE);
        targets.add(IMetaFile.TargetType.WORKFLOW);
        resolver.setTargets(targets);
        resolver.setFollowingDependencies(true);
        resolver.buildGraph();
        assertEquals(10, resolver.findRelatedTestsByFiles(
                new File(root, "modules/local/sra_to_samplesheet/main.nf")
        ).size());

        resolver = new DependencyResolver(root);
        targets = new HashSet<IMetaFile.TargetType>();
        targets.add(IMetaFile.TargetType.WORKFLOW);
        resolver.setTargets(targets);
        resolver.setFollowingDependencies(true);
        resolver.buildGraph();
        assertEquals(9, resolver.findRelatedTestsByFiles(
                new File(root, "modules/local/sra_to_samplesheet/main.nf")
        ).size());
    }

    @Test
    void findRelatedTestsWithTrigger() throws Exception {

        File root = getNfGwas();
        DependencyResolver resolver = new DependencyResolver(root);
        List<String> triggers = new Vector<String>();
        triggers.add("nextflow.config");
        resolver.buildGraph(new Vector<>(), triggers);

        assertEquals(10, resolver.findRelatedTestsByFiles(
                new File(root, "modules/../modules/local/regenie/regenie_step1_run.nf"),
                new File(root, "nextflow.config")
        ).size());

    }

    @Test
    void findRelatedTestsWithTriggerAndPattern() throws Exception {

        File root = getNfGwas();
        DependencyResolver resolver = new DependencyResolver(root);
        List<String> triggers = new Vector<String>();
        triggers.add("*.config");
        resolver.buildGraph(new Vector<>(), triggers);

        assertEquals(10, resolver.findRelatedTestsByFiles(
                new File(root, "modules/../modules/local/regenie/regenie_step1_run.nf"),
                new File(root, "nextflow.config")
        ).size());

    }

    @Test
    void findRelatedTestsWithTriggerAndTestDataPattern() throws Exception {

        File root = getNfGwas();
        DependencyResolver resolver = new DependencyResolver(root);
        List<String> triggers = new Vector<String>();
        triggers.add("tests/input/**/*");
        resolver.buildGraph(new Vector<>(), triggers);

        assertEquals(10, resolver.findRelatedTestsByFiles(
                new File(root, "tests/input/filter_results/regenie_step2_example_Y1.regenie.gz")
        ).size());

        resolver = new DependencyResolver(root);
        resolver.buildGraph();

        assertEquals(0, resolver.findRelatedTestsByFiles(
                new File(root, "tests/input/filter_results/regenie_step2_example_Y1.regenie.gz")
        ).size());

    }

    private File downloadAndExtractZip(String url, String destinationFolder, String name) throws IOException {
        // Check if destination folder exists
        File destFolder = new File(destinationFolder);
        if (!new File(destFolder, name).exists()) {
            // Create destination folder if it doesn't exist
            destFolder.mkdirs();

            // Download the zip file
            URL zipUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) zipUrl.openConnection();
            try (InputStream inputStream = connection.getInputStream()) {
                Path zipFilePath = Paths.get(destinationFolder, "temp.zip");
                Files.copy(inputStream, zipFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Extract the contents of the zip file
            File extractedFolder = new File(destinationFolder, name);
            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(new File(destinationFolder, "temp.zip")))) {
                ZipEntry entry = zipInputStream.getNextEntry();
                while (entry != null) {
                    Path filePath = Paths.get(destinationFolder, entry.getName());
                    if (!entry.isDirectory()) {
                        // Create parent directories if they don't exist
                        Files.createDirectories(filePath.getParent());
                        // Write file content
                        try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = zipInputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                        }
                    } else {
                        // Create directory if it doesn't exist
                        Files.createDirectories(filePath);
                    }
                    zipInputStream.closeEntry();
                    entry = zipInputStream.getNextEntry();
                }
            }

            // Clean up: Delete the temporary zip file
            Files.deleteIfExists(Paths.get(destinationFolder, "temp.zip"));

            return extractedFolder;
        } else {
            // If the destination folder already exists, return it without downloading
            return new File(destinationFolder, name);
        }
    }


}