package fpt.capstone.etbs.ControllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.capstone.etbs.controller.AccountController;
import fpt.capstone.etbs.payload.LoginRequest;
import fpt.capstone.etbs.payload.RegisterRequest;
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
public class AccountControllerTest {

  private static final String email = "testemail3@gmail.com";
  private static final String fullname = "Thai Test";
  private static final String password = "123456";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccountController accountController;

  @Test
  public void register() throws Exception {
    RegisterRequest request = RegisterRequest.builder()
        .email(email)
        .fullName(fullname)
        .password(password).build();
    this.mockMvc.perform(post("/register")
        .content(asJsonString(request))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.message").value("Account created"));
  }

  @Test
  public void login() throws Exception {
    LoginRequest request = LoginRequest.builder()
        .email(email)
        .password(password).build();
    this.mockMvc.perform(post("/login")
        .content(asJsonString(request))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.message").value("Logged successfully"))
        .andExpect(jsonPath("$.data.accessToken").isString());
  }

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}