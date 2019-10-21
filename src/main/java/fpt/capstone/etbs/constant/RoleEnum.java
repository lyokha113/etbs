package fpt.capstone.etbs.constant;

import java.util.Arrays;

public enum RoleEnum {

    ADMINISTRATOR(1, "ADMINISTRATOR"), USER(2, "USER");

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    RoleEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static RoleEnum getRole(int roleId) {
        return Arrays.stream(RoleEnum.values()).filter(role -> role.getId() == roleId)
                .findFirst().orElse(null);
    }
}
