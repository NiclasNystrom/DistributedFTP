package com.serverCommunication;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public interface ServerComInterface {
    @Post
    Representation handleServer(Representation instructionRepresentation) throws Exception;
}
