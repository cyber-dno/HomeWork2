package Task1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerTask1
{
    public static AtomicInteger count = new AtomicInteger(0);

    public static void main( String[] args ) throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);

        List<Future<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(service.submit(new FirstCallable()));
        }
        for (Future<Void> f : futures) {
            f.get();
        }

        service.shutdown();

        System.out.println(count);
    }

    public static void increment() {
        count.incrementAndGet();
    }

    public static class FirstCallable implements Callable<Void> {

        @Override
        public  Void call() {
            for (int i = 0; i < 1000; i++) {
                increment();
            }
            return null;
        }
    }
}
