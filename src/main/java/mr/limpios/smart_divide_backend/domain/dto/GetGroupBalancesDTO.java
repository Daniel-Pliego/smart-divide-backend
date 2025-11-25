package mr.limpios.smart_divide_backend.domain.dto;

import java.util.List;

public record GetGroupBalancesDTO(
        String groupId,
        List<BalanceDetailDTO> balances
    
) {
    
}
