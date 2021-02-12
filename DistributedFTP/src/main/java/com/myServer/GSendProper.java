package com.myServer;

import com.clientCommunication.ClientComResource;
import com.fileTransfer.FileResource;
import com.datatypes.NotSingleton;
import com.fileTransfer.FileResource;
import org.restlet.Component;
import org.restlet.data.Protocol;
import com.serverCommunication.ServerComResource;


public class GSendProper extends Component {
    public GSendProper(int port) {
        getServers().add(Protocol.HTTP, port);
        getClients().add(Protocol.HTTP);
        getDefaultHost().attach("/file/", FileResource.class);
        getDefaultHost().attach("/client/", ClientComResource.class);
        getDefaultHost().attach("/server/", ServerComResource.class);
        NotSingleton.getInstance(port).setgSendProper(this);
    }
    public GSendProper(int port, int stealPort) {
        getServers().add(Protocol.HTTP, port);
        getClients().add(Protocol.HTTP);
        getDefaultHost().attach("/file/", FileResource.class);
        getDefaultHost().attach("/client/", ClientComResource.class);
        getDefaultHost().attach("/server/", ServerComResource.class);
        NotSingleton.copyContent(stealPort, port);
    }
}
