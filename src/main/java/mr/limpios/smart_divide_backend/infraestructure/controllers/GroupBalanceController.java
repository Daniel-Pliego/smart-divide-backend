package mr.limpios.smart_divide_backend.infraestructure.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.aplication.services.ExpenseGroupBalanceService;
import mr.limpios.smart_divide_backend.domain.dto.GetGroupBalancesDTO;
import mr.limpios.smart_divide_backend.domain.dto.WrapperResponse;

@RestController
@CrossOrigin(maxAge = 3600, methods = { RequestMethod.OPTIONS, RequestMethod.GET }, origins = {
        "*" })
@Tag(name = "Group Balances", description = "Endpoints for managing group balances")
public class GroupBalanceController {
    private final ExpenseGroupBalanceService expenseGroupBalanceService;

    public GroupBalanceController(ExpenseGroupBalanceService expenseGroupBalanceService) {
        this.expenseGroupBalanceService = expenseGroupBalanceService;
    }

    @Operation(summary = "Get all balances by group")
    @GetMapping("groups/{groupId}/balances")
    public ResponseEntity<WrapperResponse<GetGroupBalancesDTO>> getAllBalancesByGroup(@PathVariable String groupId) {
        GetGroupBalancesDTO balances = expenseGroupBalanceService.getAllBalancesByGroup(groupId);
        return ResponseEntity.ok(
                new WrapperResponse<>(true, "Balances retrieved successfully", balances));
    }
}
