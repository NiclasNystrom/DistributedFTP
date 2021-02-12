package com.datatypes;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.security.PublicKey;

public class MyServerInfo implements Serializable {


    private static final long serialVersionUID = -8544317271562501662L;
    private String address;
    private PublicKey publicKey;
    private SecretKey secretKey;

    //address should not contain http://
    public MyServerInfo(String address, PublicKey publicKey, SecretKey key) {
        this.address = address;
        this.publicKey = publicKey;
        this.secretKey = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof MyServerInfo))
            return false;
        if (obj == this)
            return true;
        return this.getPublicKey().equals(((MyServerInfo) obj).getPublicKey()) && this.getAddress().equals(((MyServerInfo) obj).address);
    }
    public int hashCode(){
        return address.hashCode()*publicKey.hashCode();
    }
    public String toString(){
        return address;
    }
}
