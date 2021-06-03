package main;

import java.io.File;

public class ImagePath {
	private String absolutePath;
	private String fileName;
	
	public ImagePath(String absolutePath) {
		this.absolutePath = absolutePath;
		this.fileName = absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1, absolutePath.lastIndexOf("."));
	}
	
	public String getAbsolutePath() {
		return absolutePath;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getHashedFileName() {
		return "#" + fileName.length();
	}
}
