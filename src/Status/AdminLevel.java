package Status;

public enum AdminLevel {
    SUPER_ADMIN,
    REGULAR_ADMIN;

    public int toDbValue() {
        return ordinal() + 1;
    }

    public static AdminLevel fromDbValue(int value) {
        return values()[value - 1];
    }
}