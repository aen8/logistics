package com.logistics.notification_service.exception;

public class NotificationNotFoundException extends RuntimeException{
    public NotificationNotFoundException(String msg){
        super(msg) ;
    }
}
