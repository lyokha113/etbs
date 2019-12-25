package fpt.capstone.etbs.ControllerTest;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.capstone.etbs.constant.RoleEnum;
import fpt.capstone.etbs.controller.AccountController;
import fpt.capstone.etbs.model.Account;
import fpt.capstone.etbs.model.Role;
import fpt.capstone.etbs.payload.AccountCreateRequest;
import fpt.capstone.etbs.payload.LoginRequest;
import fpt.capstone.etbs.payload.RegisterRequest;
import fpt.capstone.etbs.repository.AccountRepository;
import fpt.capstone.etbs.repository.RoleRepository;
import fpt.capstone.etbs.repository.WorkspaceRepository;
import fpt.capstone.etbs.service.AccountService;
import java.util.ArrayList;
import java.util.List;
import jdk.internal.jline.internal.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

  private static final String email = "testemail@gmail.com";
  private static final String fullname = "Thai Test";
  private static final String password = "123456";

  @MockBean
  RoleRepository roleRepository;

  @MockBean
  AccountRepository accountRepository;

  @MockBean
  WorkspaceRepository workspaceRepository;

  @MockBean
  AccountService accountService;

  @Autowired
  MockMvc mockMvc;

  @Before
  public void setup() {
    if (!roleRepository.findById(1).isPresent() && !roleRepository.findById(2).isPresent()) {
      Role role = Role.builder().active(true).name(RoleEnum.ADMINISTRATOR.getName()).build();
      roleRepository.save(role);
      role = Role.builder().active(true).name(RoleEnum.USER.getName()).build();
      roleRepository.save(role);
    }
  }

  @Test
  public void register() throws Exception {
    AccountCreateRequest request = AccountCreateRequest.builder()
        .email(email)
        .fullName(fullname)
        .password(password).build();
    Account account = new Account();
    given(accountService.createAccount(request)).willReturn(account);
    this.mockMvc.perform(post("/register")
        .content(asJsonString(request))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Account created"))
        .andExpect(jsonPath("$.data.id").isString())
        .andExpect(jsonPath("$.data.fullName").value(fullname))
        .andExpect(jsonPath("$.data.email").value(request.getEmail()))
        .andExpect(jsonPath("$.data.imageUrl").isString())
        .andExpect(jsonPath("$.data.active").value(true))
        .andExpect(jsonPath("$.data.provider").value("local"))
        .andExpect(jsonPath("$.data.roleId").value(RoleEnum.USER.getId()))
        .andExpect(jsonPath("$.data.roleName").value(RoleEnum.USER.getName()))
    ;
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