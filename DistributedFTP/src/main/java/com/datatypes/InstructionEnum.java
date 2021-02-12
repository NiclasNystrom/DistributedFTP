package com.datatypes;

// So the server knows what type of instruction it is
public enum InstructionEnum {
    MESSAGE,
    FILE,
    SETDIR,
    STATUS,
    JOIN,
    SERVERJOIN,
    SERVERPROPAGATE,
    CHANGEPORT,
    SENDFILE,
    SYNCDIRS,
    FILEDUPLICATE,
    FILEDUPLICATE2
}
