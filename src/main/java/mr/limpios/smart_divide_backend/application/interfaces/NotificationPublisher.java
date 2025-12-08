package mr.limpios.smart_divide_backend.application.interfaces;

import java.util.List;
import java.util.Map;

public interface NotificationPublisher {
  void sendMulticast(List<String> userIds, String title, String body, Map<String, Object> data);
}
