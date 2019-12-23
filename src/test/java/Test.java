import org.apache.commons.text.similarity.JaroWinklerSimilarity;

public class Test {

  public static void main(String[] args) throws Exception {

    String src1 =
        "        <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-email-paddings\" valign=\"top\">"
            + "<table class=\"es-content es-preheader esd-header-popover\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-adaptive esd-stripe\" style=\"background-color: rgb(247, 247, 247);\" esd-custom-block-id=\"8428\" bgcolor=\"#f7f7f7\" align=\"center\">"
            + "<table class=\"es-content-body\" style=\"background-color: transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-structure es-p10\" align=\"left\">"
            + "<!--[if mso]><table width=\"580\"><tr><td width=\"280\" valign=\"top\"><![endif]-->"
            + "<table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame\" width=\"280\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-infoblock esd-block-text es-m-txt-c\" align=\"left\">"
            + "<p>Put your preheader text here</p>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td><td width=\"20\"></td><td width=\"280\" valign=\"top\"><![endif]-->"
            + "<table class=\"es-right\" cellspacing=\"0\" cellpadding=\"0\" align=\"right\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame\" width=\"280\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-infoblock esd-block-text es-m-txt-c\" align=\"right\">"
            + "<p><a href=\"https://viewstripo.email/\" target=\"_blank\">View in browser</a></p>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td></tr></table><![endif]-->"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<table class=\"es-header\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-adaptive esd-stripe\" esd-custom-block-id=\"8429\" align=\"center\">"
            + "<table class=\"es-header-body\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-structure es-p25t es-p20b es-p20r es-p20l\" align=\"left\">"
            + "<!--[if mso]><table width=\"560\" cellpadding=\"0\""
            + "cellspacing=\"0\"><tr><td width=\"205\" valign=\"top\"><![endif]-->"
            + "<table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-m-p0r es-m-p20b esd-container-frame\" width=\"205\" valign=\"top\" align=\"center\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-image es-m-p0l es-m-txt-c\" align=\"right\"><a href=\"https://viewstripo.email/\" target=\"_blank\"><img src=\"https://tlr.stripocdn.email/content/guids/CABINET_944049925f70694686e3cfdb5a5862f4/images/69781523356282391.png\" alt=\"Logo\" style=\"display: block;\" title=\"Logo\" width=\"88\"></a></td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td><td width=\"10\"></td><td width=\"345\" valign=\"top\"><![endif]-->"
            + "<table cellspacing=\"0\" cellpadding=\"0\" align=\"right\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame\" width=\"345\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-menu\" esd-img-prev-h=\"16\" esd-img-prev-w=\"16\">"
            + "<table class=\"es-menu\" width=\"70%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr class=\"links\">"
            + "<td class=\"es-p10t es-p10b es-p5r es-p5l \" style=\"padding-bottom: 0px; padding-top: 20px; \" width=\"33.33%\" bgcolor=\"transparent\" align=\"center\"><a target=\"_blank\" style=\"color: #333333;\" href=\"https://viewstripo.email/\">Classes</a></td>"
            + "<td class=\"es-p10t es-p10b es-p5r es-p5l \" style=\"padding-bottom: 0px; padding-top: 20px; \" width=\"33.33%\" bgcolor=\"transparent\" align=\"center\"><a target=\"_blank\" style=\"color: #333333;\" href=\"https://viewstripo.email/\">Workshops</a></td>"
            + "<td class=\"es-p10t es-p10b es-p5r es-p5l \" style=\"padding-bottom: 0px; padding-top: 20px; \" width=\"33.33%\" bgcolor=\"transparent\" align=\"center\"><a target=\"_blank\" style=\"color: #333333;\" href=\"https://viewstripo.email/\">Blog</a></td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td></tr></table><![endif]-->"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-stripe\" align=\"center\">"
            + "<table class=\"es-content-body\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-structure\" esd-custom-block-id=\"8430\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame\" width=\"600\" valign=\"top\" align=\"center\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-banner\" style=\"position: relative;\" align=\"center\" esdev-config=\"h7\"><a target=\"_blank\" href=\"http://#\"><img class=\"adapt-img esdev-stretch-width esdev-banner-rendered\" src=\"https://tlr.stripocdn.email/content/guids/bannerImgGuid/images/66791536320362728.png\" alt=\"Happy Thanksgiving! 40% off for all classes!\" title=\"Happy Thanksgiving! 40% off for all classes!\" style=\"display: block;\" width=\"100%\"></a></td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-text es-m-txt-c es-p20b es-p20r es-p20l\" align=\"center\">"
            + "<p style=\"font-size: 16px; color: #333333;\">We’re thankful for you, our users. Wishing you the happiness of good friends, the joy of a happy family, and the wonder of the holiday season.&nbsp; Have a memorable Thanksgiving!</p>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-stripe\" align=\"center\">"
            + "<table class=\"es-content-body\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-structure es-p5t es-p15b es-p20r es-p20l\" esd-custom-block-id=\"8433\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame\" width=\"560\" valign=\"top\" align=\"center\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-image\" align=\"center\"><a target=\"_blank\"><img src=\"https://tlr.stripocdn.email/content/guids/CABINET_fda017b5701cf3c9915c3e123c79e5eb/images/92241523451206218.png\" alt=\"icon\" style=\"display: block;\" title=\"icon\" width=\"30\"></a></td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-text\" align=\"center\">"
            + "<h3 style=\"color: #999999; font-family: lora, georgia, times new roman, serif; line-height: 150%;\"><em>5 tips for a</em></h3>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-text\" align=\"center\">"
            + "<h2>Stress-Free Thanksgiving</h2>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-structure esd-checked es-p20t es-p10b es-p20r es-p20l\" esd-custom-block-id=\"8439\" style=\"background-image:url(https://tlr.stripocdn.email/content/guids/CABINET_56fd46c8da4c1061e3aab874d2edac8d/images/831536320129906.png);background-position: left top; background-repeat: no-repeat;\" align=\"left\" esdev-config=\"h1\">"
            + "<!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"180\" valign=\"top\"><![endif]-->"
            + "<table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-m-p0r es-m-p20b esd-container-frame\" width=\"180\" valign=\"top\" align=\"center\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-image\" align=\"center\"><a target=\"_blank\"><img src=\"https://tlr.stripocdn.email/content/guids/CABINET_56fd46c8da4c1061e3aab874d2edac8d/images/55521536315492410.png\" alt=\"1. Make a menu\" style=\"display: block;\" title=\"1. Make a menu\" width=\"150\"></a></td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td><td width=\"20\"></td><td width=\"360\" valign=\"top\"><![endif]-->"
            + "<table cellspacing=\"0\" cellpadding=\"0\" align=\"right\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-m-p20b esd-container-frame\" width=\"360\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-text es-m-txt-c\" align=\"left\">"
            + "<h3>1. Make a menu</h3>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-text es-m-txt-c es-p10t\" align=\"left\">"
            + "<p class=\"product-description\">Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.</p>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td></tr></table><![endif]-->"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-structure esd-checked es-p20t es-p10b es-p20r es-p20l\" esd-custom-block-id=\"8438\" style=\"background-image:url(https://tlr.stripocdn.email/content/guids/CABINET_56fd46c8da4c1061e3aab874d2edac8d/images/98931536320277404.png);background-position: right top; background-repeat: no-repeat;\" align=\"left\" esdev-config=\"h2\">"
            + "<!--[if mso]><table dir=\"rtl\" width=\"560\" cellpadding=\"0\" "
            + "cellspacing=\"0\"><tr><td dir=\"ltr\" width=\"180\" valign=\"top\"><![endif]-->"
            + "<table cellspacing=\"0\" cellpadding=\"0\" align=\"right\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame es-m-p20b\" width=\"180\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-image\" align=\"center\"><a target=\"_blank\"><img src=\"https://tlr.stripocdn.email/content/guids/CABINET_56fd46c8da4c1061e3aab874d2edac8d/images/21611536318753025.png\" alt=\"2. Set the table ahead of time\" style=\"display: block;\" title=\"2. Set the table ahead of time\" width=\"150\"></a></td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td><td dir=\"ltr\" width=\"20\"></td><td dir=\"ltr\" width=\"360\" valign=\"top\"><![endif]-->"
            + "<table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame es-m-p20b\" width=\"360\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-text es-p10t\" align=\"left\">"
            + "<h3>2. Set the table ahead of time</h3>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-text es-m-txt-c es-p10t\" align=\"left\">"
            + "<p class=\"product-description\">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip.</p>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td></tr></table><![endif]-->"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-structure esd-checked es-p20t es-p10b es-p20r es-p20l\" esd-custom-block-id=\"8439\" style=\"background-image:url(https://tlr.stripocdn.email/content/guids/CABINET_56fd46c8da4c1061e3aab874d2edac8d/images/831536320129906.png);background-position: left top; background-repeat: no-repeat;\" align=\"left\" esdev-config=\"h3\">"
            + "<!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"180\" valign=\"top\"><![endif]-->"
            + "<table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-m-p0r es-m-p20b esd-container-frame\" width=\"180\" valign=\"top\" align=\"center\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-image\" align=\"center\"><a target=\"_blank\"><img src=\"https://tlr.stripocdn.email/content/guids/CABINET_56fd46c8da4c1061e3aab874d2edac8d/images/69931536318895778.png\" alt=\"3. Cook in advance as much as possible\" style=\"display: block;\" title=\"3. Cook in advance as much as possible\" width=\"150\"></a></td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td><td width=\"20\"></td><td width=\"360\" valign=\"top\"><![endif]-->"
            + "<table cellspacing=\"0\" cellpadding=\"0\" align=\"right\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-m-p20b esd-container-frame\" width=\"360\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-text es-m-txt-c\" align=\"left\">"
            + "<h3>3. Cook in advance as much as possible</h3>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-text es-m-txt-c es-p10t\" align=\"left\">"
            + "<p class=\"product-description\">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip.</p>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td></tr></table><![endif]-->"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-structure es-p20t es-p10b es-p20r es-p20l esd-checked\" esd-custom-block-id=\"8438\" style=\"background-image:url(https://tlr.stripocdn.email/content/guids/CABINET_56fd46c8da4c1061e3aab874d2edac8d/images/98931536320277404.png);background-position: right top; background-repeat: no-repeat;\" align=\"left\" esdev-config=\"h4\">"
            + "<!--[if mso]><table dir=\"rtl\" width=\"560\" cellpadding=\"0\" "
            + "cellspacing=\"0\"><tr><td dir=\"ltr\" width=\"180\" valign=\"top\"><![endif]-->"
            + "<table cellspacing=\"0\" cellpadding=\"0\" align=\"right\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame es-m-p20b\" width=\"180\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-image\" align=\"center\"><a target=\"_blank\"><img src=\"https://tlr.stripocdn.email/content/guids/CABINET_56fd46c8da4c1061e3aab874d2edac8d/images/67631536319326131.png\" alt=\"4. Have your favorite music playing while you are in the kitchen\" style=\"display: block;\" title=\"4. Have your favorite music playing while you are in the kitchen\" width=\"150\"></a></td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td><td dir=\"ltr\" width=\"20\"></td><td dir=\"ltr\" width=\"360\" valign=\"top\"><![endif]-->"
            + "<table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame es-m-p20b\" width=\"360\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-text\" align=\"left\">"
            + "<h3>4. Have your favorite music playing while you are in the kitchen</h3>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-text es-m-txt-c es-p10t\" align=\"left\">"
            + "<p class=\"product-description\">Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.</p>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td></tr></table><![endif]-->"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-structure esd-checked es-p20t es-p10b es-p20r es-p20l\" esd-custom-block-id=\"8439\" style=\"background-image:url(https://tlr.stripocdn.email/content/guids/CABINET_56fd46c8da4c1061e3aab874d2edac8d/images/831536320129906.png);background-position: left top; background-repeat: no-repeat;\" align=\"left\" esdev-config=\"h5\">"
            + "<!--[if mso]><table width=\"560\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"180\" valign=\"top\"><![endif]-->"
            + "<table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-m-p0r es-m-p20b esd-container-frame\" width=\"180\" valign=\"top\" align=\"center\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-image\" align=\"center\"><a target=\"_blank\"><img src=\"https://tlr.stripocdn.email/content/guids/CABINET_56fd46c8da4c1061e3aab874d2edac8d/images/64301536319786821.png\" alt=\"5. Allow guests to help with tidying\" style=\"display: block;\" title=\"5. Allow guests to help with tidying\" width=\"150\"></a></td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td><td width=\"20\"></td><td width=\"360\" valign=\"top\"><![endif]-->"
            + "<table cellspacing=\"0\" cellpadding=\"0\" align=\"right\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-m-p20b esd-container-frame\" width=\"360\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-text es-m-txt-c\" align=\"left\">"
            + "<h3>5. Allow guests to help with tidying</h3>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-text es-p10t es-p10b es-m-txt-c\" align=\"left\">"
            + "<p class=\"product-description\">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip.</p>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<!--[if mso]></td></tr></table><![endif]-->"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-structure\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame\" width=\"600\" valign=\"top\" align=\"center\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-spacer es-p20t es-p15b es-p20r es-p20l\" align=\"center\">"
            + "<table width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td style=\"border-bottom: 1px solid rgb(255, 255, 255); background: rgba(0, 0, 0, 0) none repeat scroll 0% 0%; height: 1px; width: 100%; margin: 0px;\"></td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">"
            + "<tbody>"
            + "<tr></tr>"
            + "<tr>"
            + "<td class=\"esd-stripe\" esd-custom-block-id=\"8442\" style=\"background-color: rgb(247, 247, 247);\" bgcolor=\"#f7f7f7\" align=\"center\">"
            + "<table class=\"es-footer-body\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-structure es-p20t es-p20b es-p20r es-p20l\" esd-general-paddings-checked=\"false\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame\" width=\"560\" valign=\"top\" align=\"center\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-text es-p5b\" align=\"center\">"
            + "<h3 style=\"line-height: 150%;\">Let's get social</h3>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-social es-p10t es-p10b\" align=\"center\">"
            + "<table class=\"es-table-not-adapt es-social\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"es-p20r\" valign=\"top\" align=\"center\"><a href=\"https://viewstripo.email/\"><img title=\"Facebook\" src=\"https://tlr.stripocdn.email/content/assets/img/social-icons/logo-black/facebook-logo-black.png\" alt=\"Fb\" width=\"32\" height=\"32\"></a></td>"
            + "<td class=\"es-p20r\" valign=\"top\" align=\"center\"><a href=\"https://viewstripo.email/\"><img title=\"Youtube\" src=\"https://tlr.stripocdn.email/content/assets/img/social-icons/logo-black/youtube-logo-black.png\" alt=\"Yt\" width=\"32\" height=\"32\"></a></td>"
            + "<td class=\"es-p20r\" valign=\"top\" align=\"center\"><a href=\"https://viewstripo.email/\"><img title=\"Pinterest\" src=\"https://tlr.stripocdn.email/content/assets/img/social-icons/logo-black/pinterest-logo-black.png\" alt=\"P\" width=\"32\" height=\"32\"></a></td>"
            + "<td class=\"es-p20r\" valign=\"top\" align=\"center\"><a href=\"https://viewstripo.email/\" target=\"_blank\"><img title=\"Instagram\" src=\"https://tlr.stripocdn.email/content/assets/img/social-icons/logo-black/instagram-logo-black.png\" alt=\"Ig\" width=\"32\" height=\"32\"></a></td>"
            + "<td valign=\"top\" align=\"center\"><a href=\"https://viewstripo.email/\" target=\"_blank\"><img title=\"Twitter\" src=\"https://tlr.stripocdn.email/content/assets/img/social-icons/logo-black/twitter-logo-black.png\" alt=\"Tw\" width=\"32\" height=\"32\"></a></td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-text es-p10t es-p10b\" align=\"center\">"
            + "<p style=\"line-height: 150%;\">You are receiving this email because you have visited our site or asked us about the regular newsletter. You can&nbsp;<a target=\"_blank\" style=\"line-height: 150%;\">unsubscribe here</a>&nbsp;or&nbsp;<a target=\"_blank\" style=\"line-height: 150%;\">update your subscription preferences</a>.</p>"
            + "</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"esd-block-text es-p10t es-p10b\" align=\"center\">"
            + "<p>© 2018<br></p>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "<table class=\"esd-footer-popover es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-stripe\" align=\"center\">"
            + "<table class=\"es-content-body\" style=\"background-color: transparent;\" width=\"600\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-structure es-p30t es-p30b es-p20r es-p20l\" align=\"left\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-container-frame\" width=\"560\" valign=\"top\" align=\"center\">"
            + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"esd-block-image es-infoblock made_with\" align=\"center\"><a target=\"_blank\" href=\"https://viewstripo.email/?utm_source=templates&utm_medium=email&utm_campaign=yoga&utm_content=thanksgiving\"><img src=\"https://tlr.stripocdn.email/content/guids/CABINET_9df86e5b6c53dd0319931e2447ed854b/images/64951510234941531.png\" alt width=\"125\"></a></td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>"
            + "</td>"
            + "</tr>"
            + "</tbody>"
            + "</table>";
    String src2 = "<p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p><p>abcasd</p><p>1233333333333</p>";

    JaroWinklerSimilarity jws = new JaroWinklerSimilarity();
    Double duplicationRate = jws.apply(src1, src2);
    System.out.println(duplicationRate);

  }
}
