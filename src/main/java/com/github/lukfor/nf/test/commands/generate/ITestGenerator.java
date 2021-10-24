package com.github.lukfor.nf.test.commands.generate;

import java.io.File;

public interface ITestGenerator {

	public boolean generate(File source, File target) throws Exception;

}
