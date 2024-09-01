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
        '2024-08-01', -- Ngày tạo
        NULL, -- Ngày cập nhật (NULL nếu chưa cập nhật)
        NULL, -- Ngày sử dụng (NULL nếu chưa sử dụng)
        'admin', -- Người tạo
        NULL, -- Người cập nhật (NULL nếu chưa cập nhật)
        NULL, -- Người sử dụng (NULL nếu chưa sử dụng)
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
        '2024-09-01', -- Ngày tạo
        '2024-09-05', -- Ngày cập nhật
        NULL, -- Ngày sử dụng (NULL nếu chưa sử dụng)
        'admin', -- Người tạo
        'admin', -- Người cập nhật
        NULL, -- Người sử dụng (NULL nếu chưa sử dụng)
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
        '2024-07-15', -- Ngày tạo
        '2024-08-01', -- Ngày cập nhật
        '2024-08-10', -- Ngày sử dụng
        'user1', -- Người tạo
        'user1', -- Người cập nhật
        'user2', -- Người sử dụng
        FALSE -- Không bị xóa
       );