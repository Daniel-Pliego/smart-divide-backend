package mr.limpios.smart_divide_backend.domain.models;

import java.util.List;

public record Group(
                String id,
                String name,
                String description,
                User owner,
                String type,
                List<User> members) {

        public boolean hasMember(String memberId) {
                return this.members.stream().anyMatch(m -> m.id().equals(memberId));
        }
}
