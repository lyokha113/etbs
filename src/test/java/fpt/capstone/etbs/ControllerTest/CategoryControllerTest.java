package fpt.capstone.etbs.ControllerTest;

import static fpt.capstone.etbs.ControllerTest.AccountControllerTest.asJsonString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
//  private static final String category = "Auto & Moto";
//  private static final String category = "Beauty & Personal Care";
  private static final String category = "Beverages";
  //  private static final String category = "Business";
//  private static final String category = "Construction";
//  private static final String category = "Consulting";
//  private static final String category = "Education";
//  private static final String category = "Electronics";
  private static final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOlwiOWIyYWM2MmMtODU5Zi00MjE1LTkyNTctMjA5MDE4NjMzMjM0XCIsXCJmdWxsTmFtZVwiOlwiVGhhaSBUZXN0XCIsXCJlbWFpbFwiOlwidGVzdGVtYWlsQGdtYWlsLmNvbVwiLFwiaW1hZ2VVcmxcIjpcImh0dHBzOi8vZmlyZWJhc2VzdG9yYWdlLmdvb2dsZWFwaXMuY29tL3YwL2IvZXRicy00NDFhMS5hcHBzcG90LmNvbS9vL2RlZmF1bHQlMkZkZWZhdWx0X2F2YXRhci5wbmc_YWx0PW1lZGlhJnRva2VuPWViYjJhZTA3LTc3YzEtNGRkMy1iNWIzLTk4NWY0ZmI5ZWI5OVwiLFwiYWN0aXZlXCI6dHJ1ZSxcInByb3ZpZGVyXCI6XCJsb2NhbFwiLFwicm9sZUlkXCI6MixcInJvbGVOYW1lXCI6XCJVU0VSXCJ9IiwiaWF0IjoxNTczMjg3ODcwLCJleHAiOjE1NzM4OTI2NzB9.8mEksc6z8SchddGXuv9xcqHag1bowv1fiw5dpv81j6FkUURq8Nk9Gjjrp84TU-vRBZN6SqHmNURKzcRip5NsDw";
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

  @Test
  public void getListCategory() throws Exception {
    this.mockMvc.perform(get("/category")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").hasJsonPath());
  }
}
