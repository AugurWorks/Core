package com.augurworks.workserver;

import java.io.File;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;

import com.augurworks.decisiontree.impl.TreeWithStats;

public enum JobType {
	DECISION_TREE {
		@Override
		public boolean isCorrectType(String filename) {
			return filename.contains("dtree-todo");
		}
		@Override
		public Callable<JobResponse> execute(final String filename, final String fileContents) {
			return new Callable<JobResponse>() {
				@Override
				public JobResponse call() throws Exception {
					String head = filename.substring(0, filename.indexOf("dtree-todo"));
					String number = filename.substring(filename.indexOf("dtree-todo") + 10);
					String outputPath = head + "dtree-complete" + number;
					String jobTemp = head + "dtree-tmp" + number;
					FileUtils.writeStringToFile(new File(jobTemp), fileContents);
					TreeWithStats tree = com.augurworks.decisiontree.main.Main.runJob(jobTemp);
					FileUtils.deleteQuietly(new File(jobTemp));
					return new JobResponse(outputPath, tree.toString(), "DTree job " + number);
				}
			};
		}
	},
	NEURAL_NET {
		@Override
		public boolean isCorrectType(String filename) {
			return false;
		}
		@Override
		public Callable<JobResponse> execute(String filename, final String fileContents) {
			return null; 	
		}
	},
	EMPTY {
		@Override
		public boolean isCorrectType(String filename) {
			return filename.contains("empty-todo");
		}
		@Override
		public Callable<JobResponse> execute(final String filename, final String fileContents) {
			return new Callable<JobResponse>() {
				@Override
				public JobResponse call() throws Exception {
					String head = filename.substring(0, filename.indexOf("empty-todo"));
					String number = filename.substring(filename.indexOf("empty-todo") + 10);
					String outputPath = head + "empty-complete" + number;
					return new JobResponse(outputPath, "Done :)", "Empty job " + number);
				}
			};
		}
	},
	;
	
	private JobType() {	
	}
	
	public abstract boolean isCorrectType(String filename);
	public abstract Callable<JobResponse> execute(String filename, String fileContents);
	
	public static JobType fromFilename(String filename) {
		for (JobType jobType : values()) {
			if (jobType.isCorrectType(filename)) {
				return jobType;
			}
		}
		throw new IllegalArgumentException("File \"" + filename + "\" is not a recognized type.");
	}
}
