package org.example.ecommercefashion.enums;

public enum TypeImage {
    JPEG("image/jpeg"),
    PNG("image/png"),
    SVG("image/svg+xml"),
    JPG("image/jpg"),
    GIF("image/gif"),
    BMP("image/bmp");

    private final String mimeType;

    TypeImage(String mimeType) {
        this.mimeType = mimeType;
    }

    public static TypeImage fromMimeType(String mimeType) {
        for (TypeImage type : TypeImage.values()) {
            if (type.mimeType.equalsIgnoreCase(mimeType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MIME type: " + mimeType);
    }

    public String getMimeType() {
        return mimeType;
    }
}
