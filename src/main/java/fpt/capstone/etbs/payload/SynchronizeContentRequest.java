package fpt.capstone.etbs.payload;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SynchronizeContentRequest {

  private int blockId;
  private List<Integer> rawIds;
}
