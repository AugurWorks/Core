package com.augurworks.workserver;

public class JobResponse {
	private final String jobName;
	private final String filepath;
	private final String contents;
	
	public JobResponse(String filepath, String contents, String name) {
		this.filepath = filepath;
		this.contents = contents;
		this.jobName = name;
	}
	
	public String getFilepath() {
		return this.filepath;
	}
	
	public String getContents() {
		StringBuilder sb = new StringBuilder(jobName);
		sb = sb.append("\n");
		sb = sb.append(contents);
		return sb.toString();
	}

	@Override
	public String toString() {
		return "JobResponse [jobName=" + jobName + ", filepath=" + filepath
				+ ", contents=" + contents + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contents == null) ? 0 : contents.hashCode());
		result = prime * result
				+ ((filepath == null) ? 0 : filepath.hashCode());
		result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobResponse other = (JobResponse) obj;
		if (contents == null) {
			if (other.contents != null)
				return false;
		} else if (!contents.equals(other.contents))
			return false;
		if (filepath == null) {
			if (other.filepath != null)
				return false;
		} else if (!filepath.equals(other.filepath))
			return false;
		if (jobName == null) {
			if (other.jobName != null)
				return false;
		} else if (!jobName.equals(other.jobName))
			return false;
		return true;
	}
	
}
