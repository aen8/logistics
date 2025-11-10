package com.logistics.user_service.exceptions;

public class UserNotFoundException extends  RuntimeException{
    public UserNotFoundException(String msg){
        super(msg);
    }
}
