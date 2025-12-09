package mr.limpios.smart_divide_backend.application.dtos;

import java.util.List;

public record GetGroupBalancesDTO(
        String groupId,
        List<BalanceDetailDTO> balances
    
) {
    
}
