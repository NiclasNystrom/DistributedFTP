package com.fileFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Given a directory the class traverses all subdirectories and finds all files.
public class FileFinder {

	public static String TEST_FILE = "Resources/pingvin.jpg";
	public static String TEST_FILE_NO_PATH = "pingvin.jpg";

	public static List<File> getFilesInDirectory(String directoryName) {
		List<File> files = new ArrayList<File>();
		File directory = new File(directoryName);

		// Get all files from a directory.
		File[] fList = directory.listFiles();
		if(fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory()) {
					getFilesInDirectory(file.getAbsolutePath(), files);
				}
			}
		}
		return files;
	}

	private static void getFilesInDirectory(String directoryName, List<File> files) {

		File directory = new File(directoryName);

		// Get all files from a directory.
		File[] fList = directory.listFiles();
		if(fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory()) {
					getFilesInDirectory(file.getAbsolutePath(), files);
				}
			}
		}
	}

	public static void listFiles(List<File> files) {
		if (files != null) {
			System.out.println("Number of files: " + files.size());
			System.out.println("------------------------------------");
			for (int i = 0; i < files.size(); i++) {
				System.out.println("File " + i + ": " + files.get(i).getName() + " (" + files.get(i).getPath() + ")");
			}
			System.out.println("------------------------------------");
		} else {
			System.out.println("Error listFiles: List of files is null.");
		}
	}

	public static String getWorkingPath() {
		return new File("").getAbsolutePath();
	}


	public static File lookupFile(String filename, String path){
		List<File> files = FileFinder.getFilesInDirectory(path);
		File f = null;
		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).getName().equals(filename)) {
				f = files.get(i);
				break;
			}
		}
		return f;
	}

	public static File lookupFile(String filename){
		return lookupFile(filename, getWorkingPath());
	}

	public static void main(String[] args) {
		// Test case 1
		String path = FileFinder.getWorkingPath();
		if (args.length > 0) {
			path = args[0];
		}

		List<File> files = FileFinder.getFilesInDirectory(path);
		FileFinder.listFiles(files);
	}

}

