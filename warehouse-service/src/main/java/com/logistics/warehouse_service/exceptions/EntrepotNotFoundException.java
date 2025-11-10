package com.logistics.warehouse_service.exceptions;

public class EntrepotNotFoundException extends  RuntimeException{
    public EntrepotNotFoundException(String msg){
        super(msg);
    }
}
