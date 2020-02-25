package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.RawTemplate;
import fpt.capstone.etbs.model.UserBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBlockResponse {

  private Integer id;
  private String name;
  private String tag;
  private String icon;
  private String content;

  public static UserBlockResponse setResponse(UserBlock block) {
    return UserBlockResponse.builder()
        .id(block.getId())
        .name(block.getName())
        .tag(block.getTag())
        .icon(block.getIcon())
        .build();
  }

  public static UserBlockResponse setResponseWithContent(UserBlock block) {
    UserBlockResponse response = UserBlockResponse.setResponse(block);
    response.setContent(block.getContent());
    return response;
  }
}


