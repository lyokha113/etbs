package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.UserBlock;
import fpt.capstone.etbs.payload.DynamicDataAttrs;
import fpt.capstone.etbs.service.HtmlContentService;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class HtmlContentServiceImpl implements HtmlContentService {

  @Override
  public String setSynchronizeContent(UserBlock block, String content) {
    Document doc = Jsoup.parse(content, "UTF-8");
    String cssQuery = "[datatype=user block][name=" + block.getName() + "]";
    Elements ele = doc.select(cssQuery);

    if (ele.isEmpty()) {
      return null;
    }

    ele.empty();
    ele.append(block.getContent());
    return doc.outerHtml();
  }

  @Override
  public String setDynamicData(List<DynamicDataAttrs> attrs, String content) {

    if (attrs.isEmpty()) {
      return content;
    }

    Document doc = Jsoup.parse(content, "UTF-8");
    attrs.forEach(attr -> {
      String cssQuery =
          "[datatype=" + attr.getDatatype() + "]"
              + "[name=" + attr.getName() + "]";
      Element ele = doc.select(cssQuery).first();
      if (ele != null) {
        String value = attr.getValue();
        if (attr.getDatatype().equalsIgnoreCase("dynamic text")) {
          ele.text(value);
        } else if (attr.getDatatype().equalsIgnoreCase("dynamic link")) {
          ele.attr("href", value);
        }
      }
    });
    return doc.outerHtml();
  }

  @Override
  public String setConfirmToken(String uri, String token, String content) {
    Document doc = Jsoup.parse(content, "UTF-8");
    String cssQuery = "#token";
    Element ele = doc.select(cssQuery).first();
    ele.attr("href", uri + "?token=" + token);
    return doc.outerHtml();
  }

  @Override
  public String setRecoveryPassword(String password, String content) {
    Document doc = Jsoup.parse(content, "UTF-8");
    String cssQuery = "#password";
    Element ele = doc.select(cssQuery).first();
    ele.text(password);
    return doc.outerHtml();
  }
}
