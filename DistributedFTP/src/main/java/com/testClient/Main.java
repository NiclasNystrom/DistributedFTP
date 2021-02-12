package com.testClient;

import com.datatypes.InstructionWrapper;
import com.datatypes.MyResponse;
import com.fileFinder.FileFinder;
import com.fileSerialization.FileSerialization;
import com.fileTransfer.FileInterface;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.ClientResource;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        //org.restlet.representation.ObjectRepresentation.VARIANT_OBJECT_BINARY_SUPPORTED = true;
        ClientResource clientResource = new ClientResource("http://localhost:8111/file/");
        FileInterface fileInterface = clientResource.wrap(FileInterface.class);


        // Message Test
        System.out.println("Message Test");
        System.out.println("-------------------------------------------");
        InstructionWrapper instructions = InstructionWrapper.makeStringInstruction("test");
        MyResponse response = new ObjectRepresentation<MyResponse>(fileInterface.handleFile(new ObjectRepresentation<InstructionWrapper>(instructions))).getObject();
        System.out.println(response.getString());
        System.out.println("-------------------------------------------");


        System.out.println("\nFile Test");
        System.out.println("-------------------------------------------");
        instructions = InstructionWrapper.makeFileInstruction(FileFinder.TEST_FILE_NO_PATH);
        response = new ObjectRepresentation<MyResponse>(fileInterface.handleFile(new ObjectRepresentation<InstructionWrapper>(instructions))).getObject();
        FileSerialization.deserializeFile(response.getString(), "DeserializedFile.jpg");
        System.out.println("Result: " + response.getStatus() + "\nDeserialized file in working directory as \"DeserializedFile.jpg\"");
        System.out.println("-------------------------------------------");

    }
}
