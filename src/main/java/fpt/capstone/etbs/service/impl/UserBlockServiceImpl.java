package fpt.capstone.etbs.service.impl;

import static fpt.capstone.etbs.constant.AppConstant.CURRENT_ONLINE_CACHE;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.exception.BadRequestException;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.UserBlock;
import fpt.capstone.etbs.payload.SynchronizeContentRequest;
import fpt.capstone.etbs.payload.UserBlockRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RawTemplateRepository;
import fpt.capstone.etbs.repository.UserBlockRepository;
import fpt.capstone.etbs.service.HtmlContentService;
import fpt.capstone.etbs.service.RawTemplateService;
import fpt.capstone.etbs.service.RedisService;
import fpt.capstone.etbs.service.UserBlockService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserBlockServiceImpl implements UserBlockService {

  @Autowired
  private UserBlockRepository userBlockRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private RawTemplateRepository rawTemplateRepository;

  @Autowired
  private RawTemplateService rawTemplateService;

  @Autowired
  private HtmlContentService htmlContentService;

  @Autowired
  private RedisService redisService;

  @Override
  public List<UserBlock> getUserBlocks(UUID accountId) {
    return userBlockRepository.getByAccount_Id(accountId);
  }

  @Override
  public UserBlock getUserBlock(UUID accountId, Integer id) {
    return userBlockRepository.getByAccount_IdAndId(accountId, id).orElse(null);
  }

  @Override
  public UserBlock createUserBlock(UUID accountId, UserBlockRequest request) {

    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new BadRequestException("Account doesn't exist");
    }

    UserBlock userBlock = userBlockRepository.getByNameAndAccount_Id(request.getName(), accountId)
        .orElse(null);
    if (userBlock != null) {
      throw new BadRequestException("Block name is existed");
    }

    userBlock = UserBlock.builder()
        .name(request.getName()).icon(request.getIcon())
        .content(AppConstant.BLANK_CONTENT).account(account)
        .build();

    return userBlockRepository.save(userBlock);
  }

  @Override
  public UserBlock updateUserBlock(UUID accountId, Integer id, UserBlockRequest request) {

    UserBlock userBlock = userBlockRepository.getByAccount_IdAndId(accountId, id).orElse(null);
    if (userBlock == null) {
      throw new BadRequestException("Block doesn't exist");
    }

    if (isDuplicateName(request.getName(), accountId, id)) {
      throw new BadRequestException("Block name is existed");
    }

    userBlock.setName(request.getName());
    userBlock.setIcon(request.getIcon());
    return userBlockRepository.save(userBlock);
  }

  @Override
  public UserBlock updateUserBlockContent(UUID accountId, Integer id, String content) {
    UserBlock userBlock = userBlockRepository.getByAccount_IdAndId(accountId, id).orElse(null);
    if (userBlock == null) {
      throw new BadRequestException("Block doesn't exist");
    }

    if (StringUtils.isEmpty(content)) {
      throw new BadRequestException("Content is empty");
    }

    userBlock.setContent(content);
    return userBlockRepository.save(userBlock);
  }

  @Override
  public void deleteUserBlock(UUID accountId, Integer id) {
    UserBlock userBlock = userBlockRepository.getByAccount_IdAndId(accountId, id).orElse(null);
    if (userBlock == null) {
      throw new BadRequestException("Block doesn't exist");
    }
    userBlockRepository.delete(userBlock);
  }

  @Override
  public Map<String, List<Integer>> synchronizeContent(UUID accountId,
      SynchronizeContentRequest request)
      throws Exception {
    UserBlock userBlock = userBlockRepository.getByAccount_IdAndId(accountId, request.getBlockId())
        .orElse(null);
    if (userBlock == null) {
      throw new BadRequestException("Block doesn't exist");
    }

    List<RawTemplate> rawTemplates = rawTemplateRepository
        .getByWorkspace_Account_IdAndIdIn(accountId, request.getRawIds());
    List<RawTemplate> syncs = new ArrayList<>();
    List<RawTemplate> errors = new ArrayList<>();
    for (RawTemplate rt : rawTemplates) {
      String content = htmlContentService.setSynchronizeContent(userBlock, rt.getContent());
      if (content != null) {
        String currentOnlineKey = CURRENT_ONLINE_CACHE + rt.getId();
        Set<Object> onlineSessions = redisService.getOnlineSessions(currentOnlineKey);
        if (onlineSessions != null && onlineSessions.size() > 0) {
          errors.add(rt);
        } else {
          rt.setContent(content);
          syncs.add(rt);
        }

      }
    }
    syncs = rawTemplateRepository.saveAll(syncs);

    for (RawTemplate rt : syncs) {
      rawTemplateService.updateThumbnail(rt);
    }

    Map<String, List<Integer>> result = new HashMap<>();
    result.put("syncs", syncs.stream().map(RawTemplate::getId).collect(Collectors.toList()));
    result.put("errors", errors.stream().map(RawTemplate::getId).collect(Collectors.toList()));
    return result;

  }

  private boolean isDuplicateName(String name, UUID accountId, Integer id) {
    return userBlockRepository.getByNameAndAccount_IdAndIdNot(name, accountId, id).isPresent();
  }


}
