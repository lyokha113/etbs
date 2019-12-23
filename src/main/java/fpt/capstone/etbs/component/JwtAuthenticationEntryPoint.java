package fpt.capstone.etbs.component;


import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.capstone.etbs.payload.ApiError;
import java.io.IOException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

  private static final long serialVersionUID = -7858869558953243875L;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    HttpStatus httpStatus = HttpStatus.valueOf(HttpServletResponse.SC_UNAUTHORIZED);
    ApiError error = new ApiError(httpStatus, "N/A", "N/A");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.getWriter().append(new ObjectMapper().writeValueAsString(error));
  }
}
