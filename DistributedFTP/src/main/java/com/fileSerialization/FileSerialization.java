package com.fileSerialization;

import com.fileFinder.FileFinder;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
//import java.util.Base64;


public class FileSerialization  {


	public static String serializeFile(File f) {
		String result = "";
		try {
			InputStream inputStream = new FileInputStream(f);//You can get an inputStream using any IO API
			byte[] bytes = null;
			byte[] buffer = new byte[8192];
			int bytesRead;
			ByteArrayOutputStream output = new ByteArrayOutputStream();

		    while ((bytesRead = inputStream.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
				bytes = output.toByteArray();
			}
			result = DatatypeConverter.printBase64Binary(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static File deserializeFile(String s, String outputname) {
		File f = null;
		try {
			FileOutputStream fos = new FileOutputStream(outputname);
			fos.write(DatatypeConverter.parseBase64Binary(s));
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	// Usage: Empty or "main filepath outputname"
	// Example: main Resources/pingvin.jpg pingvin2.jpg
	public static void main(String[] args) {

		String filepath = FileFinder.TEST_FILE;
		String outputName = "pingvin2.jpg";

		if (args.length > 1) {
			filepath = args[0];
			outputName = args[1];
		}

		// Test case 1
		File _f = new File(filepath);
		String fBytes = FileSerialization.serializeFile(_f);
		//System.out.println("Serialized file 1: " + fBytes);
		File f = FileSerialization.deserializeFile(fBytes, outputName);


	}





}

