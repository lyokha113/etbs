package fpt.capstone.etbs.service;

import java.util.Set;

public interface RedisService {

  void setOnlineSession(String key, String accountId);

  void setOfflineSession(String key, String accountId);

  Set<Object> getOnlineSessions(String key);

}
