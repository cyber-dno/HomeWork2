package gigabank.annotation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogExecutionTimeProcessor implements InvocationHandler {
    private final Object target;

    public LogExecutionTimeProcessor(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method targetMethod = target.getClass().getMethod(
                method.getName(),
                method.getParameterTypes()
        );

        if (targetMethod.isAnnotationPresent(LogExecutionTime.class)){
            System.out.println(method.getName() + " started");
            long startTime = System.currentTimeMillis();

            try {
                return method.invoke(target, args);
            } finally {
                long duration = System.currentTimeMillis() - startTime;
                System.out.println(method.getName() + " finished, duration: " + duration + " ms");
            }
        }

        return method.invoke(target, args);
    }
}
