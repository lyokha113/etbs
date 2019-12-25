package fpt.capstone.etbs.ControllerTest;

import static fpt.capstone.etbs.ControllerTest.AccountControllerTest.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fpt.capstone.etbs.constant.IntegrationTesting;
import fpt.capstone.etbs.controller.RawTemplateVersionController;
import fpt.capstone.etbs.payload.RawTemplateVersionRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RawTemplateVersionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RawTemplateVersionController rawTemplateVersionController;

  private static final String name = "Test version of raw template ";
  private static final Integer rawTemplateId = 1;
  private static final String token = IntegrationTesting.TOKEN_TEST;

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
