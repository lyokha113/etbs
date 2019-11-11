package fpt.capstone.etbs.ControllerTest;

import static fpt.capstone.etbs.ControllerTest.AccountControllerTest.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fpt.capstone.etbs.controller.RawTemplateVersionController;
import fpt.capstone.etbs.model.RawTemplateVersion;
import fpt.capstone.etbs.payload.RawTemplateVersionRequest;
import io.swagger.models.auth.In;
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
public class RawTemplateVersionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RawTemplateVersionController rawTemplateVersionController;

  private static final String name = "Test version of raw template 1";
  private static final Integer rawTemplateId = 2;
  private static final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOlwiOWIyYWM2MmMtODU5Zi00MjE1LTkyNTctMjA5MDE4NjMzMjM0XCIsXCJmdWxsTmFtZVwiOlwiVGhhaSBUZXN0XCIsXCJlbWFpbFwiOlwidGVzdGVtYWlsQGdtYWlsLmNvbVwiLFwiaW1hZ2VVcmxcIjpcImh0dHBzOi8vZmlyZWJhc2VzdG9yYWdlLmdvb2dsZWFwaXMuY29tL3YwL2IvZXRicy00NDFhMS5hcHBzcG90LmNvbS9vL2RlZmF1bHQlMkZkZWZhdWx0X2F2YXRhci5wbmc_YWx0PW1lZGlhJnRva2VuPWViYjJhZTA3LTc3YzEtNGRkMy1iNWIzLTk4NWY0ZmI5ZWI5OVwiLFwiYWN0aXZlXCI6dHJ1ZSxcInByb3ZpZGVyXCI6XCJsb2NhbFwiLFwicm9sZUlkXCI6MixcInJvbGVOYW1lXCI6XCJVU0VSXCJ9IiwiaWF0IjoxNTczMjg3ODcwLCJleHAiOjE1NzM4OTI2NzB9.8mEksc6z8SchddGXuv9xcqHag1bowv1fiw5dpv81j6FkUURq8Nk9Gjjrp84TU-vRBZN6SqHmNURKzcRip5NsDw";

  @Test
  public void createRawTemplateVersion() throws Exception {
    RawTemplateVersionRequest request = RawTemplateVersionRequest.builder()
        .name(name)
        .rawTemplateId(rawTemplateId)
        .build();
    this.mockMvc.perform(post("/version").content(asJsonString(request))
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Version created"))
        .andExpect(jsonPath("$.data.id").isNumber())
        .andExpect(jsonPath("$.data.name").value(name))
        .andExpect(jsonPath("$.data.content").isString());
  }
}
