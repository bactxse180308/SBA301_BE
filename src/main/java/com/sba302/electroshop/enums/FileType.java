package com.sba302.electroshop.enums;

import lombok.Getter;

@Getter
public enum FileType {
    PRODUCT("products"),
    BRAND("brands"),
    CATEGORY("categories"),
    USER("users"),
    OTHER("others");

    private final String folderName;

    FileType(String folderName) {
        this.folderName = folderName;
    }
}
