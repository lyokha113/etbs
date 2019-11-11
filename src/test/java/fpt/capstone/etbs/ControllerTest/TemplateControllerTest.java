package fpt.capstone.etbs.ControllerTest;

import static fpt.capstone.etbs.ControllerTest.AccountControllerTest.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fpt.capstone.etbs.constant.IntegrationTest;
import fpt.capstone.etbs.controller.TemplateController;
import fpt.capstone.etbs.payload.TemplateCreateRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-test.properties")
public class TemplateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TemplateController templateController;

  private static final String description = "Test template description";
  private static final String name = "Test template name 123";
  private static final String token = IntegrationTest.TOKEN_TEST;

  @Test
  public void createTemplate() throws Exception {
    List<Integer> categories = new ArrayList<>();
    categories.add(1);
    categories.add(2);
    categories.add(3);
    TemplateCreateRequest request = TemplateCreateRequest
        .builder()
        .rawTemplateId(1)
        .categoryIds(categories)
        .description(description)
        .name(name)
//        .thumbnail(file)
        .build();
    mockMvc.perform(post("/template")
        .content(asJsonString(request))
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").isBoolean())
        .andExpect(jsonPath("$.message").value("Template created"))
        .andExpect(jsonPath("$.data.id").isNumber())
        .andExpect(jsonPath("$.data.name").value(name))
        .andExpect(jsonPath("$.data.authorName").isString())
        .andExpect(jsonPath("$.data.authorId").isString())
        .andExpect(jsonPath("$.data.thumbnail").isString())
        .andExpect(jsonPath("$.data.upVote").isNumber())
        .andExpect(jsonPath("$.data.downVote").isNumber())
        .andExpect(jsonPath("$.data.active").isBoolean())
        .andExpect(jsonPath("$.data.description").value(description))
        .andExpect(jsonPath("$.data.categories").hasJsonPath());
  }
}
