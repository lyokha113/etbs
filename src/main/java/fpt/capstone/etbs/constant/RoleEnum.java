package fpt.capstone.etbs.constant;

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
}
