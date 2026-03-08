package com.mipt.notification.controller;

import com.mipt.notification.model.NotificationRequest;
import com.mipt.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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