//import dto.UserRequest;
//import org.springframework.context.annotation.*;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.view.InternalResourceViewResolver;
//import service.BankManager;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
//@Configuration
//@ComponentScan(basePackages = {"controllers", "service"})
//@PropertySource("classpath:application.properties")
//public class Main{
//
//    public static void main( String[] args ) {
//        AnnotationConfigApplicationContext  context = new AnnotationConfigApplicationContext(Main.class);
//        BankManager bankManager = context.getBean(BankManager.class);
//
//        // Тестовые данные
//        List<UserRequest> requests = List.of(
//                new UserRequest(123, new BigDecimal("100.00"), "CARD",
//                        Map.of("cardNumber", "4111111111111111", "merchant", "Amazon")),
//                new UserRequest(123, new BigDecimal("200.00"), "BANK",
//                        Map.of("bankName", "Chase", "accountNumber", "12345678")),
//                new UserRequest(999, new BigDecimal("50.00"), "WALLET",  // Несуществующий аккаунт
//                        Map.of("walletId", "wallet123")),
//                new UserRequest(123, new BigDecimal("150.00"), "WALLET",
//                        Map.of("walletId", "wallet456"))
//        );
//
//        System.out.println("\n");
//        // Обработка запросов
//        bankManager.doWork(requests);
//
//        context.close();
//    }
//}
