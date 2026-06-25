package com.base.auth.repository;

import com.base.auth.model.Notification;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationRepository extends JpaRepository<Notification, Long>,
    JpaSpecificationExecutor<Notification> {
  @Transactional
  void deleteAllByReceiverIdAndReadFlag(Long receiverId, Boolean readFlag);
}
