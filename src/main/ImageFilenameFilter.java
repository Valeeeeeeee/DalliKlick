package main;

import java.io.File;
import java.io.FilenameFilter;

public class ImageFilenameFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		return name.endsWith(".png") || name.endsWith(".jpg");
	}
}
