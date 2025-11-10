package com.logistics.warehouse_service.exceptions;

public class InsufficientCapacityException extends RuntimeException{
    public InsufficientCapacityException(String msg){
        super(msg);
    }
}
