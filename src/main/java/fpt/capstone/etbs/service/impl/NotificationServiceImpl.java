package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.constant.NotificationCode;
import fpt.capstone.etbs.constant.NotificationStatus;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Notification;
import fpt.capstone.etbs.payload.NotificationResponse;
import fpt.capstone.etbs.repository.NotificationRepository;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.MessagePublisherService;
import fpt.capstone.etbs.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private MessagePublisherService messagePublisherService;

  @Override
  public List<Notification> getNotifications(UUID accountId) {
    return notificationRepository.getByAccount_IdOrderByCreatedDateDesc(accountId);
  }

  @Override
  public List<Notification> getUnloadNotifications(UUID accountId) {
    return notificationRepository.getByAccount_IdAndLoadedFalseOrderByCreatedDateDesc(accountId);
  }

  @Override
  public List<Notification> getNotificationsToRemove() {
    LocalDateTime limit = LocalDateTime.now().minusDays(3);
    return notificationRepository.getByLoadedTrueAndCreatedDateBefore(limit);
  }

  @Override
  @Async("notificationAsyncExecutor")
  public void createPublishNotification() {
    List<Account> accounts = accountService.getAdminAccounts();
    List<Notification> notifications = accounts.stream().map(account ->
        Notification.builder()
            .title("Publish Request")
            .content("There is new request to publish template")
            .status(NotificationStatus.PRIMARY)
            .code(NotificationCode.REQUEST_PUBLISH.toString())
            .account(account)
            .build())
        .collect(Collectors.toList());
    notifications = notificationRepository.saveAll(notifications);
    messagePublisherService.sendNotifications(notifications);
  }

  @Override
  @Async("notificationAsyncExecutor")
  public void createApproveNotification(Account receiver, Integer templateId) {
    Notification notification = Notification.builder()
        .title("Request Approved")
        .content("Your publish request was approved")
        .status(NotificationStatus.SUCCESS)
        .code(NotificationCode.APPROVE_PUBLISH + " " + templateId)
        .account(receiver)
        .build();
    notificationRepository.save(notification);
    messagePublisherService.sendNotification(receiver.getId().toString(),
        NotificationResponse.setResponse(notification));
  }

  @Override
  @Async("notificationAsyncExecutor")
  public void createDenyNotification(Account receiver) {
    Notification notification = Notification.builder()
        .title("Request Denied")
        .content("Your publish request was denied")
        .status(NotificationStatus.DANGER)
        .code(NotificationCode.DENY_PUBLISH.toString())
        .account(receiver)
        .build();
    notificationRepository.save(notification);
    messagePublisherService.sendNotification(receiver.getId().toString(),
        NotificationResponse.setResponse(notification));
  }

  @Override
  @Async("notificationAsyncExecutor")
  public void createEmailNotification(Account receiver) {
    Notification notification = Notification.builder()
        .title("Confirmation")
        .content("Your email was confirmation")
        .status(NotificationStatus.SUCCESS)
        .code(NotificationCode.USER_EMAIL.toString())
        .account(receiver)
        .build();
    notificationRepository.save(notification);
    messagePublisherService.sendNotification(receiver.getId().toString(),
        NotificationResponse.setResponse(notification));
  }

  @Override
  @Async("notificationAsyncExecutor")
  public void createInvitationNotification(Account receiver) {
    Notification notification = Notification.builder()
        .title("Invitation")
        .content("You got an invitation for joining to design a template")
        .status(NotificationStatus.PRIMARY)
        .code(NotificationCode.INVITE_SESSION.toString())
        .account(receiver)
        .build();
    notificationRepository.save(notification);
    messagePublisherService.sendNotification(receiver.getId().toString(),
        NotificationResponse.setResponse(notification));
  }

  @Override
  @Async("notificationAsyncExecutor")
  public void createKickNotification(Account receiver) {
    Notification notification = Notification.builder()
        .title("Ask to leave")
        .content("Design owner has changed their mind and want you to leave it")
        .status(NotificationStatus.WARNING)
        .code(NotificationCode.KICK_SESSION.toString())
        .account(receiver)
        .build();
    notificationRepository.save(notification);
    messagePublisherService.sendNotification(receiver.getId().toString(),
        NotificationResponse.setResponse(notification));
  }

  @Override
  @Async("notificationAsyncExecutor")
  public void createLeaveNotification(Account receiver) {
    Notification notification = Notification.builder()
        .title("Contributor leave")
        .content("A contributor who invited don't want to join with you")
        .status(NotificationStatus.WARNING)
        .code(NotificationCode.LEAVE_SESSION.toString())
        .account(receiver)
        .build();
    notificationRepository.save(notification);
    messagePublisherService.sendNotification(receiver.getId().toString(),
        NotificationResponse.setResponse(notification));
  }


  @Override
  public void loadNotification(Integer id) {
    Notification notification = notificationRepository.findById(id).orElse(null);
    if (notification != null) {
      notification.setLoaded(true);
      notificationRepository.save(notification);
    }
  }
}
