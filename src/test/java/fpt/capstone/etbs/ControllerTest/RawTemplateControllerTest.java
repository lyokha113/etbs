package fpt.capstone.etbs.ControllerTest;

import static fpt.capstone.etbs.ControllerTest.AccountControllerTest.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fpt.capstone.etbs.controller.RawTemplateController;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
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
public class RawTemplateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RawTemplateController rawTemplateController;

  private static final String description = "dsafasdfdsfdsafsd";
  private static final String name = "Test raw template name 1212121232424242";
  private static final Integer worspaceId = 2;
  private static final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOlwiMDE4MDQwMjUtYWZhZi00MTAzLTk1MTctZWY5ZjNmMGY3N2NhXCIsXCJmdWxsTmFtZVwiOlwiVGhhaSBUZXN0XCIsXCJlbWFpbFwiOlwidGVzdGVtYWlsMkBnbWFpbC5jb21cIixcImltYWdlVXJsXCI6XCJodHRwczovL2ZpcmViYXNlc3RvcmFnZS5nb29nbGVhcGlzLmNvbS92MC9iL2V0YnMtNDQxYTEuYXBwc3BvdC5jb20vby9kZWZhdWx0JTJGZGVmYXVsdF9hdmF0YXIucG5nP2FsdD1tZWRpYSZ0b2tlbj1lYmIyYWUwNy03N2MxLTRkZDMtYjViMy05ODVmNGZiOWViOTlcIixcImFjdGl2ZVwiOnRydWUsXCJwcm92aWRlclwiOlwibG9jYWxcIixcInJvbGVJZFwiOjIsXCJyb2xlTmFtZVwiOlwiVVNFUlwifSIsImlhdCI6MTU3MzExNzA2NSwiZXhwIjoxNTczNzIxODY1fQ.-1hLdmXQO0xLJkSPA3qm9kvGsGpamfHWMJ6b3XQnJVxmDi_dUgVCtYQFELe7Qk5hak-d6TpQ8b6H7FJQTTAjHg";

  @Test
  public void createRawTemplate() throws Exception {
    RawTemplateCreateRequest request = RawTemplateCreateRequest.builder()
        .description(description)
        .name(name)
        .workspaceId(worspaceId)
        .build();
    this.mockMvc.perform(post("/raw")
        .content(asJsonString(request))
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").isBoolean())
        .andExpect(jsonPath("$.message").value("Raw template created"))
        .andExpect(jsonPath("$.data.id").isNumber())
        .andExpect(jsonPath("$.data.workspaceId").value(worspaceId))
        .andExpect(jsonPath("$.data.name").value(name))
        .andExpect(jsonPath("$.data.thumbnail").isString())
        .andExpect(jsonPath("$.data.content").isString())
        .andExpect(jsonPath("$.data.description").value(description))
        .andExpect(jsonPath("$.data.currentVersion").hasJsonPath())
        .andExpect(jsonPath("$.data.versions").hasJsonPath())
    ;
  }
}
