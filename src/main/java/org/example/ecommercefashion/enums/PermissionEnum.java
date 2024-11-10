package org.example.ecommercefashion.enums;

public enum PermissionEnum {
    ADD_ROLE("add_role"),
    UPDATE_ROLE("update_role"),
    DELETE_ROLE("delete_role"),
    ADD_COLOR("add_color"),
    UPDATE_COLOR("update_color"),
    DELETE_COLOR("delete_color"),
    ADD_SIZE("add_size"),
    UPDATE_SIZE("update_size"),
    DELETE_SIZE("delete_size"),
    ADD_PRODUCT("add_product"),
    UPDATE_PRODUCT("update_product"),
    DELETE_PRODUCT("delete_product"),
    ADD_CATEGORY("add_category"),
    UPDATE_CATEGORY("update_category"),
    DELETE_CATEGORY("delete_category"),
    ADD_MATERIAL("add_material"),
    UPDATE_MATERIAL("update_material"),
    DELETE_MATERIAL("delete_material"),
    ADD_PROMOTION("add_promotion"),
    UPDATE_PROMOTION("update_promotion"),
    DELETE_PROMOTION("delete_promotion"),
    CUSTOMER_CHAT("customer_chat")
    ;

    public final String val;

    PermissionEnum(String val) {
        this.val = val;
    }

}
