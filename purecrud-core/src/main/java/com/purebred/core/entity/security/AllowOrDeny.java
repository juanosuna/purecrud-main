package com.purebred.core.entity.security;

public enum AllowOrDeny {
    ALLOW("Allow"),
    DENY("Deny");

    private String displayName;

    AllowOrDeny(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}