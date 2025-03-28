package com.kita.organizer.model;

public enum RepeatOption {
    NO_REPEAT(0),
    ONCE_AN_HOUR(1),
    ONCE_A_DAY(2),
    ONCE_A_WEEK(3),
    ONCE_A_YEAR(4);

    private final int value;

    RepeatOption(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RepeatOption fromValue(int value) {
        for (RepeatOption option : values()) {
            if (option.getValue() == value) {
                return option;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}

