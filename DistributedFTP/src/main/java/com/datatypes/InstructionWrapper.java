package com.datatypes;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.HashSet;

/**
 * Objectrepresentation makes it possible to send objects
 * To keep it simple, let's use this object
 */
public class InstructionWrapper implements Serializable {
    private static final long serialVersionUID = 7133594764730256379L;

    // Variables
    private String string;
    private InstructionEnum instructionEnum;
    private PublicKey publicKey;
    private int anInt;
    private MyServerInfo callerInfo;
    private HashSet<MyServerInfo> joiners;

    // Empty private constructor
    private InstructionWrapper(){

    }
    public static InstructionWrapper makeStringInstruction(String string){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setString(string);
        instructionWrapper.setInstructionEnum(InstructionEnum.MESSAGE);
        return instructionWrapper;
    }
    public static InstructionWrapper makeTargetDirectoryInstruction(String path){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setString(path);
        instructionWrapper.setInstructionEnum(InstructionEnum.SETDIR);
        return instructionWrapper;
    }
    public static InstructionWrapper makeStatusInstruction(){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setInstructionEnum(InstructionEnum.STATUS);
        return instructionWrapper;
    }
    public static InstructionWrapper makeJoinInstruction(String address){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setInstructionEnum(InstructionEnum.JOIN);
        instructionWrapper.setString(address);
        return instructionWrapper;
    }
    public static InstructionWrapper makeServerJoinInstruction(MyServerInfo callerInfo){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setInstructionEnum(InstructionEnum.SERVERJOIN);
        instructionWrapper.setCallerInfo(callerInfo);
        return instructionWrapper;
    }
    public static InstructionWrapper makeServerPropagateInstruction(HashSet<MyServerInfo> joiners){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setInstructionEnum(InstructionEnum.SERVERPROPAGATE);
        instructionWrapper.setJoiners(joiners);
        return instructionWrapper;
    }
    public static InstructionWrapper makeChangePortInstruction(int port){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setInstructionEnum(InstructionEnum.CHANGEPORT);
        instructionWrapper.setAnInt(port);
        return instructionWrapper;
    }

    public static InstructionWrapper makeFileInstruction(String filename){ // TODO: Path maybe
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setString(filename);
        instructionWrapper.setInstructionEnum(InstructionEnum.FILE);
        return instructionWrapper;
    }

    public static InstructionWrapper makeSendFileInstruction(String serialFW){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setString(serialFW);
        instructionWrapper.setInstructionEnum(InstructionEnum.SENDFILE);
        return instructionWrapper;
    }
    public static InstructionWrapper makeSyncDirInstruction(String serialFW){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setString(serialFW);
        instructionWrapper.setInstructionEnum(InstructionEnum.SYNCDIRS);
        return instructionWrapper;
    }

    public static InstructionWrapper makeFileDuplicateInstruction(String serialFW){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setString(serialFW);
        instructionWrapper.setInstructionEnum(InstructionEnum.FILEDUPLICATE);
        return instructionWrapper;
    }
    public static InstructionWrapper makeFileDuplicate2Instruction(String serialFW){
        InstructionWrapper instructionWrapper = new InstructionWrapper();
        instructionWrapper.setString(serialFW);
        instructionWrapper.setInstructionEnum(InstructionEnum.FILEDUPLICATE2);
        return instructionWrapper;
    }

    // Getters and setters
    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public InstructionEnum getInstructionEnum() {
        return instructionEnum;
    }

    public void setInstructionEnum(InstructionEnum instructionEnum) {
        this.instructionEnum = instructionEnum;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

    public MyServerInfo getCallerInfo() {
        return callerInfo;
    }

    public void setCallerInfo(MyServerInfo callerInfo) {
        this.callerInfo = callerInfo;
    }

    public HashSet<MyServerInfo> getJoiners() {
        return joiners;
    }

    public void setJoiners(HashSet<MyServerInfo> joiners) {
        this.joiners = joiners;
    }
}
