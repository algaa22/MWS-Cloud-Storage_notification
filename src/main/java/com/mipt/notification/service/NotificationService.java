package com.mipt.notification.service;


import com.mipt.notification.config.NotificationConfig;
import com.mipt.notification.utils.FormatUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private final NotificationConfig notificationConfig;

    public void notifyFileDeleted(String userEmail, String userName, String fileName, UUID userId) {
        String subject = notificationConfig.getSubjects().getFileDeleted();

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("fileName", fileName);
        context.setVariable("websiteUrl", notificationConfig.getUrls().getWebsite());
        context.setVariable("telegramSupportUrl", notificationConfig.getUrls().getTelegramSupport());

        String htmlContent = templateEngine.process("file-deleted", context);
        sendNotification(userEmail, subject, htmlContent, userId, userName);
    }

    public void notifyStorageAlmostFull(
            String userEmail, String userName, long usedStorage, long storageLimit, UUID userId) {
        String subject = notificationConfig.getSubjects().getStorageAlmostFull();

        double percentUsed = (usedStorage * 100.0) / storageLimit;
        int percentInt = (int) Math.round(percentUsed);
        String percentFormatted = String.format("%.1f%%", percentUsed);
        String usedFormatted = FormatUtils.formatBytes(usedStorage);
        String limitFormatted = FormatUtils.formatBytes(storageLimit);

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("usedFormatted", usedFormatted);
        context.setVariable("limitFormatted", limitFormatted);
        context.setVariable("percentInt", percentInt);
        context.setVariable("percentFormatted", percentFormatted);
        context.setVariable("websiteUrl", notificationConfig.getUrls().getWebsite());
        context.setVariable("telegramSupportUrl", notificationConfig.getUrls().getTelegramSupport());

        String htmlContent = templateEngine.process("storage-almost-full", context);
        sendNotification(userEmail, subject, htmlContent, userId, userName);
    }

    public void notifyStorageFull(String userEmail, String userName, UUID userId) {
        String subject = notificationConfig.getSubjects().getStorageFull();

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("websiteUrl", notificationConfig.getUrls().getWebsite());
        context.setVariable("telegramSupportUrl", notificationConfig.getUrls().getTelegramSupport());

        String htmlContent = templateEngine.process("storage-full", context);
        sendNotification(userEmail, subject, htmlContent, userId, userName);
    }

    public void notifyTariffPurchased(String email, String name, String tariffName, LocalDateTime endDate) {
        String subject = "🎉 Спасибо за покупку!";

        Context context = new Context();
        context.setVariable("userName", name);
        context.setVariable("tariffName", tariffName);
        context.setVariable("endDate", endDate);

        String htmlContent = templateEngine.process("tariff-purchased", context);
        emailService.sendHtmlEmail(email, subject, htmlContent);

        log.info("Tariff purchase notification sent to: {}", email);
    }

    public void notifyTariffEndingSoon(String email, String name, int daysLeft, LocalDateTime endDate) {
        String subject = getSubjectForDaysLeft(daysLeft);

        Context context = new Context();
        context.setVariable("userName", name);
        context.setVariable("daysLeft", daysLeft);
        context.setVariable("endDate", endDate);

        String htmlContent = templateEngine.process("tariff-ending-soon", context);
        emailService.sendHtmlEmail(email, subject, htmlContent);

        log.info("Tariff ending soon notification sent to: {} ({} days)", email, daysLeft);
    }

    public void notifyTariffExpired(String email, String name) {
        String subject = "❌ Ваш тариф истек";

        Context context = new Context();
        context.setVariable("userName", name);

        String htmlContent = templateEngine.process("tariff-expired", context);
        emailService.sendHtmlEmail(email, subject, htmlContent);

        log.info("Tariff expired notification sent to: {}", email);
    }

    private String getSubjectForDaysLeft(int days) {
        switch (days) {
            case 7: return "⏰ Ваш тариф заканчивается через 7 дней";
            case 3: return "⚠️ Осталось 3 дня действия тарифа";
            case 1: return "❗ Сегодня последний день действия тарифа";
            default: return "Напоминание о тарифе";
        }
    }

    public void notifyTariffRenewed(String email, String name, LocalDateTime newEndDate) {
        String subject = "🔄 Ваш тариф автоматически продлен";

        Context context = new Context();
        context.setVariable("userName", name);
        context.setVariable("newEndDate", newEndDate);

        String htmlContent = templateEngine.process("tariff-renewed", context);
        emailService.sendHtmlEmail(email, subject, htmlContent);

        log.info("Tariff renewed notification sent to: {}", email);
    }

private void sendNotification(
            String userEmail, String subject, String htmlContent, UUID userId, String userName) {
        emailService.sendHtmlEmail(userEmail, subject, htmlContent);
        log.info("HTML Notification sent to {}: {}", userEmail, subject);
    }
}
