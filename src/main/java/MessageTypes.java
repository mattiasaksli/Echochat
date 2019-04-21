public enum MessageTypes {

    END_SESSION(-2),
    EXIT_CHATROOM(-1),
    TEXT(1),
    UPDATE_REQ(5),
    USER_MAP(6),
    AUTHOR_SIGNATURE(7),
    CHATROOMS_LIST_REQ(50),
    CHATROOM_SIGNATURE(51),
    CHATROOMS_LIST_SUCCESS(52),
    CHATROOMS_USER_CONNECTED(53),
    REGISTRATION_REQ(100),
    REGISTRATION_SUCCESS(101),
    REGISTRATION_WRONG_USERNAME(102),
    REGISTRATION_WRONG_PASSWORD(103),
    LOGIN_REQ(200),
    LOGIN_SUCCESS(201),
    LOGIN_WRONG_USERNAME(202),
    LOGIN_WRONG_PASSWORD(203),
    LOGIN_MISSING_DB(204);

    private int typeValue;

    MessageTypes(int typeValue) {
        this.typeValue = typeValue;
    }

    public int value() {
        return typeValue;
    }
}