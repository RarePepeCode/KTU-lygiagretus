package com.IgnasKucinas;

import org.w3c.dom.css.Counter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    public final static int NUMBER_OF_PAYMENTS = 26;
    public final static String DATA_FILE_NAME = "IFF-7_5_KucinasI_dat_1.csv";
    public final static String RESULT_FILE_NAME = "IFF-7_5_KucinasI_rez_1.txt";;


    public static void main(String[] args) throws IOException, InterruptedException{


        Payment[] allPayments = readData();

        Monitor dataMonitor = new Monitor(NUMBER_OF_PAYMENTS, NUMBER_OF_PAYMENTS / 2);
        Monitor resultMonitor = new Monitor(NUMBER_OF_PAYMENTS, NUMBER_OF_PAYMENTS);

        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1 started");
            while (!dataMonitor.finishWork())
            {
                Payment payment = dataMonitor.getPayment();
                if (payment.getNumber() / 2 == 0) {
                    resultMonitor.addPayment(payment);
                }
            }
            System.out.println("Thread 1 ended");
        });
        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2 started");
            while (!dataMonitor.finishWork())
            {
                Payment payment = dataMonitor.getPayment();
                if (payment.getNumber() / 2 == 0) {
                    resultMonitor.addPayment(payment);
                }
            }
            System.out.println("Thread 2 ended");
        });
        Thread thread3 = new Thread(() -> {
            System.out.println("Thread 3 started");
            while (!dataMonitor.finishWork())
            {
                Payment payment = dataMonitor.getPayment();
                if (payment.getNumber() / 2 == 0) {
                    resultMonitor.addPayment(payment);
                }
            }
            System.out.println("Thread 3 ended");
        });
        Thread thread4 = new Thread(() -> {
            System.out.println("Thread 4 started");
            while (!dataMonitor.finishWork())
            {
                Payment payment = dataMonitor.getPayment();
                if (payment.getNumber() / 2 == 0) {
                    resultMonitor.addPayment(payment);
                }
            }
            System.out.println("Thread 4 ended");
        });
        thread1.start();
        thread2.start();
        thread4.start();
        thread3.start();

        for (int i = 0; i < allPayments.length; i++){
            dataMonitor.addPayment(allPayments[i]);
        }

        thread1.join();
        thread2.join();
        thread4.join();
        thread3.join();

        System.out.println("Worker threads finished work");

        writeData(resultMonitor.getPayments());
    }

    private static Payment[] readData() throws IOException {
        int counter = 0;
        Payment[] payments = new Payment[NUMBER_OF_PAYMENTS];
        Path pathToFile = Paths.get(DATA_FILE_NAME);
        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");
                payments[counter++] = new Payment(Integer.parseInt(attributes[1]), attributes[0], Double.parseDouble(attributes[2]));
            }
        }
        return payments;
    }

    private static void writeData(Payment[] payments) throws IOException {
        File file = new File(RESULT_FILE_NAME);
        FileOutputStream fos = new FileOutputStream(file);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for(int i = 0; i < payments.length-1; i++){
            bw.write(payments[i].toString());
        }
        bw.close();
    }

    private static d

}

class Monitor {
    private Payment[] payments;
    private int counterIndex;
    private int readIndex;
    private int numberOfPayment;
    private int arraySize;

    public Monitor(int numberOfPayment, int arraySize) {
        payments = new Payment[arraySize];
        this.numberOfPayment = numberOfPayment;
        this.arraySize = arraySize;
        counterIndex = 0;
        readIndex = 0;
    }

    public synchronized void addPayment(Payment payment) {
        int index = counterIndex % arraySize;
        while ((payments[index] != null)) {
            try { wait(); }
            catch (InterruptedException e) { }
        }
        counterIndex++;
        payments[index] = payment;
        notify();
    }

    public synchronized Payment[] getPayments() {
        return payments;
    }

    public synchronized Payment getPayment() {
        int index = readIndex % arraySize;
        while ((payments[index] == null)) {
            try { wait(); }
            catch (InterruptedException e) { }
        }
        readIndex++;
        Payment payment = payments[index];
        payments[index] = null;
        notify();
        return payment;
    }

    public synchronized boolean finishWork() {        
        return isArrayEmpty() && numberOfPayment == counterIndex;
    }

    private  boolean isArrayEmpty() {
        for (Payment payment : this.payments) {
            if (payment != null) {
                return false;
            }
        }
        return true;
    }

}

class Payment {
    public final static int STRING_COLUMMN_LENGTH = 25;

    private int number;
    private String person;
    private double amount;

    public Payment(int number, String person, double amount) {
        this.number = number;
        this.person = person;
        this.amount = amount;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "| " + number +" | " + person + " ".repeat(STRING_COLUMMN_LENGTH - person.length()) +  " | " + amount + "\n";
    }
}
