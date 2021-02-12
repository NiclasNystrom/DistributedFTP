package com.fileTransfer;

import com.datatypes.InstructionWrapper;
import com.datatypes.MyResponse;
import com.datatypes.NotSingleton;
import com.encryption.FileEncryption;
import com.encryption.KeySerialization;
import com.fileFinder.FileFinder;
import com.fileSerialization.FileSerialization;
import com.fileSerialization.FileWrapper;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;
import com.testClient.Client;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

public class FileResource extends ServerResource implements FileInterface {

    public Representation handleFile(Representation instructionRepresentation) throws Exception {
        //org.restlet.representation.ObjectRepresentation.VARIANT_OBJECT_BINARY_SUPPORTED = true;
        InstructionWrapper instructions = new ObjectRepresentation<InstructionWrapper>(instructionRepresentation).getObject();
        switch (instructions.getInstructionEnum()){
            case MESSAGE:
                return messageThing(instructions);
            case FILE:
                return messageFile(instructions);
            case SENDFILE:
                return saveFile(instructions);
            case FILEDUPLICATE:
                return replaceFile(instructions, true);
            case FILEDUPLICATE2:
                return replaceFile(instructions, false);
        }
        return null;
    }
    private Representation messageThing(InstructionWrapper instructions){
        MyResponse response = new MyResponse("Idk, some string, you sent "+instructions.getString(),1);
        return new ObjectRepresentation<>(response);
    }

    private Representation messageFile(InstructionWrapper instructions){
        File f = FileFinder.lookupFile(instructions.getString()); // TODO: Add option for path later.
        String serialFile = "";
        int status = 1;
        if (f != null) {
            serialFile = FileSerialization.serializeFile(f);
        } else {
            status = -1;
        }
        MyResponse response = new MyResponse(serialFile, status);
        return new ObjectRepresentation<>(response);
    }

    private Representation saveFile(InstructionWrapper instructions){
        int status = 1;
        String message = "Saved File!";

        NotSingleton _this = NotSingleton.getInstance(getPort());
        if(_this != null){

            String _output = instructions.getString();
            try {
                byte[] b = FileEncryption.decrypt(DatatypeConverter.parseBase64Binary(instructions.getString()), KeySerialization.serializeKey(_this.getSecretKey()));
                _output = new String(b);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //FileWrapper fw = FileWrapper.deserialize(instructions.getString());
            FileWrapper fw = FileWrapper.deserialize(_output);
            if (fw != null) {
                String output = _this.getDirectory() + "/" + fw.filename;


                if (FileFinder.lookupFile(fw.filename, _this.getDirectory()) == null) {
                    // If there is no other file with same file. Just save it.
                    System.out.println("Saving file: " + fw.filename);
                    File f = FileSerialization.deserializeFile(fw.content, output);
                } else {
                    // Ask user to skip file or replace it.
                    System.out.println("Duplicate files incoming. Name: " + fw.filename);
                    Client.Instance.incomingDuplicates.add(fw);
                }
            }
        } else {
            status = -1;
            message = "Error: Save file. Missing dir?";
        }


        MyResponse response = new MyResponse(message, status);
        return new ObjectRepresentation<MyResponse>(response);
    }

    private Representation replaceFile(InstructionWrapper instructions, Boolean replace){
        int status = 1;
        String message = "Replace File!";

        NotSingleton _this = NotSingleton.getInstance(getPort());
        if(_this != null){
            FileWrapper fw = FileWrapper.deserialize(instructions.getString());
            if (fw != null) {
                if (replace) {
                    System.out.println("Replacing duplicate!");
                    String output = _this.getDirectory() + "/" + fw.filename;
                    File f = FileSerialization.deserializeFile(fw.content, output);
                } else {
                    // Add _duplicate to output.
                    String[] tokens = fw.filename.split("\\.(?=[^\\.]+$)");
                    if (tokens.length == 2) {
                        String name = tokens[0] + "_duplicate." + tokens[1];
                        System.out.println("Name2: " + name);
                        String output = _this.getDirectory() + "/" + name;
                        File f = FileSerialization.deserializeFile(fw.content, output);
                    } else {
                        System.err.println("Duplicate file: No extension or to many '.'? Len: " + tokens.length);
                    }

                }
                status = 1;
            }
        }
        MyResponse response = new MyResponse(message, status);
        return new ObjectRepresentation<MyResponse>(response);
    }

    /**
     * To avoid having to write "this.getServerInfo().getPort()" everywhere
     * @return
     */
    private int getPort(){
        return this.getServerInfo().getPort();
    }
    /**
     * It gets the IP online and stores it.
     * @return
     */
    private String getAddress(){
        if(NotSingleton.getInstance(getPort()).getAddress() == null){
            try{
                if(Client.localOnly){
                    NotSingleton.getInstance(getPort()).setAddress("localhost:"+getPort());
                } else {
                    URL url_name = new URL("http://ipv4bot.whatismyipaddress.com");
                    BufferedReader sc =
                            new BufferedReader(new InputStreamReader(url_name.openStream()));
                    String address = sc.readLine().trim();
                    NotSingleton.getInstance(getPort()).setAddress(address+":"+getPort());
                }
            } catch (Exception e){
                System.out.println("Couldn't get public IP");
            }
        }
        return NotSingleton.getInstance(getPort()).getAddress();
    }

}
