package br.com.somewhere.core;

import javafx.beans.property.SimpleStringProperty;

public class ResultBean {
	private SimpleStringProperty fileName = new SimpleStringProperty();
	private SimpleStringProperty path = new SimpleStringProperty();
	private SimpleStringProperty line = new SimpleStringProperty();
	private SimpleStringProperty snippet = new SimpleStringProperty();
        private SimpleStringProperty fileNameLine = new SimpleStringProperty();
	
	public ResultBean() {
		super();
	}
        
        public String getFileNameLine(){
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
}
