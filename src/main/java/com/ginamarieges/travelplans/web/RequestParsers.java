package com.ginamarieges.travelplans.web;

import com.ginamarieges.travelplans.domain.PlanType;

public final class RequestParsers {

    private RequestParsers() {
    }

    public static PlanType parsePlanType(String rawType) {
        if (rawType == null || rawType.trim().isEmpty()) {
            return null;
        }
        try {
            return PlanType.valueOf(rawType.trim());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static Integer parseIntegerOrNull(String rawNumber) {
        if (rawNumber == null) {
            return null;
        }
        String trimmed = rawNumber.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(trimmed);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static String safeString(String value) {
        return value == null ? "" : value;
    }
}
