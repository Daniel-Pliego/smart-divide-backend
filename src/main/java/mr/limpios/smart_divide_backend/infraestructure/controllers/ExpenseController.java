package mr.limpios.smart_divide_backend.infraestructure.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.aplication.services.ExpenseService;
import mr.limpios.smart_divide_backend.infraestructure.dto.AddExpenseDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.ExpenseResumeDTO;
import mr.limpios.smart_divide_backend.infraestructure.dto.WrapperResponse;

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
            @RequestBody AddExpenseDTO addExpenseDTO) {
        ExpenseResumeDTO expenseResumeDTO = expenseService.addExpense(addExpenseDTO, userId, groupId);
        return new ResponseEntity<>(
                new WrapperResponse<>(true, "Expense created successfully", expenseResumeDTO),
                HttpStatus.CREATED);

    }
}
