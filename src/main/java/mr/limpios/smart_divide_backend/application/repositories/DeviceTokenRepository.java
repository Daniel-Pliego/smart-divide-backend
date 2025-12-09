package mr.limpios.smart_divide_backend.application.repositories;

import java.util.List;

import mr.limpios.smart_divide_backend.domain.models.User;

public interface DeviceTokenRepository {
  void saveToken(User user, String token);

  List<String> getTokensByUserIdIn(List<String> userIds);
}
