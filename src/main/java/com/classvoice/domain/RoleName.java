package com.classvoice.domain;

/** Role names as stored in the roles table (Spring authority = ROLE_ + name). */
public final class RoleName {
    public static final String COLLEGE_ADMIN = "COLLEGE_ADMIN";
    public static final String TEACHER = "TEACHER";
    public static final String STUDENT = "STUDENT";

    private RoleName() {
    }
}
