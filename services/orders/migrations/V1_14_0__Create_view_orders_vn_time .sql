create view orders_vn_time as
select
    *,
    (created_at + INTERVAL '7 hour') AS vn_created_at,
    (updated_at + INTERVAL '7 hour') AS vn_updated_at,
    (success_at + INTERVAL '7 hour') AS vn_success_at
from orders.order
