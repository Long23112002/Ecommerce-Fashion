package org.example.ecommercefashion.enums.notification;

import lombok.Getter;

@Getter
public enum NotificationCode {
  ORDER_PLACED(
      new String[] {"order_id", "customer_name"},
      "Đặt hàng thành công",
      "Đơn hàng #order_id# của khách hàng #customer_name# đã được đặt thành công"),

  ORDER_SHIPPED(
      new String[] {"order_id"},
      "Đơn hàng đã được giao",
      "Đơn hàng #order_id# đang được giao hàng "),

  PAYMENT_SUCCESS(
      new String[] {"order_id", "payment_method"},
      "Thanh toán thành công",
      "Đơn hàng #order_id# đã được thanh toán thành công qua #payment_method#"),

  CART_ABANDONED(
      new String[] {"customer_name"},
      "Giỏ hàng bị bỏ quên",
      "Khách hàng #customer_name#, bạn có một giỏ hàng chưa hoàn thành. Đừng quên hoàn tất mua hàng!"),

  PRODUCT_BACK_IN_STOCK(
      new String[] {"product_name"},
      "Sản phẩm có hàng lại",
      "Sản phẩm #product_name# mà bạn quan tâm hiện đã có hàng trở lại.");

  private final String[] variables;
  private final String defaultTitle;
  private final String defaultContent;

  NotificationCode(String[] variables, String defaultTitle, String defaultContent) {
    this.variables = variables;
    this.defaultTitle = defaultTitle;
    this.defaultContent = defaultContent;
  }
}
