package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.MediaFile;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.RawTemplateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface RawTemplateService {

  RawTemplate getRawTemplate(Integer id, UUID accountId);

  RawTemplate createRawTemplate(UUID accountId, RawTemplateRequest request) throws Exception;

  RawTemplate updateRawTemplate(UUID accountId, Integer id, RawTemplateRequest request);

  RawTemplate updateContent(UUID accountId, Integer id, String content) throws Exception;

  List<MediaFile> uploadFiles(UUID accountId, Integer rawId, MultipartFile[] files)
      throws Exception;

  void updateThumbnail(RawTemplate rawTemplate) throws Exception;

  void deleteRawTemplate(UUID accountId, Integer id) throws Exception;
}
