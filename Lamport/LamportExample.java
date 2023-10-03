package Lamport;

import java.util.concurrent.atomic.AtomicInteger;

class Bakery {
    private boolean[] choosing;
    private AtomicInteger[] number;
    
    public Bakery(int n) {
        choosing = new boolean[n];
        number = new AtomicInteger[n];
        for (int i = 0; i < n; i++) {
            choosing[i] = false;
            number[i] = new AtomicInteger(0);
        }
    }

    public void lock(int threadId) {
        choosing[threadId] = true;
        int max = 0;
        for (int i = 0; i < number.length; i++) {
            int num = number[i].get();
            max = Math.max(max, num);
        }
        number[threadId].set(max + 1);
        choosing[threadId] = false;
        for (int i = 0; i < number.length; i++) {
            while (choosing[i]) { } // Espera activa
            while ((number[i].get() != 0) && (number[i].get() < number[threadId].get() || (number[i].get() == number[threadId].get() && i < threadId))) { }
        }
    }

    public void unlock(int threadId) {
        number[threadId].set(0);
    }
}

public class LamportExample {
    private static final int THREADS = 5;
    private static Bakery bakery = new Bakery(THREADS);

    public static void main(String[] args) {
        for (int i = 0; i < THREADS; i++) {
            final int threadId = i;
            new Thread(() -> {
                bakery.lock(threadId);
                // Secci�n cr�tica
                System.out.println("Hilo " + threadId + " est� en la secci�n cr�tica.");
                bakery.unlock(threadId);
            }).start();
        }
    }
}

