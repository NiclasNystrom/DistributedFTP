package com.serverCommunication;

import com.datatypes.InstructionWrapper;
import com.datatypes.MyResponse;
import com.datatypes.MyServerInfo;
import com.datatypes.NotSingleton;
import org.restlet.Context;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ServerResource;
import com.testClient.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class ServerComResource extends ServerResource implements ServerComInterface {
    @Override
    public Representation handleServer(Representation instructionRepresentation) throws Exception {
        //org.restlet.representation.ObjectRepresentation.VARIANT_OBJECT_BINARY_SUPPORTED = true;
        InstructionWrapper instructions = new ObjectRepresentation<InstructionWrapper>(instructionRepresentation).getObject();
        switch (instructions.getInstructionEnum()){
            case SERVERJOIN:
                return acceptJoining(instructions);
            case SERVERPROPAGATE:
                return propagate(instructions);
        }
        return null;
    }

    /**
     * If the list of those being propagated is the same as the stored one, it returns null.
     * @param instructions List of members that have been discovered
     * @return null or expanded list of members
     */
    private Representation propagate(InstructionWrapper instructions){
        if(NotSingleton.getInstance(getPort()).getMyServerInfos() == null){
            MyResponse response = new MyResponse("Nah I shouldn't propagate", 1);
            return new ObjectRepresentation<>(response);
        }
        System.out.println("Start of propagate as "+NotSingleton.getInstance(getPort()).getAddress());
        HashSet<MyServerInfo> myServerInfos = NotSingleton.getInstance(getPort()).getMyServerInfos();
        MyResponse response = new MyResponse("idk, test", 1);

        if(instructions.getJoiners().containsAll(myServerInfos)){
            //We both have the same, or the joining has more than me, no duplication possible either way
            myServerInfos.addAll(instructions.getJoiners());
        } else {
            //If I have everything the joiner has, adding all is the same as contains
            //If I don't have everything, just add it directly
            myServerInfos.addAll(instructions.getJoiners());
            //Send only to those not in the list of joiners
            //If someone is in that list, the one that called this one will call those
            HashSet<MyServerInfo> uniques = new HashSet<>(myServerInfos);
            for(MyServerInfo myServerInfo : instructions.getJoiners())
                if(!uniques.add(myServerInfo))
                    uniques.remove(myServerInfo);
            LinkedList<MyServerInfo> sendQueue = new LinkedList<>(uniques);
            while(sendQueue.size() != 0){
                MyServerInfo myServerInfo = sendQueue.pop();
                //I can't be in this set anyway, no need to check
                Context context = new Context();
                context.getParameters().add("socketConnectTimeoutMs","200");
                ClientResource clientResource = new ClientResource(context,"http://"+myServerInfo.getAddress()+"/server/");
                ServerComInterface serverComInterface = clientResource.wrap(ServerComInterface.class);
                InstructionWrapper instructionWrapper = InstructionWrapper.makeServerPropagateInstruction(new HashSet<>(myServerInfos));
                System.out.println("Trying to message "+myServerInfo.getAddress() +" as "+getAddress());
                try{
                    MyResponse response1 = new ObjectRepresentation<MyResponse>(serverComInterface.handleServer(new ObjectRepresentation<>(instructionWrapper))).getObject();
                    if(response1.getMyServerInfos() != null){
                        //If it returns null it means it couldn't add anything
                        myServerInfos.addAll(response1.getMyServerInfos());
                    }
                } catch (Exception e){
                    System.out.println("Sending to "+myServerInfo.getAddress()+" failed, deleting it.");
                    myServerInfos.remove(myServerInfo);
                    continue;
                }
            }
            response.setMyServerInfos(myServerInfos);
        }
        return new ObjectRepresentation<>(response);
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

    /**
     * This is called when a single server tries to join a new network
     * @param instructions
     * @return result of joining
     */
    private Representation acceptJoining(InstructionWrapper instructions) {
        MyResponse response;
        int status;
        String message;
        if(NotSingleton.getInstance(getPort()).getMyServerInfos() != null){
            message = "Had a list already, adding own and sending all of it.";
            status = 1;
        }else{
            message = "Did not have a list, making a new one.";
            HashSet<MyServerInfo> myServerInfos = new HashSet<>();
            MyServerInfo myServerInfo = new MyServerInfo(getAddress(), NotSingleton.getInstance(getPort()).getKeyPair().getPublic(), NotSingleton.getInstance(getPort()).getSecretKey());
            myServerInfos.add(myServerInfo);
            NotSingleton.getInstance(getPort()).setMyServerInfos(myServerInfos);
            status = 1;
        }
        response = new MyResponse(message, status);
        if(status == 1){
            //Add the new one to the list
            NotSingleton.getInstance(getPort()).getMyServerInfos().add(instructions.getCallerInfo());


            HashSet<MyServerInfo> myServerInfos = NotSingleton.getInstance(getPort()).getMyServerInfos();


            LinkedList<MyServerInfo> sendQueue = new LinkedList<>(myServerInfos);
            while(sendQueue.size() != 0){
                MyServerInfo myServerInfo = sendQueue.pop();
                if(myServerInfo.equals(instructions.getCallerInfo())){
                    //Don't send to the one trying to join
                    continue;
                }
                if(myServerInfo.equals(new MyServerInfo(getAddress(), NotSingleton.getInstance(getPort()).getKeyPair().getPublic(), NotSingleton.getInstance(getPort()).getSecretKey()))){
                    //Don't send to self
                    continue;
                }
                //Send and get
                Context context = new Context();
                context.getParameters().add("socketConnectTimeoutMs","200");
                ClientResource clientResource = new ClientResource(context,"http://"+myServerInfo.getAddress()+"/server/");
                ServerComInterface serverComInterface = clientResource.wrap(ServerComInterface.class);

                InstructionWrapper instructionWrapper = InstructionWrapper.makeServerPropagateInstruction(new HashSet<>(myServerInfos));

                System.out.println("Trying to message "+myServerInfo.getAddress()+" as "+getAddress());
                try{
                    MyResponse response1 = new ObjectRepresentation<MyResponse>(serverComInterface.handleServer(new ObjectRepresentation<>(instructionWrapper))).getObject();
                    if(response1.getMyServerInfos() != null){
                        //If it returns null it means it couldn't add anything
                        myServerInfos.addAll(response1.getMyServerInfos());
                    }
                } catch (Exception e){
                    System.out.println("Sending to "+myServerInfo.getAddress()+" failed, deleting it.");
                    myServerInfos.remove(myServerInfo);
                    continue;
                }
            }
            //Send back a copy
            response.setMyServerInfos(new HashSet<>(myServerInfos));
        }
        return new ObjectRepresentation<>(response);
    }

}
