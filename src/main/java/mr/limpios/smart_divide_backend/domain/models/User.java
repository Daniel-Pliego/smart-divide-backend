package mr.limpios.smart_divide_backend.domain.models;

import java.util.List;

public record User(String id, String name, String email, String password, String photUrl, List<Card> cards) {
}
