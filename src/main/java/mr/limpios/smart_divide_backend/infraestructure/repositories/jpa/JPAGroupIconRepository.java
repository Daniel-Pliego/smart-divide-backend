package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupIconSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAGroupIconRepository extends JpaRepository<GroupIconSchema, Integer> {
}
