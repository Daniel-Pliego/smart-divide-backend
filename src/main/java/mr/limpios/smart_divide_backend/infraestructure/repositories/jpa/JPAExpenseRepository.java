package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseSchema;

@Repository
public interface JPAExpenseRepository extends JpaRepository<ExpenseSchema, String> {
  List<ExpenseSchema> findByGroupId(String groupId);
}
