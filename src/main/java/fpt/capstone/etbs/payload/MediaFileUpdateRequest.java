package fpt.capstone.etbs.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileUpdateRequest {
    private int id;
    private String link;
    private String name;
    private String type;
    private boolean active;
}
