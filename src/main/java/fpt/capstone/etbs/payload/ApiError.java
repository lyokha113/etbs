package fpt.capstone.etbs.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

  private HttpStatus status;
  private String message;
  private List<String> errors;

  public ApiError(final HttpStatus status, final String message, final String error) {
    super();
    this.status = status;
    this.message = message;
    errors = Collections.singletonList(error);
  }
}
