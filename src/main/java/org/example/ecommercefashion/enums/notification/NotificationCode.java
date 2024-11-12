package org.example.ecommercefashion.enums.notification;

import lombok.Getter;
import org.example.ecommercefashion.enums.PermissionEnum;

@Getter
public enum NotificationCode {

    //NOTE: CÁCH FORMAT ENUM
    //      TÊN ENUM PHẢI VIẾT HOA VÀ CAMEL_CASE VÀ CÓ Ý NGHĨA RÕ RÀNG
    //      THAM SỐ ĐẦU TIÊN: TÊN TITLE -> SẼ HIỆN THỊ Ở TOAST KHI CÓ THÔNG BÁO MỚI
    //      THAM SỐ THỨ HAI: NỘI DỤNG CỦA THÔNG BÁO -> SẼ HIỂN THỊ Ở TRONG BOX NOTIFICATION FRONTEND
    //                       "%s" LÀ NHỮNG NƠI ĐƯỢC THAY THẾ CHO BIẾN. CÓ THỂ VÔ HẠN SỐ LẦN %s
    //                       LƯU Ý: NẾU SỬ DỤNG %s BAO NHIÊN LẦN THÌ KHI GỌI HÀM SERVICE NHỚ VIẾT ĐỦ CÁC THAM SỐ VỚI SỐ %s
    //                       CÓ THỂ IN ĐẬM BẰNG CÁCH CHO VÀO <b></b> HOẶC CHỮ NGHIÊNG BẰNG <i></i>
    //      THAM SỐ THỨ BA: PERMISSION SẼ ĐƯỢC GỬI -> PERMISSION LÀ MESSAGE_COUNT THÌ SẼ GỬI CHO NHỮNG USER CÓ PERMISSION MESSAGE_COUNT

    ORDER_PLACED(
            "Đặt hàng thành công",
            "Đơn hàng %s của khách hàng %s đã được đặt thành công",
            PermissionEnum.CUSTOMER_CHAT.val
    ),

    ORDER_SHIPPED(
            "Đơn hàng đã được giao",
            "Đơn hàng %s đang được giao hàng",
            PermissionEnum.CUSTOMER_CHAT.val
    ),

    PAYMENT_SUCCESS(
            "Thanh toán thành công",
            "Đơn hàng %s đã được thanh toán thành công qua %s",
            PermissionEnum.CUSTOMER_CHAT.val
    ),

    CART_ABANDONED(
            "Giỏ hàng bị bỏ quên",
            "Khách hàng %s, bạn có một giỏ hàng chưa hoàn thành. Đừng quên hoàn tất mua hàng!",
            PermissionEnum.CUSTOMER_CHAT.val
    ),

    PRODUCT_BACK_IN_STOCK(
            "Sản phẩm có hàng lại",
            "Sản phẩm %s mà bạn quan tâm hiện đã có hàng trở lại.",
            PermissionEnum.CUSTOMER_CHAT.val
    ),

    CATEGORY_UPDATED(
            "Cập nhật danh mục",
            "Đã cập nhật lại danh mục id là: <b>%s</b> thành công.",
            PermissionEnum.CUSTOMER_CHAT.val
    ),

    GREETING_NOTIFICATION(
            "Xin chào",
            "Chào mừng <b>%s</b> đã đến với cửa hàng.",
            null
    ),

    TEST(
            "Test",
            "User: <b>%s</b> vừa test.",
            PermissionEnum.CUSTOMER_CHAT.val
    ),

    CREATE_COLOR(
            "Thêm màu sắc thành công",
            "<b>%s</b> vừa được thêm thành công vào thuộc tính",
            PermissionEnum.ADD_COLOR.val
    ),

    UPDATE_COLOR(
            "Cập nhật màu sắc thành công",
            "Đã cập nhật <b>%s</b> thành <b>%s</b>",
            PermissionEnum.UPDATE_COLOR.val
    ),

    CREATE_PROMOTION(
            "Thêm mới đợt giảm giá thành công",
            "Đã thêm mới đợt giảm giá từ <b>%s</b> đến <b>%s</b>",
            PermissionEnum.ADD_PROMOTION.val
    ),

    UPDATE_PROMOTION(
            "Cập nhật đợt giảm giá thành công",
            "Cập nhật đợt giảm giá id: <b>%s</b>",
            PermissionEnum.UPDATE_PROMOTION.val
    ),

    ADD_PRODUCT_DETAIL_TO_PROMOTION(
            "Đã lên lịch giảm giá cho sản phẩm",
            "Đã cập nhật chi tiết sản phẩm vào đợt giảm giá id: <b>%s</b>",
            PermissionEnum.ADD_PROMOTION.val
    ),

    DELETE_PRODUCT_DETAIL_FROM_PROMOTION(
            "Đã xóa chi tiết sản phẩm khỏi đợt giảm giá",
            "Đã xóa tất cả sản phẩm cho đợt giảm giá id: <b>%s</b>",
            PermissionEnum.DELETE_PROMOTION.val
    ),
    ;

    private final String defaultTitle;
    private final String defaultContent;
    private final String permission;

    NotificationCode(String defaultTitle, String defaultContent, String permission) {
        this.defaultTitle = defaultTitle;
        this.defaultContent = defaultContent;
        this.permission = permission;
    }

    public String getContentWithInfor(Object... variables) {
        return String.format(this.defaultContent, variables);
    }
}
