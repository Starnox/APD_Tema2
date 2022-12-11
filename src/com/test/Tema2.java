package com.test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class Tema2 {
    public static String ordersTextFile;
    public static String orderProductsTextFile;
    public static ExecutorService executorServiceOrders;
    public static ExecutorService executorServiceProducts;
    public static FileWriter ordersFileWriter;
    public static FileWriter orderProductsFileWriter;

    public static Set<Integer> orderLinesProcessed = ConcurrentHashMap.newKeySet();
    public static Set<Integer> productLinesProcessed = ConcurrentHashMap.newKeySet();



    public static int P;

    public static void getArguments(String[] args) {
        ordersTextFile = args[0] + "/orders.txt";
        orderProductsTextFile = args[0] + "/order_products.txt";
        P = Integer.parseInt(args[1]);
    }

    public static void main(String[] args) {
        getArguments(args);
        // Create a parallel approach using ExecutorService with maximum P threads
        // Assign each OrderHandlerThread a part of the file to read using startLine and endLine
        executorServiceOrders = Executors.newFixedThreadPool(P);
        executorServiceProducts = Executors.newFixedThreadPool(P);
        // FileWriter object in java is by default synchronized

        BufferedReader ordersBufferedReader = null;
        BufferedReader orderProductsBufferedReader = null;
        try {
            ordersFileWriter = new FileWriter("orders_out.txt");
            orderProductsFileWriter = new FileWriter("order_products_out.txt");
            ordersBufferedReader = Files.newBufferedReader(Paths.get(ordersTextFile));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating output files");
        }

        List<Future<Void>> futures = new ArrayList<>();
        for (int i = 0; i < P; i++) {
            OrderHandlerThread orderHandlerThread = new OrderHandlerThread(ordersBufferedReader);
            futures.add(executorServiceOrders.submit(orderHandlerThread));
        }

        // wait for all the threads to finish
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorServiceOrders.shutdown();
        executorServiceProducts.shutdown();
        try {
            if (!executorServiceOrders.awaitTermination(2000, TimeUnit.MILLISECONDS)) {
                executorServiceOrders.shutdownNow();
            }
            if (!executorServiceProducts.awaitTermination(2000, TimeUnit.MILLISECONDS)) {
                executorServiceProducts.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorServiceOrders.shutdownNow();
            executorServiceProducts.shutdownNow();
        }

        try {
            ordersFileWriter.close();
            orderProductsFileWriter.close();
            assert ordersBufferedReader != null;
            ordersBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
