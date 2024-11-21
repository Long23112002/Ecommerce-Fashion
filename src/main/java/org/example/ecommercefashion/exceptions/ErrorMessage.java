package org.example.ecommercefashion.exceptions;

import com.longnh.exceptions.BaseErrorMessage;

public enum ErrorMessage implements BaseErrorMessage {
  DISCOUNT_ALREADY_USED("Giảm Giá đã được sử dụng"),
  DISCOUNT_PERCENTAGE_WRONG_FORMAT("phần trăm phải nằm trong khoảng từ 0 đến 100"),
  DISCOUNT_AMOUNT_WRONG_FORMAT("Giá trị khuyến mãi số tiền phải lớn hơn 1.000"),
  DISCOUNT_NAME_EXISTED("Tên Phiếu Giảm Giá Đã Tồn Tại"),
  DISCOUNT_START_DATE_OR_END_DATE_WRONG("Ngày kết thúc không được nhỏ hơn ngày bắt đầu"),
  DISCOUNT_CONDITION_NOT_MET("Không đủ điều kiện để sử dụng Voucher"),
  VOUCHER_ALREADY_USED("Voucher đã được sử dụng"),
  VOUCHER_NOT_FOUND("Không Tìm thấy voucher"),
  DISCOUNT_NOT_FOUND("Không tìm thấy mã giảm giá"),
  CATEGORY_NAME_EXISTED("Tên Danh Mục Đã Tồn Tại"),
  ORIGIN_NAME_EXISTED("Tên Xuất Xứ Đã Tồn Tại"),
  BRAND_NAME_EXISTED("Tên Thương Hiệu Đã Tồn Tại"),
  ORIGIN_NOT_FOUND("Không tìm thấy Xuất Xứ"),
  BRAND_NOT_FOUND("Không tìm thấy Thương Hiệu"),
  CATEGORY_NOT_FOUND("Không tìm thấy Danh Mục"),
  SUCCESS("Thành Công"),
  FALSE("Sai"),
  ROLE_NOT_FOUND("Không tìm thấy Vai Trò"),
  PERMISSION_NOT_FOUND("Không tìm thấy Quyền"),
  USER_NOT_FOUND("Không tìm thấy Người Dùng"),
  ROLE_ALREADY_ASSIGNED("Vai Trò đã được gán"),
  JWT_EXPIRED("JWT đã hết hạn"),
  ACCESS_TOKEN_NOT_FOUND("Không tìm thấy access token"),
  REFRESH_TOKEN_NOT_FOUND("Không tìm thấy mã thông báo làm mới"),
  BAD_CREDENTIAL("Thông tin xác thực không chính xác"),
  INVALID_REFRESH_TOKEN("Mã thông báo làm mới không hợp lệ"),
  CURRENT_PASSWORD_SAME_NEW_PASSWORD("Mật khẩu hiện tại giống mật khẩu mới"),
  PASSWORD_DO_NOT_MATCH("Mật khẩu không đúng"),
  OTP_IS_EXPIRED("OTP đã hết hạn"),
  ERROR_UPLOADING_FILE("Lỗi khi tải tệp lên"),
  SOMETHING_WENT_WRONG("Đã xảy ra lỗi"),
  NOT_FOUND_BUCKET("Không tìm thấy bucket"),
  ACCESS_DENIED("Truy cập bị từ chối"),
  IMAGE_NOT_FOUND("Không tìm thấy Hình Ảnh"),
  ROLE_EXISTED("Vai Trò đã tồn tại"),
  EMAIL_NOT_VERIFIED("Email chưa được xác minh"),
  CHAT_NOT_FOUND("Không tìm thấy chat"),

  WEBSOCKET_NOT_FOUND("Không tìm thấy Websocket"),
  USER_PERMISSION_DENIED("Người dùng không có quyền yêu cầu này"),
  EMAIL_EXISTED("Email của bạn đã tồn tại trong hệ thống"),
  PHONE_EXISTED("Số điện thoại của bạn đã tồn tại trong hệ thống"),
  CHAT_ROOM_NOT_FOUND("Không tìm thấy phòng trò chuyện"),
  USER_ALREADY_HAS_CHAT_ROOM("Người dùng đã tồn tại chat room"),
  TEMPLATE_NOT_FOUND("Không tìm thấy Mẫu"),
  OTP_NOT_MATCH("OTP không đúng vui lòng kiểm tra lại"),
  OTP_EXPIRED("OTP đã hết hạn"),
  PROMOTION_NOT_FOUND("Không tìm thấy khuyến mãi"),
  PROMOTION_PERCENTAGE_WRONG_FORMAT("Giá trị khuyến mãi phần trăm phải nằm trong khoảng từ 0 đến 100"),
  PROMOTION_AMOUNT_WRONG_FORMAT("Giá trị khuyến mãi số tiền phải lớn hơn 1.000"),
  PROMOTION_START_DATE_OR_END_DATE_WRONG("Ngày kết thúc không được nhỏ hơn ngày bắt đầu"),
  PRODUCT_NAME_EXISTED("Tên Sản Phẩm Đã Tồn Tại"),
  PRODUCT_NOT_FOUND("Không tìm thấy Sản Phẩm"),
  PRODUCT_WAS_DISABLE("Sản Phẩm bị ẩn "),
  SECURE_NOT_MATCH("VNPAY secure không hợp lệ "),
  INVALID_PAY_AMOUNT("Số tiền thanh toán không hợp lệ "),
  PAYMENT_FAILED("Giao dịch thất bại"),
  INVALID_ADDRESS("Địa chỉ không được trống"),
  ORDER_NOT_IN_DRAFT("Đơn hàng không thể thay đổi khi không ở trạng thái nháp"),
  ERROR_WHEN_CREATE("Lỗi khi tạo"),
  COLOR_HAS_PRODUCT_DETAIL("Màu sắc đang thuộc về 1 chi tiết sản phẩm"),
  SIZE_HAS_PRODUCT_DETAIL("Size đang thuộc về 1 chi tiết sản phẩm"),
  MATERIAL_HAS_PRODUCT_DETAIL("Chất liệu đang thuộc về 1 chi tiết sản phẩm"),
  BRAND_HAS_PRODUCT("Thương hiệu đang thuộc về 1 chi tiết sản phẩm"),
  CATEGORY_HAS_PRODUCT("Danh mục đang thuộc về 1 chi tiết sản phẩm"),
  ORIGIN_HAS_PRODUCT("Xuất xứ sản phẩm đang thuộc về 1 chi tiết sản phẩm"),
  CODE_COLOR_EXIST("Mã màu đã tồn tại"),
  NON_NEGATIVE_AMOUNT("Tổng tiền không được âm"),
  PRODUCT_DETAIL_NOT_FOUND("Sản phẩm chi tiết không tồn tại"),
  PRODUCT_DETAIL_IDS_EMPTY("ID sản phẩm chi tiết trống"),
  PROMOTION_DATE_OVERLAP("Không được tạo đợt giảm giá có khoảng thời gian chồng nhau"),
  PRODUCT_NOT_ENOUGH("Số lượng sản phẩm không đủ"),
  EXPORT_EXCEL_ERROR("Lỗi khi xuất file excel"),
  PRODUCT_HAS_PRODUCT_DETAIL("Sản phẩm đang có từ một chi tiết sản phẩm"),
  PROMOTION_END_DATE_WRONG("Ngày kết thúc không được nhỏ hơn ngày hiện tại"),

  ;

  public String val;

  private ErrorMessage(String label) {
    val = label;
  }

  @Override
  public String val() {
    return val;
  }
}