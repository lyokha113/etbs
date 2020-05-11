package fpt.capstone.etbs.service.impl;

import static fpt.capstone.etbs.constant.AppConstant.CONTENT_TO_CHECK_DUPLICATE_CACHE;
import static fpt.capstone.etbs.constant.AppConstant.LOGIN_CACHE;

import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.repository.TemplateRepository;
import fpt.capstone.etbs.service.HtmlContentService;
import fpt.capstone.etbs.service.RedisService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private TemplateRepository templateRepository;

  @Autowired
  private HtmlContentService htmlContentService;

  @Override
  public boolean checkLoginToken(String accountId, String token) {
    String currentToken = (String) redisTemplate.opsForHash().get(LOGIN_CACHE, accountId);
    return currentToken != null && currentToken.equals(token);
  }

  @Override
  public void setLoginToken(String accountId, String token) {
    redisTemplate.opsForHash().put(LOGIN_CACHE, accountId, token);
  }

  @Override
  public void setOnlineSession(String rawId, String accountId) {
    redisTemplate.opsForSet().add(rawId, accountId);
    redisTemplate.expire(rawId, 1, TimeUnit.HOURS);
  }

  @Override
  public void setOfflineSession(String rawId, String accountId) {
    redisTemplate.opsForSet().remove(rawId, accountId);
  }

  @Override
  public void setContentToCheckDuplicate(String templateId, String contentId) {
    redisTemplate.opsForHash().put(CONTENT_TO_CHECK_DUPLICATE_CACHE, templateId, contentId);
  }

  @Override
  public void initContentToCheckDuplicate() {
    if (redisTemplate.opsForHash().size(CONTENT_TO_CHECK_DUPLICATE_CACHE) == 0) {
      List<Template> templates = templateRepository.findAll();
      Map<String, String> map = new HashMap<>();
      templates.forEach(t -> {
        String content = htmlContentService.removeAllText(t.getContent());
        map.put(String.valueOf(t.getId()), content);
      });
      redisTemplate.opsForHash().putAll(CONTENT_TO_CHECK_DUPLICATE_CACHE, map);
    }
  }

  @Override
  public Set<Object> getOnlineSessions(String rawId) {
    return redisTemplate.opsForSet().members(rawId);
  }

  @Override
  public Map<Object, Object> getContentToCheckDuplicate() {
    return redisTemplate.opsForHash().entries(CONTENT_TO_CHECK_DUPLICATE_CACHE);
  }
}
