INSERT INTO discounts.voucher (code,
                               id_discount,
                               create_at,
                               update_at,
                               used_at,
                               create_by,
                               update_by,
                               used_by,
                               deleted)
VALUES (uuid_generate_v4(), -- Tạo UUID mới
        1, -- ID giảm giá liên kết với bảng discounts.discount (phải tồn tại)
        CURRENT_DATE, -- Ngày tạo
        CURRENT_DATE, -- Ngày cập nhật (NULL nếu chưa cập nhật)
        NULL, -- Ngày sử dụng (NULL nếu chưa sử dụng)
        1, -- Người tạo
        1, -- Người cập nhật (NULL nếu chưa cập nhật)
        1, -- Người sử dụng (NULL nếu chưa sử dụng)
        FALSE -- Không bị xóa
       );

INSERT INTO discounts.voucher (code,
                               id_discount,
                               create_at,
                               update_at,
                               used_at,
                               create_by,
                               update_by,
                               used_by,
                               deleted)
VALUES (uuid_generate_v4(), -- Tạo UUID mới
        2, -- ID giảm giá liên kết với bảng discounts.discount (phải tồn tại)
        CURRENT_DATE, -- Ngày tạo
        CURRENT_DATE, -- Ngày cập nhật
        CURRENT_DATE, -- Ngày sử dụng (NULL nếu chưa sử dụng)
        1, -- Người tạo
        1, -- Người cập nhật
        1, -- Người sử dụng (NULL nếu chưa sử dụng)
        FALSE -- Không bị xóa
       );

INSERT INTO discounts.voucher (code,
                               id_discount,
                               create_at,
                               update_at,
                               used_at,
                               create_by,
                               update_by,
                               used_by,
                               deleted)
VALUES (uuid_generate_v4(), -- Tạo UUID mới
        1, -- ID giảm giá liên kết với bảng discounts.discount (phải tồn tại)
        CURRENT_DATE, -- Ngày tạo
        CURRENT_DATE, -- Ngày cập nhật
        CURRENT_DATE, -- Ngày sử dụng
        1, -- Người tạo
        1, -- Người cập nhật
        1, -- Người sử dụng
        FALSE -- Không bị xóa
       );