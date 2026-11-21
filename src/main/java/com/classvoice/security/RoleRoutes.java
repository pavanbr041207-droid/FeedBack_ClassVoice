package com.classvoice.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public final class RoleRoutes {

    private RoleRoutes() {
    }

    public static String homeFor(Collection<? extends GrantedAuthority> authorities) {
        for (GrantedAuthority a : authorities) {
            String name = a.getAuthority();
            if ("ROLE_COLLEGE_ADMIN".equals(name)) {
                return "/admin/home";
            }
            if ("ROLE_TEACHER".equals(name)) {
                return "/teacher/home";
            }
            if ("ROLE_STUDENT".equals(name)) {
                return "/student/home";
            }
        }
        return "/login";
    }
}
