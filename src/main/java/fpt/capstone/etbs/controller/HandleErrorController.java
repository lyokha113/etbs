package fpt.capstone.etbs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.capstone.etbs.payload.ApiError;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class HandleErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) throws JsonProcessingException {
        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        HttpStatus httpStatus = HttpStatus.valueOf(status);
        ApiError error = new ApiError(httpStatus,
                exception == null ? "N/A" : exception.getLocalizedMessage(),
                "N/A");
        return new ObjectMapper().writeValueAsString(error);
    }

    @Override
    public String getErrorPath() {
        return "error";
    }
}
