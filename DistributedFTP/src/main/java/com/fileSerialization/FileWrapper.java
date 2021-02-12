package com.fileSerialization;

import java.io.*;

/*
 * Class for sending files with meta-information such as output name etc.
 */
public class FileWrapper {

	public String filename;
	public String content;
	// int size?
	// String type?

	public FileWrapper(File f) {
		this.filename = f.getName();
		this.content = FileSerialization.serializeFile(f);
	}

	public FileWrapper(String filename, String content) {
		this.filename = filename;
		this.content = content;
	}

	public static FileWrapper build(File f) {
		return new FileWrapper(f);
	}


	public String serialize() {
		String result = "";
		result += this.filename + "," + this.content;
		return result;
	}

	public static String serialize(FileWrapper fw) {
		String result = "";
		result += fw.filename + "," + fw.content;
		return result;
	}

	public static FileWrapper deserialize(String s) {
		FileWrapper fw = null;
		String[] tokens = s.split(",");
		if (tokens.length == 2) {
			fw = new FileWrapper(tokens[0], tokens[1]);
		} else	{
			System.err.println("Tokens more than 2: " + tokens.length);
		}
		return fw;
	}

}
