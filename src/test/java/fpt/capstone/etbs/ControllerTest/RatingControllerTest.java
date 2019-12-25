package fpt.capstone.etbs.ControllerTest;

import static fpt.capstone.etbs.ControllerTest.AccountControllerTest.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fpt.capstone.etbs.constant.IntegrationTesting;
import fpt.capstone.etbs.controller.RatingController;
import fpt.capstone.etbs.payload.RatingRequest;
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
public class RatingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RatingController ratingController;

  private static final String token = IntegrationTesting.TOKEN_TEST;

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
