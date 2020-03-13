package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.UserBlock;
import fpt.capstone.etbs.payload.DynamicDataAttrs;
import java.util.List;

public interface HtmlContentService {

  String setSynchronizeContent(UserBlock block, String content);

  String setDynamicData(List<DynamicDataAttrs> attrs, String content);

  String setConfirmToken(String uri, String token, String content);

  String setRecoveryPassword(String password, String content);

  String removeAllText(String content);

  String getOnlyText(String content);
}
