package mr.limpios.smart_divide_backend.infraestructure.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.aplication.services.BalanceService;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseGroupBalanceDTO;
import mr.limpios.smart_divide_backend.domain.dto.WrapperResponse;

@RestController
@RequestMapping("user/{userId}/group/{groupId}")
@CrossOrigin(maxAge = 3600, methods = {}, origins = {
        "*" })
@Tag(name = "Balance", description = "Endpoints for managing balances")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Operation(summary = "Get balance by user and group")
    @GetMapping("balance")
    public ResponseEntity<WrapperResponse<ExpenseGroupBalanceDTO>> getBalanceByUserAndGroup(@PathVariable String userId,
            @PathVariable String groupId) {
                ExpenseGroupBalanceDTO balance = balanceService.getBalanceByUserAndGroup(userId, groupId);
                return new ResponseEntity<>(
                    new WrapperResponse<>(true, "Balance retrieved successfully", balance),
                    HttpStatus.OK);
            }
    
    
}
