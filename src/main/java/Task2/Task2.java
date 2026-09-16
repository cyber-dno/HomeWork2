package Task2;

import java.util.concurrent.*;

public class Task2
{
    private static final int STOP_FLAG = -1;
    private static final BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(20);

    public static void main( String[] args ) throws InterruptedException {
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 100; i++) {
                    blockingQueue.put(i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                try {
                    blockingQueue.put(STOP_FLAG);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    int item = blockingQueue.take();
                    if (item == STOP_FLAG) {
                        break;
                    }
                    System.out.println("Достали: " + item + " В очереди осталось элементов: " + blockingQueue.size());
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("Очередь пуста");
    }
}