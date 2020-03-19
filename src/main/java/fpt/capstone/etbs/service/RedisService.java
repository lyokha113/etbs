package fpt.capstone.etbs.service;

import java.util.Set;

public interface RedisService {

  boolean checkLoginToken(String accountId, String token);

  void setLoginToken(String accountId, String token);

  void setOnlineSession(String key, String accountId);

  void setOfflineSession(String key, String accountId);

  Set<Object> getOnlineSessions(String key);

}
