package com.askimed.nf.test.lang.dependencies;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IMetaFileTest {

    @Test
    void parseDependencies() {
        String input = "workflow,process";
        Set<IMetaFile.TargetType> targets = new HashSet<IMetaFile.TargetType>();
        targets.add(IMetaFile.TargetType.WORKFLOW);
        targets.add(IMetaFile.TargetType.PROCESS);
        assertEquals(targets , IMetaFile.TargetType.parse(input));
    }

    @Test
    void parseDependenciesAll() {
        String input = "all";
        Set<IMetaFile.TargetType> targets = new HashSet<IMetaFile.TargetType>();
        assertEquals(targets , IMetaFile.TargetType.parse(input));

        input = "  ";
        targets = new HashSet<IMetaFile.TargetType>();
        assertEquals(targets , IMetaFile.TargetType.parse(input));
    }
}