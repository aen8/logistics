package com.logistics.delivery_service.exceptions;

public class NoAvailableDriverException extends RuntimeException{
    public  NoAvailableDriverException(String msg){
        super(msg) ;
    }
}
