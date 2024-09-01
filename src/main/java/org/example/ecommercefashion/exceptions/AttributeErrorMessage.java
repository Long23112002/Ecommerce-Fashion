package org.example.ecommercefashion.exceptions;

import com.longnh.exceptions.BaseErrorMessage;

public enum AttributeErrorMessage  implements BaseErrorMessage {
    SIZE_NOT_FOUND("Size not found"),
    COLOR_NOT_FOUND("Color not found")
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
