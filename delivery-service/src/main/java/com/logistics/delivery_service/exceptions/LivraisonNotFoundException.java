package com.logistics.delivery_service.exceptions;

public class LivraisonNotFoundException extends RuntimeException{
    public LivraisonNotFoundException(String msg){
        super(msg);
    }
}
