package mr.limpios.smart_divide_backend.domain.dto;

import java.util.Map;

public record NotificationDTO(String title, String message, Map<String, Object> data) {
}
