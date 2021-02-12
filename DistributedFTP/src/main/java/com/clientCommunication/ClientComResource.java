package com.clientCommunication;

import com.datatypes.InstructionWrapper;
import com.datatypes.MyResponse;
import com.datatypes.MyServerInfo;
import com.datatypes.NotSingleton;
import com.encryption.AESEncryption;
import com.encryption.FileEncryption;
import com.encryption.KeySerialization;
import com.encryption.RSAEncryption;
import com.fileSerialization.FileSerialization;
import com.fileTransfer.FileInterface;
import com.myServer.GSendProper;
import org.restlet.Context;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ServerResource;
import com.serverCommunication.ServerComInterface;
import com.testClient.Client;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;

public class ClientComResource extends ServerResource implements ClientComInterface {
    public Representation handleClient(Representation instructionRepresentation) throws Exception {
        //org.restlet.representation.ObjectRepresentation.VARIANT_OBJECT_BINARY_SUPPORTED = true;
        InstructionWrapper instructions = new ObjectRepresentation<InstructionWrapper>(instructionRepresentation).getObject();
        switch (instructions.getInstructionEnum()){
            case SETDIR:
                return setDir(instructions);
            case STATUS:
                return getGSendStatus();
            case JOIN:
                return joinGroup(instructions);
            case CHANGEPORT:
                return changePort(instructions);
            case SYNCDIRS:
                return syncDirs(instructions);
            case SENDFILE:
                return sendFile(instructions);
            case FILEDUPLICATE:
                return duplicateFile(instructions, true);
            case FILEDUPLICATE2:
                return duplicateFile(instructions, false);
        }
        return null;
    }

    /**
     * A hilariously terrible way of changing port. However, it does do the job of simulating one server changing port.
     * @param instructions Contains which port to change to
     * @return A response
     * @throws Exception To have
     */
    private Representation changePort(InstructionWrapper instructions) throws Exception{
        MyResponse response = new MyResponse("Hopefully started a new server",1);
        new GSendProper(instructions.getAnInt(), getPort()).start();
        (new Thread() {
            public void run() {
                try{
                    sleep(50);
                    //NotSingleton.getInstance(getPort()).getgSendProper().stop();
                    NotSingleton.getInstance(getPort()).getgSendProper().stop();
                } catch (Exception e){
                    System.out.println("Couldn't stop, no clue.");
                }
            }
        }).start();
        return new ObjectRepresentation<>(response);
    }

