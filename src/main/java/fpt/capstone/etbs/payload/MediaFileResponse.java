package fpt.capstone.etbs.payload;

import fpt.capstone.etbs.model.MediaFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaFileResponse {

    public MediaFileResponse(MediaFile mediaFile) {
        this.name = mediaFile.getName();
        this.link = mediaFile.getLink();
    }

    private String name;
    private String link;

}
