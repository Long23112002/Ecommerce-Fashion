package org.example.ecommercefashion.exceptions;

import com.longnh.exceptions.BaseErrorMessage;

public enum AttributeErrorMessage  implements BaseErrorMessage {
    SIZE_NOT_FOUND("Size not found"),
    COLOR_NOT_FOUND("Color not found"),
    SIZE_NAME_EXISTED("Size name already exists"),
    COLOR_NAME_EXISTED("Color name already exists"),
    MATERIAL_NOT_FOUND("Material not found"),
    MATERIAL_NAME_EXISTED("Material name already exists")
    ;

    public String val;

    private AttributeErrorMessage(String label) {
        val = label;
    }

    @Override
    public String val() {
        return "";
    }
}
