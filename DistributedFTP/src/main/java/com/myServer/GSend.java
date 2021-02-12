package com.myServer;

import com.fileTransfer.FileResource;
import com.fileTransfer.FileInterface;
import com.fileTransfer.FileResource;
import org.restlet.Component;
import org.restlet.data.Protocol;

import java.awt.*;
import java.io.File;

public class GSend extends Component {
    public static void main(String[] args) throws Exception{
        new GSend().start();
    }
    public GSend() throws Exception{
        getServers().add(Protocol.HTTP , 8111);
        getClients().add(Protocol.HTTP);
        getDefaultHost().attach("/file/", FileResource.class);
    }
}
