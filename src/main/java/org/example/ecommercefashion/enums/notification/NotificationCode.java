package org.example.ecommercefashion.enums.notification;

import lombok.Getter;
import org.example.ecommercefashion.contants.Permissions;

@Getter
public enum NotificationCode {
    ORDER_PLACED(
            "Đặt hàng thành công",
            "Đơn hàng %s của khách hàng %s đã được đặt thành công",
            Permissions.MESSAGE_CONSULT
    ),

    ORDER_SHIPPED(
            "Đơn hàng đã được giao",
            "Đơn hàng %s đang được giao hàng",
            Permissions.MESSAGE_CONSULT
    ),

    PAYMENT_SUCCESS(
            "Thanh toán thành công",
            "Đơn hàng %s đã được thanh toán thành công qua %s",
            Permissions.MESSAGE_CONSULT
    ),

    CART_ABANDONED(
            "Giỏ hàng bị bỏ quên",
            "Khách hàng %s, bạn có một giỏ hàng chưa hoàn thành. Đừng quên hoàn tất mua hàng!",
            Permissions.MESSAGE_CONSULT
    ),

    PRODUCT_BACK_IN_STOCK(
            "Sản phẩm có hàng lại",
            "Sản phẩm %s mà bạn quan tâm hiện đã có hàng trở lại.",
            Permissions.MESSAGE_CONSULT
    ),

    CATEGORY_UPDATED(
            "Cập nhật danh mục",
            "Danh mục %s đã được cập nhật thành công.",
            Permissions.MESSAGE_CONSULT
    )
    ;

    private final String defaultTitle;
    private final String defaultContent;
    private final String permission;

    NotificationCode(String defaultTitle, String defaultContent, String permission) {
        this.defaultTitle = defaultTitle;
        this.defaultContent = defaultContent;
        this.permission = permission;
    }

    public String getContentWithInfor(String... variables) {
        return String.format(this.defaultContent, variables);
    }
}
