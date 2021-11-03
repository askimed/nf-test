package com.askimed.nf.test.lang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVReaderHeaderAwareBuilder;
import com.opencsv.exceptions.CsvValidationException;

public class WorkflowTrace {

	private List<WorkflowTask> tasks = new Vector<WorkflowTask>();

	public WorkflowTrace(File file) throws CsvValidationException, FileNotFoundException, IOException {
		CSVParser parser = new CSVParserBuilder().withSeparator('\t').build();
		CSVReaderHeaderAware reader = new CSVReaderHeaderAwareBuilder(new FileReader(file)).withCSVParser(parser)
				.build();
		Map<String, String> values = reader.readMap();
		while (values != null) {
			WorkflowTask task = new WorkflowTask();
			System.out.println(values);
			task.success = values.get("status").equals("COMPLETED");
			task.name = values.get("name");
			tasks.add(task);
			values = reader.readMap();
		}
		reader.close();

	}

	public List<WorkflowTask> tasks() {
		return tasks;
	}

	public List<WorkflowTask> succeeded() {
		List<WorkflowTask> filtered = new Vector<WorkflowTask>();
		for (WorkflowTask task : tasks) {
			if (task.success) {
				filtered.add(task);
			}
		}
		return filtered;
	}

	public List<WorkflowTask> failed() {
		List<WorkflowTask> filtered = new Vector<WorkflowTask>();
		for (WorkflowTask task : tasks) {
			if (!task.success) {
				filtered.add(task);
			}
		}
		return filtered;
	}

}
