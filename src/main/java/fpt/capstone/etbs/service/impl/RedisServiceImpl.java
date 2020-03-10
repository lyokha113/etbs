package fpt.capstone.etbs.service.impl;

import static fpt.capstone.etbs.constant.AppConstant.CURRENT_ONLINE_CACHE;

import fpt.capstone.etbs.service.RedisService;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Override
  public void setOnlineSession(String key, String accountId) {
    redisTemplate.opsForSet().add(key, accountId);
    redisTemplate.expire(key, 1, TimeUnit.HOURS);
  }

  @Override
  public void setOfflineSession(String key, String accountId) {
    redisTemplate.opsForSet().remove(key, accountId);
  }

  @Override
  public Set<Object> getOnlineSessions(String key) {
    return redisTemplate.opsForSet().members(key);
  }
}
