package mr.limpios.smart_divide_backend.infraestructure.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.aplication.services.ExpenseService;
import mr.limpios.smart_divide_backend.domain.dto.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.domain.dto.WrapperResponse;

@RestController
@RequestMapping("user/{userId}/group/{groupId}/expense")
@CrossOrigin(maxAge = 3600, methods = { RequestMethod.OPTIONS, RequestMethod.POST }, origins = {
        "*" })
@Tag(name = "Expense", description = "Endpoints for managing expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(summary = "Create a new expense")
    @PostMapping
    public ResponseEntity<WrapperResponse<Object>> AddExpense(
            @PathVariable String userId,
            @PathVariable String groupId,
            @RequestBody ExpenseInputDTO expenseInputDTO) {
        expenseService.addExpense(expenseInputDTO, userId, groupId);
        return new ResponseEntity<>(
                new WrapperResponse<>(true, "Expense created successfully", null),
                HttpStatus.CREATED);

    }

    @Operation(summary = "Deletes an existing expense")
    @DeleteMapping("{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable String expenseId) {
        expenseService.deleteExpense(expenseId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
