package com.test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class Tema2 {
    // TODO Extract the folder name from the argument
    // TODO Extract the maximum number of threads from the argument and store it in a variable P
    // TODO Read the two files from the folder
    // TODO Create a parallel approach using ExecutorService with maximum P threads
    // TODO Create Makefile


    public static String ordersTextFile;
    public static String orderProductsTextFile;
    public static ExecutorService executorService;
    public static FileWriter ordersFileWriter;
    public static FileWriter orderProductsFileWriter;
    public static List<String> orders;
    public static List<String> products;
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
        executorService = Executors.newFixedThreadPool(P);
        // FileWriter object in java is by default synchronized
        try {
            ordersFileWriter = new FileWriter("orders_out.txt");
            orderProductsFileWriter = new FileWriter("order_products_out.txt");
            orders = Files.readAllLines(Paths.get(ordersTextFile));
            products = Files.readAllLines(Paths.get(orderProductsTextFile));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating output files");
        }

        int linesPerThread = (int) Math.round((double) orders.size() / P);
        if (linesPerThread == 0)
            linesPerThread = 1;

        int startLine = 0;
        int endLine = linesPerThread;

        List<Future<List<Future<String>>>> futures = new ArrayList<>();
        for (int i = 0; i < P; i++) {
            if (i == P - 1) {
                endLine = orders.size();
            }
            OrderHandlerThread orderHandlerThread = new OrderHandlerThread(startLine, endLine);
            futures.add(executorService.submit(orderHandlerThread));
            startLine = endLine;
            endLine += linesPerThread;
        }

        try {
            for (Future<List<Future<String>>> future : futures) {
                for (Future<String> future1 : future.get()) {
                    System.out.println(future1.get());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        try {
            ordersFileWriter.close();
            orderProductsFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
