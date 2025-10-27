package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import mr.limpios.smart_divide_backend.infraestructure.schemas.ExpenseParticipantSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAExpenseParticipantRepository
    extends JpaRepository<ExpenseParticipantSchema, Integer> {
}
