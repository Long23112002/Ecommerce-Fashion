package org.example.ecommercefashion.dtos.response;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.entities.Voucher;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.example.ecommercefashion.model.Condition;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoucherResponse {

    private Long id;

    private UUID code;

    private Timestamp createAt;

    private Timestamp updateAt;

    private Timestamp usedAt;

    private UserResponse createBy;

    private UserResponse updateBy;

    private UserResponse usedBy;

    private Boolean deleted;

    private Discount discount;
}
