package com.askimed.nf.test.lang.dependencies;

import com.askimed.nf.test.nextflow.NextflowScript;

import javax.xml.transform.Source;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class DependencyExporter {

    public static void generateDotFile(DependencyResolver resolver, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("digraph G {\n");
            writer.write("\tnode [shape=note,colorscheme=pastel19\n];\n");
            writer.write("\trankdir=LR;\n");

            for (DependencyGraph.Node node: resolver.getGraph().getNodes()) {
                writer.write("\t" + quote(getNodeId(resolver.getBaseDir(), node.getMetaFile())) +
                        " [label=" + quote(getNodeName(resolver.getBaseDir(), node.getMetaFile())) + ", " +
                        getStyle(node) + "]\n");
                for (IMetaFile  dependency: resolver.getGraph().getDependencies(node.getFilename())) {
                    writer.write(String.format("\t%s -> %s;\n", quote(getNodeId(resolver.getBaseDir(),
                            node.getMetaFile())), quote(getNodeId(resolver.getBaseDir(), dependency))));
                }
            }
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getNodeId(File baseDir, IMetaFile metaFile) {
        return baseDir.toPath().relativize(Paths.get(metaFile.getFilename())).toString();
    }

    private static String getNodeName(File baseDir, IMetaFile metaFile) {
        if (metaFile.getType() == IMetaFile.MetaFileType.SNAPSHOT_FILE) {
            return "Snapshot";
        } else if (metaFile.getType() == IMetaFile.MetaFileType.TEST_FILE) {
            return new File(metaFile.getFilename()).getName();
        } else {
            return baseDir.toPath().relativize(Paths.get(metaFile.getFilename())).toString().replace("/", "/\n");
        }
    }

    private static String getStyle(DependencyGraph.Node node) {

       if (node.getMetaFile().getType() == IMetaFile.MetaFileType.SNAPSHOT_FILE) {
            return "shape=" + quote("cylinder");// + ", color=" + quote("9") + ", style=" + quote("rounded,filled");
        } else if (node.getMetaFile().getType() == IMetaFile.MetaFileType.SOURCE_FILE) {
            String shape = "box3d";
            String color = "";
            if (node.hasDependencyOfType(IMetaFile.MetaFileType.TEST_FILE)) {
                color = "3";
            } else {
                color = "1";
            }
            return "shape=" + quote(shape) + ", fillcolor=" + quote(color) + ", style=" + quote("rounded,filled");
        } else {
           return "fillcolor=" + quote("2") + ", style=" + quote("rounded,filled");
       }
    }

    private static String quote(String text) {
        return "\"" + text + "\"";
    }

}
