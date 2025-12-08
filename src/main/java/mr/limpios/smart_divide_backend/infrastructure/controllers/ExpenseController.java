package mr.limpios.smart_divide_backend.infrastructure.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseDetails.ExpenseDetailDTO;
import mr.limpios.smart_divide_backend.application.dtos.ExpenseInputDTO;
import mr.limpios.smart_divide_backend.application.services.ExpenseService;
import mr.limpios.smart_divide_backend.infrastructure.dtos.WrapperResponse;
import mr.limpios.smart_divide_backend.infrastructure.security.CustomUserDetails;

@RestController
@RequestMapping("groups/{groupId}/expense")
@CrossOrigin(maxAge = 3600,
    methods = {RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE},
    origins = {"*"})
@Tag(name = "Expense", description = "Endpoints for managing expenses")
public class ExpenseController {
  private final ExpenseService expenseService;

  public ExpenseController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  @Operation(summary = "Create a new expense")
  @PostMapping
  public ResponseEntity<WrapperResponse<Object>> addExpense(@PathVariable String groupId,
      @RequestBody ExpenseInputDTO expenseInputDTO) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    expenseService.addExpense(expenseInputDTO, userId, groupId);
    return new ResponseEntity<>(new WrapperResponse<>(true, "Expense created successfully", null),
        HttpStatus.CREATED);

  }

  @Operation(summary = "Get expense details")
  @GetMapping("{expenseId}")
  public ResponseEntity<WrapperResponse<ExpenseDetailDTO>> getExpenseDetail(
      @PathVariable String groupId, @PathVariable String expenseId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    ExpenseDetailDTO expenseDetailDTO =
        expenseService.getExpenseDetails(expenseId, userId, groupId);

    return new ResponseEntity<>(
        new WrapperResponse<>(true, "Expense details retrieved successfully", expenseDetailDTO),
        HttpStatus.OK);
  }

  @Operation(summary = "Deletes an existing expense")
  @DeleteMapping("{expenseId}")
  public ResponseEntity<Void> deleteExpense(@PathVariable String expenseId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String userId = userDetails.getUserId();

    expenseService.deleteExpense(expenseId, userId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
