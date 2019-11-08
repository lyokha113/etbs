package fpt.capstone.etbs.ControllerTest;

import static fpt.capstone.etbs.ControllerTest.AccountControllerTest.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fpt.capstone.etbs.controller.RatingController;
import fpt.capstone.etbs.model.Rating;
import fpt.capstone.etbs.payload.RatingRequest;
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
public class RatingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RatingController ratingController;

  private static final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJpZFwiOlwiOTgyNWExOWUtMGY0OS00YTBmLWE5NDgtYmFmNDNhNWY5NjU3XCIsXCJmdWxsTmFtZVwiOlwiVGhhaSBUZXN0XCIsXCJlbWFpbFwiOlwidGVzdGVtYWlsMUBnbWFpbC5jb21cIixcImltYWdlVXJsXCI6XCJodHRwczovL2ZpcmViYXNlc3RvcmFnZS5nb29nbGVhcGlzLmNvbS92MC9iL2V0YnMtNDQxYTEuYXBwc3BvdC5jb20vby9kZWZhdWx0JTJGZGVmYXVsdF9hdmF0YXIucG5nP2FsdD1tZWRpYSZ0b2tlbj1lYmIyYWUwNy03N2MxLTRkZDMtYjViMy05ODVmNGZiOWViOTlcIixcImFjdGl2ZVwiOnRydWUsXCJwcm92aWRlclwiOlwibG9jYWxcIixcInJvbGVJZFwiOjEsXCJyb2xlTmFtZVwiOlwiQURNSU5JU1RSQVRPUlwifSIsImlhdCI6MTU3MzExNjU5NywiZXhwIjoxNTczNzIxMzk3fQ.mPHz_arR17dTrhKzfTPd9e9Ew97KhCcC_s7R1o-zWoKSgn-KRvYHE8DG2b5y9tNmQJKBqHd6oQQ88i-NqA0mVw";

  @Test
  public void rateUpvote() throws Exception {
    RatingRequest request = RatingRequest.builder().templateId(1).vote(true).build();
    this.mockMvc.perform(post("/rate")
        .content(asJsonString(request))
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"));
  }
}
