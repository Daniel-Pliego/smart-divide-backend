package mr.limpios.smart_divide_backend.domain.models;

public enum DivisionType {
  EQUAL("equal"), CUSTOM("custom"), PERCENTAGE("percentage");

  private final String type;

  DivisionType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
