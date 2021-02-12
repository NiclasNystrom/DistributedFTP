package com.datatypes;

import java.io.Serializable;
import java.util.HashSet;

//Hacky solution, objects that the server sends back
public class MyResponse implements Serializable {
    private static final long serialVersionUID = -260927274219849548L;
    private HashSet<MyServerInfo> myServerInfos;
    private String string;
    private int status;

    public MyResponse(String string, int status) {
        this.string = string;
        this.status = status;
    }

    public String getString() {
        return string;
    }

    public int getStatus() {
        return status;
    }

    public HashSet<MyServerInfo> getMyServerInfos() {
        return myServerInfos;
    }

    public void setMyServerInfos(HashSet<MyServerInfo> myServerInfos) {
        this.myServerInfos = myServerInfos;
    }
}
