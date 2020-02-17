package fpt.capstone.etbs.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicData {
  private String email;
  private List<DynamicDataAttrs> attrs;
}
