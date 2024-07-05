package com.askimed.nf.test.lang.dependencies;

public class CoverageItemSorter implements java.util.Comparator<Coverage.CoverageItem> {

    @Override
    public int compare(Coverage.CoverageItem o1, Coverage.CoverageItem o2) {
        return o1.getFile().getAbsolutePath().compareTo(o2.getFile().getAbsolutePath());
    }
}
