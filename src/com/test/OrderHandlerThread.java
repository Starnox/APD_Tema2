package com.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OrderHandlerThread implements Callable<Void> {
    private int startLine, endLine;

    public OrderHandlerThread(int startLine, int endLine)  {
        this.startLine = startLine;
        this.endLine = endLine;
    }

    @Override
    public Void call() {
        long id = Thread.currentThread().getId();
        System.out.println("Thread " + id + " started" + " with startLine " + startLine + " and endLine " + endLine);
        // Print the lines from startLine to endLine
        String orderId;
        int numberOfProducts;
        List<List<Future<String>>> futures = new ArrayList<>();
        for (int i = startLine; i < endLine; i++) {
            orderId = Tema2.orders.get(i).split(",")[0];
            numberOfProducts = Integer.parseInt(Tema2.orders.get(i).split(",")[1]);
            List<Future<String>> currentFutures = new ArrayList<>();

            for (int j = 0; j < numberOfProducts; ++j) {
                ProductHandlerThread productHandlerThread = new ProductHandlerThread(orderId);
                currentFutures.add(Tema2.executorServiceProducts.submit(productHandlerThread));
            }
            futures.add(currentFutures);
        }
        int k = startLine;
        for (List<Future<String>> currentFutures : futures) {
            for (Future<String> future : currentFutures) {
                try {
                    System.out.println(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            // after all the products for the current order have been processed, write the order to the orders_out.txt file
            try {
                Tema2.ordersFileWriter.write(Tema2.orders.get(k) + ",shipped" + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
            k++;
        }
        // write to orders_out.txt the fact that the order has been shipped
        return null;
    }
}
