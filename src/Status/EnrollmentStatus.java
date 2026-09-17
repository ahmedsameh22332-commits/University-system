package Status;

public enum EnrollmentStatus {
    ENROLLED,
    COMPLETED,
    FAILED,
    DROPPED;

    public int toDbValue() {
        return ordinal() + 1;
    }

    public static EnrollmentStatus fromDbValue(int value) {
        return values()[value - 1];
    }
}