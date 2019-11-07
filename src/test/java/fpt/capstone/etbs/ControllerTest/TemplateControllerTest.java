package fpt.capstone.etbs.ControllerTest;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Template;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import fpt.capstone.etbs.service.AccountService;
import fpt.capstone.etbs.service.TemplateService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.multipart.MultipartFile;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-test.properties")
public class TemplateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private AccountService accountService;

  @Test
  public void createTemplate() throws Exception {
    Account account = accountService.getAccounts().get(1);
    Path path = Paths.get("C:\\Users\\ThaiT\\Pictures\\Saved Pictures\\2.png");
    String name = "2.png";
    String originalFileName = "2.png";
    String contentType = "image/png";
    byte[] content = null;
    try {
      content = Files.readAllBytes(path);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    MultipartFile file = new MockMultipartFile(name,
        originalFileName, contentType, content);
    List<Integer> categories = new ArrayList<>();
    categories.add(1);
    categories.add(2);
    categories.add(3);
    TemplateCreateRequest request = TemplateCreateRequest
        .builder()
        .rawTemplateId(1)
        .categoryIds(categories)
        .description("Test template description")
        .name("Test template name")
        .thumbnail(file)
        .build();
    Template template = new Template();
    given(templateService.createTemplate(account.getId(), request)).willReturn(template);
    mockMvc.perform(post("http://localhost:5000/template")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect((ResultMatcher) jsonPath("$.name", is(request.getName())))
        .andExpect((ResultMatcher) jsonPath("$.description", is(request.getDescription())));
  }
}
