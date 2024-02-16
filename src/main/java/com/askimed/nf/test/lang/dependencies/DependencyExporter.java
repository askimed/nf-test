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
            writer.write("\tnode [shape=note];\n");
            writer.write("\trankdir=LR;\n");

            for (DependencyGraph.Node node: resolver.getGraph().getNodes()) {
                if (node.getMetaFile().getType() == IMetaFile.MetaFileType.TEST_FILE) {
                    continue;
                }
                writer.write("\t" + getNodeName(resolver.getBaseDir(), node.getMetaFile()) + " " + getStyle(node) + "\n");
                for (IMetaFile  dependency: resolver.getGraph().getDependencies(node.getFilename())) {
                    if (dependency.getType() == IMetaFile.MetaFileType.TEST_FILE) {
                        continue;
                    }
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
        return "\"" + baseDir.toPath().relativize(Paths.get(metaFile.getFilename())).toString().replace("/", "/\n") + "\"";
    }

    private static String getStyle(DependencyGraph.Node node) {
        if (node.hasDependencyOfType(IMetaFile.MetaFileType.TEST_FILE)) {
            return "[fillcolor=green, style=\"rounded,filled\"]";

        } else {
            return "";
        }
    }


}
