package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageableRequest {
    private int page = 0;
    private int size = 20;
    private Sort.Direction sort = Sort.Direction.ASC;
    private String sortBy;

    public Pageable toPageable() {
        if(sortBy == null){
            return PageRequest.of(page,size);
        }
        return PageRequest.of(Math.max(page,0),size, Sort.by(sort,sortBy));
    }
}
