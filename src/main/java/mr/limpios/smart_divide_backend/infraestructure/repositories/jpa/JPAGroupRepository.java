package mr.limpios.smart_divide_backend.infraestructure.repositories.jpa;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupSchema;

@Repository
public interface JPAGroupRepository extends JpaRepository<GroupSchema, String> {

    Optional<Set<GroupSchema>> findByMembers_Id(String userId);
}
