package fpt.capstone.etbs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.capstone.etbs.payload.ApiError;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HandleErrorController implements ErrorController {

  @RequestMapping(value = "/error", method = {RequestMethod.GET, RequestMethod.POST,
      RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
  public String handleError(HttpServletRequest request) throws JsonProcessingException {
    Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    HttpStatus httpStatus = HttpStatus.valueOf(status);
    ApiError error = new ApiError(
        httpStatus, exception == null ? "N/A" : exception.getLocalizedMessage(), "N/A");
    return new ObjectMapper().writeValueAsString(error);
  }

  @MessageExceptionHandler
  public String handleWebsocketException(IllegalArgumentException ex)
      throws JsonProcessingException {
    Map<String, WebSocketCloseStatus> error = new HashMap<>();
    error.put("Error", WebSocketCloseStatus.POLICY_VIOLATION);
    return new ObjectMapper().writeValueAsString(error);
  }

  @Override
  public String getErrorPath() {
    return "error";
  }
}
