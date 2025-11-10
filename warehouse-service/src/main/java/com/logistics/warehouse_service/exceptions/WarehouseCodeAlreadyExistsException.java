package com.logistics.warehouse_service.exceptions;

public class WarehouseCodeAlreadyExistsException  extends  RuntimeException{
    public WarehouseCodeAlreadyExistsException(String msg){
        super(msg);
    }
}
