package mr.limpios.smart_divide_backend.domain.models;

import java.util.List;

public record User(String id, String name, String lastName, String email, String password, String photUrl, Boolean isVerified, List<Card> cards) {
}
