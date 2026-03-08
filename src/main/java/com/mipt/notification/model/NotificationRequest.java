package com.mipt.notification.model;

import lombok.Data;
import java.util.UUID;

@Data
public class NotificationRequest {
  private String type;
  private String userEmail;
  private String userName;
  private String fileName;
  private Long usedStorage;
  private Long storageLimit;
  private UUID userId;
}
