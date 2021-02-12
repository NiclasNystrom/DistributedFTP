package com.fileTransfer;

import org.restlet.representation.Representation;
import org.restlet.resource.Post;

public interface FileInterface {
    @Post
    Representation handleFile(Representation instructionRepresentation) throws Exception;
}
