INSERT INTO discounts.discount (code,
                                name,
                                condition,
                                type,
                                value,
                                max_value,
                                start_date,
                                end_date,
                                discount_status,
                                create_at,
                                update_at,
                                create_by,
                                update_by,
                                deleted)
VALUES (uuid_generate_v4(), -- Tạo UUID mới
        'giam gia soc',
        '{"productDetailId": 1,"productId":1,"categoryId":1,"brandId":1}',
        'PERCENTAGE',
        15.0,
        200.0,
        '2024-09-01',
        '2024-12-31',
        'ACTIVE',
        CURRENT_DATE,
        CURRENT_DATE,
        1,
        1,
        FALSE);

INSERT INTO discounts.discount (code,
                                name,
                                condition,
                                type,
                                value,
                                max_value,
                                start_date,
                                end_date,
                                discount_status,
                                create_at,
                                update_at,
                                create_by,
                                update_by,
                                deleted)
VALUES (uuid_generate_v4(),
        'giam gia soc',
        '{"productDetailId": 1,"productId":1,"categoryId":1,"brandId":1}',
        'FIXED_AMOUNT',
        50.0,
        100.0,
        '2024-08-01',
        '2024-11-01',
        'ACTIVE',
        CURRENT_DATE,
        CURRENT_DATE,
        1,
        1,
        FALSE
       );
