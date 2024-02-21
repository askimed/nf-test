package com.askimed.nf.test.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GitCommand {

    private String binary;

    private boolean silent = true;

    public static String ERROR = "Git binary not found. Please check if git is in a directory accessible by your $PATH variable or set $GIT_HOME.";

    public GitCommand() {
        binary = new BinaryFinder("git").env("GIT_HOME").envPath().path("/usr/local/bin").find();
    }

    public boolean isInstalled() {
        return binary != null;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    private List<File> execute(String directory, List<String> args) throws IOException {

        if (!isInstalled()){
            throw new IOException("Could not find git binary.");
        }

        Command git = new Command(binary);
        git.setDirectory(directory);
        git.setParams(args);
        git.setSilent(silent);
        StringBuffer output = new StringBuffer();
        git.writeStdout(output);
        StringBuffer error = new StringBuffer();
        git.writeStderr(error);

        int result = git.execute();

        if (result != 0){
            //TODO: log output.
            throw new IOException("Error running git command." + error);
        }

        return parseFilenames(output);
    }

    public List<File> findChangesSinceLastCommit(String directory) throws IOException {
        List<String> args = new Vector<String>();
        args.add("show");
        args.add("--name-only");
        args.add("--pretty=format:");
        args.add("HEAD");
        args.add("--");
        return execute(directory, args);
    }

    public List<File> findChangesWithAncestor(File directory) throws IOException {
        return findChangesWithAncestor(directory.getAbsolutePath());
    }

    public List<File> findChangesWithAncestor(String directory) throws IOException {
        return findChangesSince(directory, "HEAD^");
    }

    public List<File> findChangesSince(File directory, String commit) throws IOException {
        return findChangesSince(directory.getAbsolutePath(), commit);

    }

    public List<File> findChangesSince(String directory, String commit) throws IOException {
        List<String> args = new Vector<String>();
        args.add("diff");
        args.add("--name-only");
        args.add(commit + "...HEAD");
        args.add("--");

        //TODO: jest adds also findChanges to list.

        return execute(directory, args);
    }

    public List<File> findChanges(File directory) throws IOException {
        return findChanges(directory.getAbsolutePath());
    }

    public List<File> findChanges(String directory) throws IOException {
        //staged
        List<String> args = new Vector<String>();
        args.add("diff");
        args.add("--cached");
        args.add("--name-only");
        args.add("--");
        List<File> staged = execute(directory, args);

        //unstaged
        args = new Vector<String>();
        args.add("ls-files");
        args.add("--other");
        args.add("--modified");
        args.add("--exclude-standard");
        args.add("--");
        List<File> unstaged = execute(directory, args);

        staged.addAll(unstaged);
        return staged;
    }

    public static List<File> parseFilenames(StringBuffer stringBuffer) {
        List<File> files = new ArrayList<File>();
        String[] lines = stringBuffer.toString().split("\\r?\\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                files.add(new File(line.trim()));
            }
        }
        return files;
    }

}