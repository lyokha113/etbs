package fpt.capstone.etbs.ControllerTest;

import static fpt.capstone.etbs.ControllerTest.AccountControllerTest.asJsonString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fpt.capstone.etbs.controller.CategoryController;
import fpt.capstone.etbs.payload.CategoryCreateRequest;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-test.properties")
public class CategoryControllerTest {

//  private static final String category = "Hotel";
//  private static final String category = "Art";
  private static final String category = "Auto & Moto";
//  private static final String category = "Beauty & Personal Care";
//  private static final String category = "Beverages";
//  private static final String category = "Business";
//  private static final String category = "Construction";
//  private static final String category = "Consulting";
//  private static final String category = "Education";
//  private static final String category = "Electronics";
  private static final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOlwiOTgyNWExOWUtMGY0OS00YTBmLWE5NDgtYmFmNDNhNWY5NjU3XCIsXCJmdWxsTmFtZVwiOlwiVGhhaSBUZXN0XCIsXCJlbWFpbFwiOlwidGVzdGVtYWlsMUBnbWFpbC5jb21cIixcImltYWdlVXJsXCI6XCJodHRwczovL2ZpcmViYXNlc3RvcmFnZS5nb29nbGVhcGlzLmNvbS92MC9iL2V0YnMtNDQxYTEuYXBwc3BvdC5jb20vby9kZWZhdWx0JTJGZGVmYXVsdF9hdmF0YXIucG5nP2FsdD1tZWRpYSZ0b2tlbj1lYmIyYWUwNy03N2MxLTRkZDMtYjViMy05ODVmNGZiOWViOTlcIixcImFjdGl2ZVwiOnRydWUsXCJwcm92aWRlclwiOlwibG9jYWxcIixcInJvbGVJZFwiOjEsXCJyb2xlTmFtZVwiOlwiQURNSU5JU1RSQVRPUlwifSIsImlhdCI6MTU3MzExNjU5NywiZXhwIjoxNTczNzIxMzk3fQ.mPHz_arR17dTrhKzfTPd9e9Ew97KhCcC_s7R1o-zWoKSgn-KRvYHE8DG2b5y9tNmQJKBqHd6oQQ88i-NqA0mVw";
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CategoryController categoryController;

  @Test
  public void createCategory() throws Exception {
    CategoryCreateRequest request = CategoryCreateRequest.builder()
        .name(category).build();
    this.mockMvc.perform(post("/category")
        .content(asJsonString(request))
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.message").value("Category created"));
  }
}
