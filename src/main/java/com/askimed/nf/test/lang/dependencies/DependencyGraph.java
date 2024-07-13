package com.askimed.nf.test.lang.dependencies;

import java.util.*;

public class DependencyGraph {

    private Map<String, Node> nodes;

    public DependencyGraph() {
        nodes = new HashMap<String, Node>();
    }

    public static class Node {
        private String filename;
        private IMetaFile metaFile;
        private Set<Node> dependencies;

        public Node(String filename, IMetaFile metaFile) {
            this.filename = filename;
            this.metaFile = metaFile;
            this.dependencies = new HashSet<>();
        }

        public void addDependency(Node node) {
            dependencies.add(node);
        }

        public Set<Node> getDependencies() {
            return dependencies;
        }

        public String getFilename() {
            return filename;
        }

        public IMetaFile getMetaFile() {
            return metaFile;
        }

        public boolean hasDependencyOfType(IMetaFile.MetaFileType type) {
            for (Node node: getDependencies()) {
                if (node.getMetaFile().getType() == type) {
                    return true;
                }
            }
            return false;
        }
    }

    public void addFile(IMetaFile metaFile) {
        String filename = metaFile.getFilename();
        if (!nodes.containsKey(filename)) {
            nodes.put(filename, new Node(filename, metaFile));
        }
    }

    public void connectDependencies(){
            for (Node node: nodes.values()) {
                for (String dependency: node.getMetaFile().getDependencies()) {
                    addDependency(dependency, node.getFilename());
                }
            }
        }

    public void addDependency(String sourceFilename, String targetFilename) {
        Node sourceNode = nodes.get(sourceFilename);
        Node targetNode = nodes.get(targetFilename);

        if (sourceNode != null && targetNode != null) {
            sourceNode.addDependency(targetNode);
        }
    }

    public IMetaFile getFile(String filename) {
        Node node = nodes.get(filename);
        if (node != null) {
            return node.getMetaFile();
        }
        return null;
    }

    public Node getNode(String filename) {
        return nodes.get(filename);
    }


    public List<IMetaFile> getFiles() {
        List<IMetaFile> files = new Vector<IMetaFile>();
        for (Node node: nodes.values()){
            files.add(node.getMetaFile());
        }
        return files;
    }

    public List<IMetaFile> getDependencies(String filename) {
        List<IMetaFile> dependencies = new Vector<IMetaFile>();
        Node node = nodes.get(filename);
        if (node != null) {
            for (Node dependency : node.getDependencies()) {
                dependencies.add(dependency.getMetaFile());
            }
        }
        return dependencies;
    }

    public int size() {
        return nodes.size();
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }


}
