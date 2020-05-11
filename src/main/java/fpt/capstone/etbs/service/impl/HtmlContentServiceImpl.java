package fpt.capstone.etbs.service.impl;

import fpt.capstone.etbs.model.UserBlock;
import fpt.capstone.etbs.payload.DynamicDataAttrs;
import fpt.capstone.etbs.service.HtmlContentService;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class HtmlContentServiceImpl implements HtmlContentService {

  @Override
  public String setSynchronizeContent(UserBlock block, String content) {
    Document doc = Jsoup.parse(content, "UTF-8");
    String cssQuery = "[datatype=user block][name=" + block.getId() + "]";
    Elements elements = doc.select(cssQuery);

    if (elements.isEmpty()) {
      return null;
    }

    elements.empty();
    elements.append(block.getContent());
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
      Elements elements = doc.select(cssQuery);
        String value = attr.getValue();
        if (attr.getDatatype().equalsIgnoreCase("dynamic text")) {
          elements.empty();
          elements.append(value);
        } else if (attr.getDatatype().equalsIgnoreCase("dynamic link")) {
          elements.attr("href", value);
        } else if (attr.getDatatype().equalsIgnoreCase("dynamic image")) {
          elements.attr("src", value);
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

  @Override
  public String removeAllText(String content) {
    Document doc = Jsoup.parse(content);
    Elements elements = doc.getAllElements();
    elements.forEach(e -> {
      List<TextNode> textNodes = e.textNodes();
      textNodes.forEach(tn -> tn.text(""));
    });
    return doc.outerHtml();
  }

  @Override
  public String getOnlyText(String content) {
    Document doc = Jsoup.parse(content);
    return doc.text();
  }
}
