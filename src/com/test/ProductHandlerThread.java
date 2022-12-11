package com.test;

import java.io.IOException;
import java.util.concurrent.Callable;

public class ProductHandlerThread implements Callable<String> {
    private final String orderID;

    public ProductHandlerThread(String orderID) {
        this.orderID = orderID;
    }

    @Override
    public String call() {
        int currentQuantity = 0, n = Tema2.products.size(), i = 0;
        while (i < n) {
            // look for the orderId in the products list
            if (Tema2.products.get(i).split(",")[0].equals(orderID) && !Tema2.linesProcessed.contains(i)) {
                Tema2.linesProcessed.add(i);
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
                break;
            }
            i++;
        }
        return "Thread " + Thread.currentThread().getId() + " has processed " + orderID;
    }
}
