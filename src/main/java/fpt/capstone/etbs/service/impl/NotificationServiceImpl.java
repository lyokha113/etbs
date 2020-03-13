package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Notification;
import fpt.capstone.etbs.payload.NotificationRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.NotificationRepository;
import fpt.capstone.etbs.service.MessagePublisherService;
import fpt.capstone.etbs.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private MessagePublisherService messagePublisherService;

  @Override
  public Slice<Notification> getNotifications(UUID accountId, int currentPage) {
    Pageable pageRequest = PageRequest.of(currentPage, 5);
    return notificationRepository
        .getByAccount_IdOrderByCreatedDateDesc(accountId, pageRequest);
  }

  @Override
  public List<Notification> getLoadedNotifications() {
    LocalDateTime limit = LocalDateTime.now().minusDays(7);
    return notificationRepository.getByLoadedTrueAndCreatedDateBefore(limit);
  }

  @Override
  public void createNotification(Account account, NotificationRequest request) {
   Notification notification = Notification.builder()
        .content(request.getContent())
        .icon(request.getIcon())
        .status(request.getStatus())
        .code(request.getCode())
        .build();
    notification = notificationRepository.save(notification);
    messagePublisherService.sendNotification(account.getId().toString(), notification);
  }

  @Override
  public void loadNotification(Integer [] ids) {
    List<Notification> notifications = notificationRepository.getByIdIn(ids);
    notifications.forEach(n -> n.setLoaded(true));
    notificationRepository.saveAll(notifications);
  }
}
