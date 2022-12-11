package com.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

public class ProductHandlerThread implements Callable<String> {
    private final String orderID;
    private String productID;
    private BufferedReader orderProductsBufferedReader;

    public ProductHandlerThread(String orderID) throws IOException {
        this.orderID = orderID;
        orderProductsBufferedReader = Files.newBufferedReader(new File(Tema2.orderProductsTextFile).toPath());
    }

    @Override
    public String call() throws IOException {
        int currentQuantity = 0, i = 0;
        String line;
        while ((line = orderProductsBufferedReader.readLine()) != null) {
            // look for the orderId in the products list
            if (line.split(",")[0].equals(orderID) && !Tema2.productLinesProcessed.contains(i)) {
                Tema2.productLinesProcessed.add(i);
                productID = line.split(",")[1];
                // if the orderId is found, write the line to the order_products_out.txt file
                // and increment the currentQuantity
                System.out.println("Thread " + Thread.currentThread().getId() + " is processing product " + line);
                try {
                    Tema2.orderProductsFileWriter.write(line + ",shipped" + System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            i++;
        }
        return "Thread " + Thread.currentThread().getId() + " has processed " + productID;
    }
}
