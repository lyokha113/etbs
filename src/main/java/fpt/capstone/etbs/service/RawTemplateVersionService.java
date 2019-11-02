package fpt.capstone.etbs.service;

import fpt.capstone.etbs.model.RawTemplateVersion;
import fpt.capstone.etbs.model.Template;

public interface RawTemplateVersionService {

  RawTemplateVersion createRawTemplateVersion(Template template, String name,
      String json_content);
}