    private Representation joinGroup(InstructionWrapper instructions){
        Context context = new Context();
        context.getParameters().add("socketConnectTimeoutMs","200");
        ClientResource chordClientResource = new ClientResource(context,"http://"+instructions.getString()+"/server/");
        ServerComInterface serverComInterface = chordClientResource.wrap(ServerComInterface.class);
        InstructionWrapper instructionWrapper = InstructionWrapper.makeServerJoinInstruction(new MyServerInfo(getAddress(),NotSingleton.getInstance(getPort()).getKeyPair().getPublic(), NotSingleton.getInstance(getPort()).getSecretKey()));
        MyResponse response;
        try {
            response = new ObjectRepresentation<MyResponse>(serverComInterface.handleServer(new ObjectRepresentation<>(instructionWrapper))).getObject();
        } catch (Exception e){
            response = new MyResponse("no", 0);
        }
        String message;
        int status;
        if(response.getStatus() == 1){
            message = "Idk, worked or something?\nNow in a network with "+response.getMyServerInfos().size()+" nodes.";
            //System.out.println("Connected to network with " + response.getMyServerInfos().size());
            //message =  response.getString(); // secret key
            status = 1;
        } else {
            message = "Didn't work, couldn't join or something";
            status = 0;
        }
        if(status == 1){
            NotSingleton.getInstance(getPort()).setMyServerInfos(response.getMyServerInfos());
        }
        response = new MyResponse(message, status);
        return new ObjectRepresentation<>(response);
    }
    private Representation getGSendStatus(){
        String message;
        int status;
        if(NotSingleton.getInstance(getPort()).getDirectory() == null){
            message = "Server running on port "+getPort()+" without a target directory.";
            status = 1;
        } else {
            message = "Server running on port "+getPort()+", target directory is "+NotSingleton.getInstance(getPort()).getDirectory();
            status = 1;
            if(NotSingleton.getInstance(getPort()).getMyServerInfos() != null){
                HashSet<MyServerInfo> myServerInfos = NotSingleton.getInstance(getPort()).getMyServerInfos();
                message += "\nIn a network with "+myServerInfos.size()+" nodes.";
                for(MyServerInfo myServerInfo : myServerInfos){
                    message += "\n"+myServerInfo.getAddress()+" - "+myServerInfo.getPublicKey();
                }
            } else {
                message += "\nNot in a network.";
            }
        }
        MyResponse response = new MyResponse(message, status);
        return new ObjectRepresentation<>(response);
    }
    /**
     * To avoid having to write "this.getServerInfo().getPort()" everywhere
     * @return port
     */
    private int getPort(){
        return this.getServerInfo().getPort();
    }
    /**
     * It gets the IP online and stores it.
     * @return address
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
    private Representation setDir(InstructionWrapper instructions) throws Exception{
        MyResponse response;
        String message;
        int status;
        File f = new File(instructions.getString());
        if(f.exists()){
            if(f.isDirectory()){
                if(f.list().length == 0){
                    message = "New instance created with empty, reachable directory "+instructions.getString()+", address is "+getAddress();
                    status = 1;
                    if(!f.canWrite()){
                        message += "\nExcept, wait, it can't be written to.";
                        status = 0;
                    }
                } else {
                    message = instructions.getString()+" is not empty or there are other issues.";
                    status = 0;
                }
            } else {
                message = "This... This isn't even a directory, what?";
                status = 0;
            }
        }else{
            message = instructions.getString()+" can't be reached or it doesn't exist.";
            status = 0;
        }
        if(status == 1){
            if(NotSingleton.getInstance(getPort()).getDirectory() == null){
                NotSingleton.getInstance(getPort()).setKeyPair(RSAEncryption.buildKeyPair());
                NotSingleton.getInstance(getPort()).setSecretKey(AESEncryption.buildSecretKey());
                System.out.println("Generated key: " + NotSingleton.getInstance(getPort()).getSecretKey().getEncoded());
            }
            NotSingleton.getInstance(getPort()).setDirectory(instructions.getString());
        }


        response = new MyResponse(message, status);
        return new ObjectRepresentation<>(response);
    }


    private Representation syncDirs(InstructionWrapper instructions) throws Exception{

        int status;
        String message;

        if(NotSingleton.getInstance(getPort()).getDirectory() == null){
            message = "Sync dirs on port "+getPort()+" without a target directory.";
            status = 1;
        } else {
            message = "Sync dirs on port "+getPort()+", target directory is "+NotSingleton.getInstance(getPort()).getDirectory();
            status = 1;

            if(NotSingleton.getInstance(getPort()).getMyServerInfos() != null){

                InstructionWrapper iw = InstructionWrapper.makeSendFileInstruction(instructions.getString());

                HashSet<MyServerInfo> myServerInfos = NotSingleton.getInstance(getPort()).getMyServerInfos();
                for(MyServerInfo myServerInfo : myServerInfos){
                    System.out.println("[Sync] Sending file to: " + myServerInfo.getAddress());
                    Context context = new Context();
                    context.getParameters().add("socketConnectTimeoutMs","200");

                    //int port = Integer.parseInt(myServerInfo.getAddress().split(":")[1]);
                    SecretKey _k = myServerInfo.getSecretKey();
                    if (_k == null)
                        System.out.println("Key null");
                    else {
                        //System.out.println("Key: " + _k.getEncoded());
                        byte[] b = FileEncryption.encrypt(instructions.getString(), KeySerialization.serializeKey(_k));
                        iw.setString(DatatypeConverter.printBase64Binary(b));
                    }

                    ClientResource clientResource = new ClientResource(context,"http://"+myServerInfo.getAddress()+"/file/");
                    FileInterface fileInterface = clientResource.wrap(FileInterface.class);
                    fileInterface.handleFile(new ObjectRepresentation<>(iw));
                }

            } else {
                message += "\nSync: Not in a network.";
            }
        }


        MyResponse response = new MyResponse(message, status);
        return new ObjectRepresentation<>(response);
    }


    private Representation sendFile(InstructionWrapper instructions) throws Exception{

        int status;
        String message;

        if(NotSingleton.getInstance(getPort()).getDirectory() == null){
            message = "Send file on port "+getPort()+" without a target directory.";
            status = 1;
        } else {
            message = "Send file on port "+getPort()+", target directory is "+ NotSingleton.getInstance(getPort()).getDirectory();
            status = 1;

            if(NotSingleton.getInstance(getPort()).getMyServerInfos() != null){
                System.out.println("Send file to: " + getAddress());
                InstructionWrapper iw = InstructionWrapper.makeSendFileInstruction(instructions.getString());
                Context context = new Context();
                context.getParameters().add("socketConnectTimeoutMs","200");
                ClientResource clientResource = new ClientResource(context,"http://"+getAddress() + "/file/");
                FileInterface fileInterface = clientResource.wrap(FileInterface.class);
                fileInterface.handleFile(new ObjectRepresentation<>(iw));

            } else {
                message += "\nSync: Not in a network.";
            }
        }


        MyResponse response = new MyResponse(message, status);
        return new ObjectRepresentation<>(response);
    }


    private Representation duplicateFile(InstructionWrapper instructions, Boolean replace) throws Exception{

        int status = -1;
        String message;

        if(NotSingleton.getInstance(getPort()).getDirectory() == null){
            message = "Duplicate file on port "+getPort()+" without a target directory.";
        } else {
            message = "Duplicate file on port "+getPort()+", target directory is "+ NotSingleton.getInstance(getPort()).getDirectory();
            if(NotSingleton.getInstance(getPort()).getMyServerInfos() != null){
                InstructionWrapper iw = null;
                if (replace) {
                    iw = InstructionWrapper.makeFileDuplicateInstruction(instructions.getString());
                }  else {
                    iw = InstructionWrapper.makeFileDuplicate2Instruction(instructions.getString());
                }
                Context context = new Context();
                context.getParameters().add("socketConnectTimeoutMs","200");
                ClientResource clientResource = new ClientResource(context,"http://"+getAddress() + "/file/");
                FileInterface fileInterface = clientResource.wrap(FileInterface.class);
                fileInterface.handleFile(new ObjectRepresentation<>(iw));
                status = 1;
            } else {
                message += "\nDuplicate file: Not in a network.";
            }
        }


        MyResponse response = new MyResponse(message, status);
        return new ObjectRepresentation<>(response);
    }





}
