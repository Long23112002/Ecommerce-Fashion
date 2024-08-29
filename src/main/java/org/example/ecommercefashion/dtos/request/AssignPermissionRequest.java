package org.example.ecommercefashion.dtos.request;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignPermissionRequest {

    @NotNull(message = "Role id is required")
    private Long roleId;
    
    private List<Long> permissionIds;

}
