package com.twinsickle.aem.utils.ssh;

public class SFTPConnectionFailedException extends Exception {
    public SFTPConnectionFailedException(String message){
        super(message);
    }
}
