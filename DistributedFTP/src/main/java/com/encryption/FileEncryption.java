package com.encryption;

import com.fileFinder.FileFinder;
import com.fileSerialization.FileSerialization;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileEncryption {

	private static void processFileToFile(boolean encrypt, File inputFile, String inputKey, File outputFile) throws Exception {

		// Convert key into bytes
		SecretKey key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(inputKey),"AES");

		// Read input file into byte array
		FileInputStream fifo = new FileInputStream(inputFile);
		byte[] inputBytes = new byte[(int)inputFile.length()];
		fifo.read(inputBytes);

		// Parce the byte array from input file
		byte[] outputBytes = null;
		if (encrypt) {
			outputBytes = AESEncryption.encrypt(DatatypeConverter.printBase64Binary(inputBytes), key, null);
		} else {
			outputBytes = AESEncryption.decrypt(inputBytes, key, null);
		}


		// Write the output byte array to the output file
		FileOutputStream fofo = new FileOutputStream(outputFile);
		fofo.write(outputBytes);

		fifo.close();
		fofo.close();
	}

	private static byte[] processFileToBytes(boolean encrypt, String input, String key) throws Exception {

		// Convert key into bytes
		SecretKey _key = KeySerialization.deserialize_SecretKey(key);
		//SecretKey _key = new SecretKeySpec(DatatypeConverter.parseBase64Binary(key),"AES");



		// Parce the byte array from input file
		byte[] outputBytes = null;
		if (encrypt) {
			outputBytes = AESEncryption.encrypt(input, _key, null);
		} else {
			outputBytes = AESEncryption.decrypt(DatatypeConverter.parseBase64Binary(input), _key, null);
		}

		return outputBytes;
	}

	// Encrypts a file
	public static void encrypt(File input, String key, File output) throws Exception {
		FileEncryption.processFileToFile(true, input, key, output);
	}

	public static byte[] encrypt(String input, String key) throws Exception {
		return FileEncryption.processFileToBytes(true, input, key);
	}

	// Decrypts a file
	public static void decrypt(File input, String key, File output) throws Exception {
		FileEncryption.processFileToFile(false,input,key,output);
	}

	public static byte[] decrypt(byte[] encrypted, String key) throws Exception {
		return FileEncryption.processFileToBytes(false, DatatypeConverter.printBase64Binary(encrypted), key);
	}


	public static void main(String[] args) throws Exception {

		// Test case 1: Simple file encryption
		File f1 = FileFinder.lookupFile(FileFinder.TEST_FILE_NO_PATH, FileFinder.getWorkingPath() + "/Resources/");
		File f2 = new File("enc_"+FileFinder.TEST_FILE_NO_PATH);
		File f3 = new File("dec_"+FileFinder.TEST_FILE_NO_PATH);

		SecretKey key = AESEncryption.buildSecretKey();
		//IVWrapper iw = AESEncryption.buildIV();

		//1
		//FileEncryption.encrypt(f1, DatatypeConverter.printBase64Binary(key.getEncoded()), f2);
		//FileEncryption.decrypt(f2, DatatypeConverter.printBase64Binary(key.getEncoded()), f3);

		//2
		byte[] b = FileEncryption.encrypt(FileSerialization.serializeFile(f1), DatatypeConverter.printBase64Binary(key.getEncoded()));
		byte[] b2 = FileEncryption.decrypt(b, DatatypeConverter.printBase64Binary(key.getEncoded()));
		FileSerialization.deserializeFile(DatatypeConverter.printBase64Binary(b), "enc_"+f1.getName());
		FileSerialization.deserializeFile(DatatypeConverter.printBase64Binary(b2), "dec_"+f1.getName());

	}
}
