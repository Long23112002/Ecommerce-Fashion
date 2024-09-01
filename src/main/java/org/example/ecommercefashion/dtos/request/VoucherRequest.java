package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.entities.Voucher;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherRequest {

    private Timestamp createAt;

    private Timestamp updateAt;

    private Timestamp usedAt;

    private String createBy;

    private String updateBy;

    private String usedBy;

    private Boolean deleted ;

    private Long IdDiscount;

    public Voucher toEntity(Voucher voucher){
//        voucher.setUsedAt();
//        voucher.setCreateBy();
//        voucher.setUpdateBy();
//        voucher.setUsedBy();
        voucher.setDeleted(this.getDeleted() != null ? this.getDeleted() : false);
        return voucher;
    }
}
