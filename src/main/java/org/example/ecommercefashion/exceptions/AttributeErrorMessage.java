package org.example.ecommercefashion.exceptions;

import com.longnh.exceptions.BaseErrorMessage;

public enum AttributeErrorMessage  implements BaseErrorMessage {
    SIZE_NOT_FOUND("Size không tồn tại"),
    COLOR_NOT_FOUND("Màu không tồn tại"),
    SIZE_NAME_EXISTED("Tên size tồn tại"),
    COLOR_NAME_EXISTED("Tên màu tồn tại"),
    MATERIAL_NOT_FOUND("Chất liệu không tồn tại"),
    MATERIAL_NAME_EXISTED("Chất liệu tồn tại"),
    ;

    public String val;

    private AttributeErrorMessage(String label) {
        val = label;
    }

    @Override
    public String val() {
        return val;
    }
}
