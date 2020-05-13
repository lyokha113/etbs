package fpt.capstone.etbs.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {

  boolean checkLoginToken(String accountId, String token);

  void setLoginToken(String accountId, String token);

  void setOnlineSession(String rawId, String accountId);

  void setOfflineSession(String rawId, String accountId);

  void setContentToCheckDuplicate(String templateId, String contentId);

  void removeTemplateToCheckDuplicate(String templateId);

  void initContentToCheckDuplicate();

  Set<Object> getOnlineSessions(String rawId);

  Map<Object, Object> getContentToCheckDuplicate();

}
