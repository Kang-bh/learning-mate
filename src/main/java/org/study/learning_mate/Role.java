package org.study.learning_mate;

public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }

    public static Role fromString(String roleType) {
        for (Role role : Role.values()) {
            if (role.getRole().equalsIgnoreCase(roleType)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant for role type: " + roleType);
    }
}