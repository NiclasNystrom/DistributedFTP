package com.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
import java.security.*;

public class AESEncryption {
    final static int KEY_SIZE = 128;

    public static SecretKey buildSecretKey() throws Exception{
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(KEY_SIZE);
        return kgen.generateKey();
    }

    public static IVWrapper buildIV() throws Exception{
        byte[] iv = new byte[KEY_SIZE/8];
        SecureRandom srandom = new SecureRandom();
        srandom.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        return new IVWrapper(iv, ivspec);
    }

    public static byte[] encrypt(String message, SecretKey skey, IvParameterSpec spec ) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        //Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        if (spec != null)
            cipher.init(Cipher.ENCRYPT_MODE, skey, spec);
        else
            cipher.init(Cipher.ENCRYPT_MODE, skey);

        //byte[] encrypted = cipher.doFinal(DatatypeConverter.parseBase64Binary(message));
        byte[] encrypted = cipher.doFinal(message.getBytes());
        return encrypted;
    }


    public static byte[] decrypt(byte[] encrypted, SecretKey skey, IvParameterSpec spec) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        //Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        if (spec != null)
            cipher.init(Cipher.DECRYPT_MODE, skey, spec);
        else
            cipher.init(Cipher.DECRYPT_MODE, skey);

        byte[] original = cipher.doFinal(encrypted);
        return original;
    }




    public static void main(String[] args) throws Exception {


        SecretKey _key = AESEncryption.buildSecretKey();
        SecretKey key = KeySerialization.deserialize_SecretKey(KeySerialization.serializeKey(_key));
        IVWrapper iw = AESEncryption.buildIV();

        String message = "More Rake Is Better.";
        System.out.println("Original: \"" + message + "\"");

        byte[] encrypted = AESEncryption.encrypt(message, key, null);
        System.out.println("Encrypted: \""+encrypted+"\"");

        byte[] decrypted = AESEncryption.decrypt(encrypted, key, null);
        System.out.println("Decrypted: \""+decrypted+"\"");

        //String decryptMess = new String(decrypted);
        String decryptMess = DatatypeConverter.printBase64Binary(decrypted);
        System.out.println("Decrypted Message: \"" + decryptMess + "\"");

    }
}

