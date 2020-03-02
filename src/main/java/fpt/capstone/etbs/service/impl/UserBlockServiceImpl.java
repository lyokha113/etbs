package fpt.capstone.etbs.service.impl;

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
import fpt.capstone.etbs.service.UserBlockService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

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
  private SimpMessageSendingOperations messagingTemplate;


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
        .content("").account(account)
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
  public void synchronizeContent(UUID accountId, SynchronizeContentRequest request)
      throws Exception {
    UserBlock userBlock = userBlockRepository.getByAccount_IdAndId(accountId, request.getBlockId())
        .orElse(null);
    if (userBlock == null) {
      throw new BadRequestException("Block doesn't exist");
    }

    List<RawTemplate> rawTemplates = rawTemplateRepository
        .getByWorkspace_Account_IdAndIdIn(accountId, request.getRawIds());

    List<RawTemplate> synchronizedRaws = new ArrayList<>();

    for (RawTemplate rt : rawTemplates) {
      String content = htmlContentService.setSynchronizeContent(userBlock, rt.getContent());
      if (content != null) {
        rt.setContent(content);
        synchronizedRaws.add(rt);
      }
    }

    synchronizedRaws = rawTemplateRepository.saveAll(synchronizedRaws);
    for (RawTemplate rt : synchronizedRaws) {
      messagingTemplate.convertAndSend(AppConstant.WEB_SOCKET_RAW + "/" + rt.getId(), rt.getContent());
      rawTemplateService.updateThumbnail(rt);
    }
  }

  private boolean isDuplicateName(String name, UUID accountId, Integer id) {
    return userBlockRepository.getByNameAndAccount_IdAndIdNot(name, accountId, id).isPresent();
  }


}
