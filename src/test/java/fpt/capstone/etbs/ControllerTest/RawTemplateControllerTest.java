package fpt.capstone.etbs.ControllerTest;

import static fpt.capstone.etbs.ControllerTest.AccountControllerTest.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fpt.capstone.etbs.constant.IntegrationTesting;
import fpt.capstone.etbs.controller.RawTemplateController;
import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.payload.RawTemplateCreateRequest;
import fpt.capstone.etbs.payload.RawTemplateUpdateRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RawTemplateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RawTemplateController rawTemplateController;

  private static final String description = "Test raw template description";
  private static final String content = "Test raw template content";
  private static final String name = "Test raw template name 5";
  private static final Integer worspaceId = 8;
  private static final Integer templateId = 8;
  private static final String token = IntegrationTesting.TOKEN_TEST;

  @Test
  public void createRawTemplate() throws Exception {
    RawTemplateCreateRequest request = RawTemplateCreateRequest.builder()
        .description(description)
        .name(name)
        .workspaceId(worspaceId)
        .build();
    this.mockMvc.perform(
        MockMvcRequestBuilders
            .post("/raw")
            .content(asJsonString(request))
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Raw template created"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.workspaceId").value(worspaceId))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(name))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.thumbnail").isString())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").isString())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(description))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.currentVersion").hasJsonPath())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.versions").hasJsonPath())
    ;
  }

  @Test
  public void createRawTemplateFromTemplate() throws Exception {
    RawTemplateCreateRequest request = RawTemplateCreateRequest.builder()
        .description(description)
        .name(name)
        .workspaceId(worspaceId)
        .templateId(templateId)
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

  @Test
  public void getRawTemplate() throws Exception {
    this.mockMvc.perform(get("/raw/16")
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

  @Test
  public void updateRawTemate() throws Exception {
    this.mockMvc.perform(put("/raw/14")
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
