package fpt.capstone.etbs.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateTokenParam<T> {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T data;
}
