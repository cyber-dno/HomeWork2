package service.notification;

import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService implements NotificationService{
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("Отправка SMS на " + recipient + ": " + message);
    }
}
