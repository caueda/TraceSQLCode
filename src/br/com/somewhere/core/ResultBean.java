package br.com.somewhere.core;

import javafx.beans.property.SimpleStringProperty;

public class ResultBean implements Comparable<ResultBean> {
	private SimpleStringProperty fileName = new SimpleStringProperty();
	private SimpleStringProperty path = new SimpleStringProperty();
	private SimpleStringProperty line = new SimpleStringProperty();
	private SimpleStringProperty snippet = new SimpleStringProperty();
	private SimpleStringProperty fileNameLine = new SimpleStringProperty();

	public ResultBean() {
		super();
	}

	public void setFileNameLine(String fileNameLine) {
		this.fileNameLine.set(fileNameLine);
	}
	
	public String getFileNameLine() {
		return this.fileName.get() + ":" + this.line.get();
	}

	public ResultBean(String fileName, String path, String line, String snippet) {
		super();
		this.fileName.set(fileName);
		this.path.set(path);
		this.line.set(line);
		this.snippet.set(snippet);
	}

	public String getFileName() {
		return fileName.get();
	}

	public void setFileName(String fileName) {
		this.fileName.set(fileName);
	}

	public String getPath() {
		return path.get();
	}

	public void setPath(String path) {
		this.path.set(path);
	}

	public String getLine() {
		return line.get();
	}

	public void setLine(String line) {
		this.line.set(line);
	}

	public String getSnippet() {
		return snippet.get();
	}

	public void setSnippet(String snippet) {
		this.snippet.set(snippet);
	}

	@Override
	public String toString() {
		return getFileName() + ":" + getLine() + " -> " + getSnippet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((fileNameLine == null) ? 0 : fileNameLine.hashCode());
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((snippet == null) ? 0 : snippet.hashCode());
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
		ResultBean other = (ResultBean) obj;
		if (fileName.get() == null) {
			if (other.fileName.get() != null)
				return false;
		} else if (!fileName.get().equals(other.fileName.get()))
			return false;
		if (fileNameLine.get() == null) {
			if (other.fileNameLine.get() != null)
				return false;
		} else if (!fileNameLine.get().equals(other.fileNameLine.get()))
			return false;
		if (line.get() == null) {
			if (other.line.get() != null)
				return false;
		} else if (!line.get().equals(other.line.get()))
			return false;
		if (path.get() == null) {
			if (other.path.get() != null)
				return false;
		} else if (!path.get().equals(other.path.get()))
			return false;
		if (snippet.get() == null) {
			if (other.snippet.get() != null)
				return false;
		} else if (!snippet.get().equals(other.snippet.get()))
			return false;
		return true;
	}

	@Override
	public int compareTo(ResultBean resultBean) {		
		return getOrdenar().compareTo(resultBean.getOrdenar());
	}

	private String getOrdenar() {
		String order = 
				this.getFileName() + ":" + String.format("%010d", Integer.parseInt(this.getLine()));
		return order;
	}
}
