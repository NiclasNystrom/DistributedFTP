package com.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.security.*;

public class RSAEncryption {
    final static int keySize = 1024;

    public static KeyPair buildKeyPair() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyPairGenerator.initialize(keySize, random);
        return keyPairGenerator.genKeyPair();
    }

    public static byte[] encrypt(String message, String privKey ) throws Exception {
        PrivateKey key = KeySerialization.deserialize_PrivateKey(privKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(message.getBytes());
    }

    public static byte[] decrypt(byte[] encrypted, String pubKey) throws Exception {
        PublicKey key = KeySerialization.deserialize_PublicKey(pubKey);
        //Cipher cipher = Cipher.getInstance("RSA");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encrypted);
    }




    public static void main(String[] args) throws Exception {

        KeyPair kp = RSAEncryption.buildKeyPair();

        String pubKey = KeySerialization.serializeKey(kp.getPublic());
        String privKey = KeySerialization.serializeKey(kp.getPrivate());

        PublicKey pubKey2 = KeySerialization.deserialize_PublicKey(pubKey);
        PrivateKey privKey2 = KeySerialization.deserialize_PrivateKey(privKey);

        // Should be true. Deserialize the keys and compares them with the original.
        /*boolean equalPub = kp.getPublic().equals(pubKey2);
        boolean equalPriv = kp.getPrivate().equals(privKey2);
        System.out.println("Equal Pub 1: " + equalPub);
        System.out.println("Equal Priv 1: " + equalPriv);*/

        // Should be false. Creates a new keypair and compares with keys above.
        /*KeyPair kp2 = RSAEncryption.buildKeyPair();
        boolean notequalPub = kp2.getPublic().equals(pubKey2);
        boolean notequalPriv = kp2.getPrivate().equals(privKey2);
        System.out.println("Not Equal Pub 2: " + notequalPub);
        System.out.println("Not Equal Priv 2: " + notequalPriv);*/


        // Test: Encrypt and decrypt a message.
        /*String message = "More Rake is Better.";
        byte[] encrypted = RSAEncryption.encrypt(message, privKey);
        byte[] decrypted = RSAEncryption.decrypt(encrypted, pubKey);

        String message2 = new String(decrypted);
        System.out.println("Message before encryption: \"" + message + "\"");
        System.out.println("Message after encryption: \"" + message2 + "\"");*/


        // Test: Encrypt and decrypt a symmetric key.
        SecretKey key = AESEncryption.buildSecretKey();

        String _message = "More Rake Is Better";
        String message = KeySerialization.serializeKey(key);

        byte[] _encrypted = AESEncryption.encrypt(_message, key, null);
        byte[] encrypted = RSAEncryption.encrypt(message, KeySerialization.serializeKey(kp.getPrivate()));
        //byte[] encrypted = RSAEncryption.encrypt(message, privKey);

        byte[] decrypted = RSAEncryption.decrypt(encrypted, pubKey);

        SecretKey key2 = KeySerialization.deserialize_SecretKey(DatatypeConverter.printBase64Binary(decrypted));
        byte[] _decrypted = AESEncryption.decrypt(_encrypted, key2, null);
        //byte[] _decrypted = AESEncryption.decrypt(_encrypted, key, null);

        String message2 = new String(decrypted);
        System.out.println("Message before encryption: \"" + message + "\"");
        System.out.println("Message after encryption: \"" + message2 + "\"");
        System.out.println("Equal: " + message.equals(message2));
        System.out.println("Decrypted message: \"" + DatatypeConverter.printBase64Binary(_decrypted) + "\"");





    }
}
