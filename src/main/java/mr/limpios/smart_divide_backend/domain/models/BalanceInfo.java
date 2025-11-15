package mr.limpios.smart_divide_backend.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class BalanceInfo {
    private String name;
    private BigDecimal balance;

}

