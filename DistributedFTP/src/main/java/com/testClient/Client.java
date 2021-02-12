package com.testClient;

import com.clientCommunication.ClientComInterface;
import com.fileSerialization.FileWrapper;
import com.datatypes.InstructionWrapper;
import com.datatypes.MyResponse;
import com.encryption.KeySerialization;
import com.fileFinder.FileFinder;
import com.fileSerialization.FileWrapper;
import com.myServer.GSendProper;
import org.restlet.Context;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import javax.crypto.SecretKey;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Client {

    public static Client Instance;
    public List<FileWrapper> incomingDuplicates;
    private Client() {
        if (Instance == null) {
            Instance = this;
            incomingDuplicates = new ArrayList<>();
        }
    }
    public static boolean localOnly;


    public static void main(String[] args) throws Exception{
        //org.restlet.representation.ObjectRepresentation.VARIANT_OBJECT_BINARY_SUPPORTED = true;
        localOnly = args.length == 1 && args[0].equals("local");
        String dir = "", my_port = "";

        boolean duplicateFiles = false;

        System.out.println("Type \"help\" for list of commands.");

        new Client();
        Scanner scanner = new Scanner(System.in);
        while(true){
            FileWrapper fw = null;
            if (Client.Instance.incomingDuplicates.size() > 0 && !(dir.equals("") || my_port.equals(""))) {
                fw = Client.Instance.incomingDuplicates.get(0);
                if (fw == null) {
                    Client.Instance.incomingDuplicates.remove(0);
                } else {
                    System.out.println("Incoming file: " + fw.filename + " already exists. Do you want to replace it? [Y/N]");
                    duplicateFiles = true;
                }
            }
            // TODO Interrupt scanner in case of new duplicates above.
            String input = scanner.nextLine();
            String[] split = input.split("\\s+");
            String command = split[0].toLowerCase();

            if (duplicateFiles) {
                if (command.toUpperCase().equals("N")) {
                    // Change filname to x+"_duplicate" and save it
                    ClientResource clientResource = new ClientResource("http://localhost:"+my_port+"/client/");
                    ClientComInterface clientComInterface = clientResource.wrap(ClientComInterface.class);
                    InstructionWrapper instructions = InstructionWrapper.makeFileDuplicate2Instruction(fw.serialize());
                    clientComInterface.handleClient(new ObjectRepresentation<>(instructions));
                    fw = Client.Instance.incomingDuplicates.remove(0);
                    duplicateFiles = false;
                } else if (command.toUpperCase().equals("Y")) {
                    // Replace file.
                    ClientResource clientResource = new ClientResource("http://localhost:"+my_port+"/client/");
                    ClientComInterface clientComInterface = clientResource.wrap(ClientComInterface.class);
                    InstructionWrapper instructions = InstructionWrapper.makeFileDuplicateInstruction(fw.serialize());
                    clientComInterface.handleClient(new ObjectRepresentation<>(instructions));
                    fw = Client.Instance.incomingDuplicates.remove(0);
                    duplicateFiles = false;
                }
                continue;
            }

            if(command.equals("exit")){
                System.out.println("Exiting");
                System.exit(1);
            } else if(command.equals("create")){
                // create [port] [directory]
                // Creates a new instance with some target directory set
                if(split.length != 3){
                    System.out.println("create [port] [directory]");
                    continue;
                }
                my_port = split[1];
                dir = split[2];

                new GSendProper(Integer.parseInt(my_port)).start();
                ClientResource clientResource = new ClientResource("http://localhost:"+my_port+"/client/");
                ClientComInterface clientComInterface = clientResource.wrap(ClientComInterface.class);
                InstructionWrapper instructions = InstructionWrapper.makeTargetDirectoryInstruction(dir);
                MyResponse response = new ObjectRepresentation<MyResponse>(clientComInterface.handleClient(new ObjectRepresentation<>(instructions))).getObject();
                System.out.println(response.getString());
            } else if(command.equals("status")){
                // status [address]
                // Retrieve information about nodes on address ADDRESS.
                if(split.length != 2){
                    System.out.println("status [address]");
                    continue;
                }
                Context context = new Context();
                context.getParameters().add("socketConnectTimeoutMs","200");
                ClientResource clientResource = new ClientResource(context,"http://"+split[1]+"/client/");
                ClientComInterface clientComInterface = clientResource.wrap(ClientComInterface.class);
                InstructionWrapper instructions = InstructionWrapper.makeStatusInstruction();
                try{
                    MyResponse response = new ObjectRepresentation<MyResponse>(clientComInterface.handleClient(new ObjectRepresentation<>(instructions))).getObject();
                    System.out.println(response.getString());
                } catch (ResourceException e){
                    System.out.println("Couldn't reach server");
                }
            } else if(command.equals("join")){
                // join [port] [address]
                if(split.length != 3){
                    System.out.println("join [port] [address]");
                    continue;
                }
                ClientResource clientResource = new ClientResource("http://localhost:"+split[1]+"/client/");

                ClientComInterface clientComInterface = clientResource.wrap(ClientComInterface.class);
                InstructionWrapper instructions = InstructionWrapper.makeJoinInstruction(split[2]);
                try{
                MyResponse response = new ObjectRepresentation<MyResponse>(clientComInterface.handleClient(new ObjectRepresentation<>(instructions))).getObject();
                System.out.println(response.getString());
                } catch (Exception e){
                    System.out.println("Couldn't join");
                }

            } else if(command.equals("port")){
                // port [port] [new port]
                if(split.length != 3){
                    System.out.println("port [port] [port]");
                    continue;
                }
                ClientResource clientResource = new ClientResource("http://localhost:"+split[1]+"/client/");
                ClientComInterface clientComInterface = clientResource.wrap(ClientComInterface.class);
                InstructionWrapper instructions = InstructionWrapper.makeChangePortInstruction(Integer.parseInt(split[2]));
                try{
                    clientComInterface.handleClient(new ObjectRepresentation<>(instructions));
                } catch (ResourceException e){
                    System.out.println("Failed to change port");
                }
            } else if(command.equals("sync")){
                // Send files to all hosts in network. I.e. Synchronizes the given folder on all hosts.
                // TODO: Remove my_port for cleaner ui. Pass my_port as a parameter when executing the jar file?
                if(split.length != 2){
                    //System.out.println("sync my_port dir");
                    System.out.println("sync dir");
                    continue;
                }
                dir = split[1];
                //String port = split[1];
                String port = my_port;

                System.out.println("Syncing files in [" + dir + "] with all hosts in network...");
                ClientResource clientResource = new ClientResource("http://localhost:"+port+"/client/");
                ClientComInterface clientComInterface = clientResource.wrap(ClientComInterface.class);

                List<File> files = FileFinder.getFilesInDirectory(dir);
                for (File f : files) {
                    System.out.println("[SYNC] File: " + f.getName());
                    InstructionWrapper instructions = InstructionWrapper.makeSyncDirInstruction(FileWrapper.build(f).serialize());
                    clientComInterface.handleClient(new ObjectRepresentation<>(instructions));
                }

            } else if(command.equals("send")){
                // Send files to a specific host.
                if(split.length != 3){
                    System.out.println("send [address] dir");
                    continue;
                }
                dir = split[2];
                String address = split[1];

                System.out.println("Sending files in [" + dir + "] to address " + address + ".");
                Context context = new Context();
                context.getParameters().add("socketConnectTimeoutMs","200");
                ClientResource clientResource = new ClientResource(context,"http://"+address+"/file/");
                ClientComInterface clientComInterface = clientResource.wrap(ClientComInterface.class);

                List<File> files = FileFinder.getFilesInDirectory(dir);
                for (File f : files) {
                    System.out.println("[SEND] File: " + f.getName());
                    InstructionWrapper instructions = InstructionWrapper.makeSendFileInstruction(FileWrapper.build(f).serialize());
                    clientComInterface.handleClient(new ObjectRepresentation<>(instructions));
                }
            } else if(command.equals("help")){
                System.out.println("------------------------------------------------------");
                System.out.println("Create\t-\tCreate a repository.\n" +
                        "Join\t-\tJoin a network of synchronized respositories.\n" +
                        "Status\t-\tCheck status of current joined network.\n" +
                        "Port\t-\tChange repository port.\n" +
                        "Sync\t-\tSynchronize all repositories in network with a specified folder.\n" +
                        "Send\t-\tSend a file to a repository on a given port.\n");

                System.out.println("Type command with no additional information for usage.");
                System.out.println("------------------------------------------------------");
            }
        }
    }
}
