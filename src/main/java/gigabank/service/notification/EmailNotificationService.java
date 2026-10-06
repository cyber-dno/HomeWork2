package gigabank.service.notification;

import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService{
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("Отправка Email на " + recipient + ": " + message);
    }
}
