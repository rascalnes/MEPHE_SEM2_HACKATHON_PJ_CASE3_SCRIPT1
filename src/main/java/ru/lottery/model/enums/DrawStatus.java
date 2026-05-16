package ru.lottery.model.enums;

public enum DrawStatus {
    DRAFT("DRAFT"),
    ACTIVE("ACTIVE"),
    FINISHED("FINISHED");

    private final String value;

    DrawStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DrawStatus fromValue(String value) {
        for (DrawStatus status : DrawStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return DRAFT;
    }

    public boolean canTransitionTo(DrawStatus newStatus) {
        switch (this) {
            case DRAFT:
                return newStatus == ACTIVE;
            case ACTIVE:
                return newStatus == FINISHED;
            case FINISHED:
                return false;
            default:
                return false;
        }
    }
}