package com.askimed.nf.test.lang.dependencies;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class DependencyExporter {

    public static void generateDotFile(DependencyResolver resolver, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("digraph G {\n");
            writer.write("\tnode [shape=rectangle];\n");
            writer.write("\trankdir=LR;\n");

            for (DependencyGraph.Node node: resolver.getGraph().getNodes()) {
                writer.write("\t" + getNodeName(resolver.getBaseDir(), node.getMetaFile()) + " " + getStyle(node.getMetaFile()) + "\n");
                for (IMetaFile  dependency: resolver.getGraph().getDependencies(node.getFilename())) {
                    writer.write(String.format("\t%s -> %s;\n", getNodeName(resolver.getBaseDir(),
                            node.getMetaFile()), getNodeName(resolver.getBaseDir(), dependency)));
                }
            }
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getNodeName(File baseDir, IMetaFile metaFile) {
        return "\"" + baseDir.toPath().relativize(Paths.get(metaFile.getFilename())) + "\"";
    }

    private static String getNodeName(File baseDir, String filename) {
        return "\"" + baseDir.toPath().relativize(Paths.get(filename)) + "\"";
    }

    private static String getStyle(IMetaFile metaFile) {
        if (metaFile.getType() == IMetaFile.MetaFileType.TEST_FILE) {
            return "[fillcolor=yellow, style=\"rounded,filled\"]";
        } else {
            return "";
        }
    }


}
