package com.clientCommunication;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public interface ClientComInterface {
    @Post
    Representation handleClient(Representation instructionRepresentation) throws Exception;

}
