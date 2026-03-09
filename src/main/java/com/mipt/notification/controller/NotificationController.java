package com.mipt.notification.controller;

import com.mipt.notification.model.NotificationRequest;
import com.mipt.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        log.info("Received notification request: {}", request);

        switch (request.getType()) {
            // Существующие типы
            case "FILE_DELETED":
                notificationService.notifyFileDeleted(
                        request.getUserEmail(),
                        request.getUserName(),
                        request.getFileName(),
                        request.getUserId()
                );
                break;

            case "STORAGE_ALMOST_FULL":
                notificationService.notifyStorageAlmostFull(
                        request.getUserEmail(),
                        request.getUserName(),
                        request.getUsedStorage(),
                        request.getStorageLimit(),
                        request.getUserId()
                );
                break;

            case "STORAGE_FULL":
                notificationService.notifyStorageFull(
                        request.getUserEmail(),
                        request.getUserName(),
                        request.getUserId()
                );
                break;

            // ✅ НОВЫЕ ТИПЫ ДЛЯ ТАРИФОВ
            case "TARIFF_PURCHASED":
                notificationService.notifyTariffPurchased(
                        request.getUserEmail(),
                        request.getUserName(),
                        request.getTariffName(),
                        LocalDateTime.parse(request.getEndDate())
                );
                break;

            case "TARIFF_ENDING_SOON":
                notificationService.notifyTariffEndingSoon(
                        request.getUserEmail(),
                        request.getUserName(),
                        request.getDaysLeft(),
                        LocalDateTime.parse(request.getEndDate())
                );
                break;

            case "TARIFF_EXPIRED":
                notificationService.notifyTariffExpired(
                        request.getUserEmail(),
                        request.getUserName()
                );
                break;

            case "TARIFF_RENEWED":
                notificationService.notifyTariffRenewed(
                        request.getUserEmail(),
                        request.getUserName(),
                        LocalDateTime.parse(request.getEndDate())
                );
                break;

            default:
                return ResponseEntity.badRequest().body("Unknown type: " + request.getType());
        }

        return ResponseEntity.ok("Notification sent");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}