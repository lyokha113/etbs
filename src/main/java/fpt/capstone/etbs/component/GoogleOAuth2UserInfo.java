package fpt.capstone.etbs.component;

import java.util.Map;

public class GoogleOAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    private Map<String, Object> attributes;

    public String getId() {
        return (String) attributes.get("sub");
    }
    public String getName() {
        return (String) attributes.get("name");
    }
    public String getEmail() {
        return (String) attributes.get("email");
    }
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }

}
