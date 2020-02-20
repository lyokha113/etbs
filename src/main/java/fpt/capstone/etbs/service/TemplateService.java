package fpt.capstone.etbs.service;

import fpt.capstone.etbs.constant.AppConstant;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public interface TemplateService {

  static double calculateScore(int up, int down, LocalDateTime createdDate) {
    int sign = up - down;
    BigDecimal voteScore = BigDecimal.valueOf(Math.log10(Math.max(Math.abs(sign), 1)));
    sign = Integer.compare(sign, 0);

    long pivot = LocalDateTime.parse("2020-01-01T00:00:00").toEpochSecond(ZoneOffset.UTC);
    long timeElapsed = createdDate.toEpochSecond(ZoneOffset.UTC) - pivot;
    BigDecimal timeScore = new BigDecimal(sign * timeElapsed / AppConstant.TIME_TO_SCORE);

    return voteScore.add(timeScore).setScale(7, BigDecimal.ROUND_UNNECESSARY).doubleValue();

  }

  List<Template> getTemplates();

  List<Template> getTemplatesForUser();

  Template getTemplate(Integer id);

  Template createTemplate(TemplateRequest request);

  Template updateTemplate(Integer id, TemplateRequest request) throws Exception;

  Template updateThumbnail(Template template) throws Exception;

  Template updateContentImage(Template template) throws Exception;

  void deleteTemplate(Integer id) throws Exception;

}
