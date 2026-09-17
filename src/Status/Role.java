package Status;

public enum Role {
    STUDENT,
    PROFESSOR,
    ADMIN;

    public int toDbValue() {
        return ordinal() + 1;
    }

    public static Role fromDbValue(int value) {
        return values()[value - 1];
    }
}