package com.base.auth.dto.notification;

import java.time.LocalDate;
import lombok.Data;

@Data
public class NotificationDto {
  private Long id;
  private Long refId;
  private String title;
  private String message;
  private Boolean readFlag;
  private LocalDate createdDate;
}
