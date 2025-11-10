package com.logistics.warehouse_service.exceptions;

public class StockNotFoundException extends  RuntimeException{
    public StockNotFoundException(String msg){
        super(msg);
    }
}
