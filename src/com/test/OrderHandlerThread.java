package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OrderHandlerThread implements Callable<Void> {
    private final BufferedReader ordersBufferedReader;

    public OrderHandlerThread(BufferedReader ordersBufferedReader)  {
        this.ordersBufferedReader = ordersBufferedReader;
    }

    @Override
    public Void call() throws IOException {
        long id = Thread.currentThread().getId();
        // Print the lines from startLine to endLine
        String orderId;
        int numberOfProducts;
        List<List<Future<String>>> futures = new ArrayList<>();
        String line;
        List<String> lines = new ArrayList<>();

        while ((line = ordersBufferedReader.readLine()) != null) {
            System.out.println("Thread " + id + " is processing order " + line);
            lines.add(line);
            orderId = line.split(",")[0];
            numberOfProducts = Integer.parseInt(line.split(",")[1]);
            List<Future<String>> currentFutures = new ArrayList<>();

            for (int j = 0; j < numberOfProducts; ++j) {
                ProductHandlerThread productHandlerThread = new ProductHandlerThread(orderId);
                currentFutures.add(Tema2.executorServiceProducts.submit(productHandlerThread));
            }
            futures.add(currentFutures);

        }
        int k = 0;
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
                Tema2.ordersFileWriter.write(lines.get(k) + ",shipped" + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
            k++;
        }
        return null;
    }
}
