package com.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OrderHandlerThread implements Callable<List<Future<String>>> {
    private int startLine, endLine;

    public OrderHandlerThread(int startLine, int endLine)  {
        this.startLine = startLine;
        this.endLine = endLine;
    }

    @Override
    public List<Future<String>> call() throws Exception {
        long id = Thread.currentThread().getId();
        System.out.println("Thread " + id + " started" + " with startLine " + startLine + " and endLine " + endLine);
        // Print the lines from startLine to endLine
        String orderId;
        int numberOfProducts;
        List<Future<String>> futures = new ArrayList<>();
        for (int i = startLine; i < endLine; i++) {
            orderId = Tema2.orders.get(i).split(",")[0];
            numberOfProducts = Integer.parseInt(Tema2.orders.get(i).split(",")[1]);
            String s = "Thread " + id + " is processing order " + orderId;
            System.out.println(s);
            // Using the ExecutorService from Tema2, add to the queue a new OrderHandlerThread
            // which will receive the orderId and numberOfProducts
            if (numberOfProducts > 0)
                futures.add(Tema2.executorService.submit(new ProductHandlerThread(orderId, numberOfProducts)));
        }
        return futures;
    }
}
