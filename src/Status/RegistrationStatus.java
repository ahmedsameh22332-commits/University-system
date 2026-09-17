package Status;

public enum RegistrationStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public int toDbValue() {
        return ordinal() + 1;
    }

    public static RegistrationStatus fromDbValue(int value) {
        return values()[value - 1];
    }
}