package com.test;

import java.util.concurrent.Callable;

public class ProductHandlerThread implements Callable<String> {
    private String orderID;
    private int quantity;

    public ProductHandlerThread(String orderID, int quantity) {
        this.orderID = orderID;
        this.quantity = quantity;
    }

    @Override
    public String call() throws Exception {
        //Tema2.orderProductsFileWriter.write();
        return "Thread " + Thread.currentThread().getId() + " has processed " + orderID + " with quantity " + quantity;
    }
}
