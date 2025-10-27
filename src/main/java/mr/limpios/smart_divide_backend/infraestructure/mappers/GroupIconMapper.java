package mr.limpios.smart_divide_backend.infraestructure.mappers;

import mr.limpios.smart_divide_backend.domain.models.GroupIcon;
import mr.limpios.smart_divide_backend.infraestructure.schemas.GroupIconSchema;

public class GroupIconMapper {
  private GroupIconMapper() {}

  public static GroupIconSchema toSchema(GroupIcon groupIcon) {
    return new GroupIconSchema(groupIcon.id(), groupIcon.url());
  }

  public static GroupIcon toModel(GroupIconSchema groupIconSchema) {
    return new GroupIcon(groupIconSchema.getId(), groupIconSchema.getUrl());
  }
}
