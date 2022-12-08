package com.test;

import java.io.IOException;
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
        int currentQuantity = 0, n = Tema2.products.size(), i = 0;
        while (currentQuantity < quantity && i < n) {
            // look for the orderId in the products list
            if (Tema2.products.get(i).split(",")[0].equals(orderID)) {
                // if the orderId is found, write the line to the order_products_out.txt file
                // and increment the currentQuantity
                String s = Tema2.products.get(i);
                System.out.println("Thread " + Thread.currentThread().getId() + " is processing product " + s);
                try {
                    Tema2.orderProductsFileWriter.write(s + ",shipped");
                    Tema2.orderProductsFileWriter.write(System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentQuantity++;
            }
            i++;
        }
        return "Thread " + Thread.currentThread().getId() + " has processed " + orderID + " with quantity " + quantity;
    }
}
