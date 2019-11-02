package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.RawTemplateVersion;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.repository.RawTemplateVersionRepository;
import fpt.capstone.etbs.service.RawTemplateVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RawTemplateVersionServiceImpl implements RawTemplateVersionService {

  @Autowired
  RawTemplateVersionRepository rawTemplateVersionRepository;

  @Override
  public RawTemplateVersion createRawTemplateVersion(Template template, String name,
      String json_content) {
    return rawTemplateVersionRepository.save(RawTemplateVersion
        .builder()
        .jsonContent(json_content)
        .name(name)
        .build());
  }
}
