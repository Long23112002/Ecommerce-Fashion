package org.example.ecommercefashion.dtos.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductParam {
    private String keyword;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
