package com.datatypes;

import com.myServer.GSendProper;
import org.restlet.Server;
import com.testClient.Client;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Not a singleton, technically
 * It's so that multiple threads can have their own version
 */
public class NotSingleton {
    private static HashMap<Integer, NotSingleton> notSingletonHashMap = new HashMap<>();
    private String directory;
    private HashSet<MyServerInfo> myServerInfos;
    private KeyPair keyPair;
    private SecretKey secretKey;
    //address should not contain http://
    private String address;
    private GSendProper gSendProper;
    private Server server;

    public static NotSingleton getInstance(int port){
        if(notSingletonHashMap.get(port) == null){
            notSingletonHashMap.put(port, new NotSingleton());
        }
        return notSingletonHashMap.get(port);
    }

    /**
     * Copies by reference, so it doesn't technically copy
     * @param firstPort The currently existing instance
     * @param secondPort The new instance to which things should be copied
     */
    public static void copyContent(int firstPort, int secondPort){
        NotSingleton first = getInstance(firstPort);
        NotSingleton second = getInstance(secondPort);
        second.setDirectory(first.getDirectory());
        HashSet<MyServerInfo> myServerInfo = first.getMyServerInfos();
        try{
            if(Client.localOnly){
                myServerInfo.add(new MyServerInfo("localhost:"+secondPort, first.getKeyPair().getPublic(), first.getSecretKey()));
            } else {
                URL url_name = new URL("http://ipv4bot.whatismyipaddress.com");
                BufferedReader sc =
                        new BufferedReader(new InputStreamReader(url_name.openStream()));
                String address = sc.readLine().trim();
                myServerInfo.add(new MyServerInfo(address+":"+secondPort, first.getKeyPair().getPublic(), first.getSecretKey()));
            }
        } catch (Exception e){
            System.out.println("Couldn't get public IP");
        }
        second.setMyServerInfos(myServerInfo);
        second.setKeyPair(first.getKeyPair());
        second.setgSendProper(first.getgSendProper());
        second.setSecretKey(first.getSecretKey());

    }
    private NotSingleton(){

    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GSendProper getgSendProper() {
        return gSendProper;
    }

    public void setgSendProper(GSendProper gSendProper) {
        this.gSendProper = gSendProper;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public HashSet<MyServerInfo> getMyServerInfos() {
        return myServerInfos;
    }

    public void setMyServerInfos(HashSet<MyServerInfo> myServerInfos) {
        this.myServerInfos = myServerInfos;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}
