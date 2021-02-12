package com.encryption;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
//import java.util.Base64;

public class KeySerialization {

	public static String serializeKey(PublicKey key) {
		byte[] encodedKey = key.getEncoded();
		return DatatypeConverter.printBase64Binary(encodedKey);
	}
	public static String serializeKey(PrivateKey key) {
		byte[] encodedKey = key.getEncoded();
		return DatatypeConverter.printBase64Binary(encodedKey);
	}
	public static String serializeKey(SecretKey key) {
		byte[] encodedKey = key.getEncoded();
		return DatatypeConverter.printBase64Binary(encodedKey);
	}


	public static PublicKey deserialize_PublicKey(String key) throws Exception {
		//byte[] bytes = Base64.getDecoder().decode(key);
		byte[] bytes = DatatypeConverter.parseBase64Binary(key);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytes);
		return factory.generatePublic(pubKeySpec);
	}
	public static PrivateKey deserialize_PrivateKey(String key) throws Exception{
		//byte[] bytes = Base64.getDecoder().decode(key);
		byte[] bytes = DatatypeConverter.parseBase64Binary(key);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(bytes);
		return factory.generatePrivate(privKeySpec);
	}
	public static SecretKey deserialize_SecretKey(String key) throws Exception{
		byte[] encodedKey = DatatypeConverter.parseBase64Binary(key);
		SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
		return originalKey;
	}
}
