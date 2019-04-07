public enum MessageTypes {

    END_SESSION(-1),
    TEXT(1),
    UPDATE_REQ(5),
    USER_MAP(6),
    AUTHOR_SIGNATURE(7),
    REGISTRATION_REQ(100),
    REGISTRATION_SUCCESS(101),
    REGISTRATION_FAIL(102),
    LOGIN_REQ(200),
    LOGIN_SUCCESS(201),
    LOGIN_FAIL(202);

    private int typeValue;

    MessageTypes(int typeValue) {
        this.typeValue = typeValue;
    }

    public int getValue() {
        return typeValue;
    }
}
