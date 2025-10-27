package mr.limpios.smart_divide_backend.infraestructure.schemas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "card")
public class CardSchema {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, length = 4)
  private String lastDigits;

  @Column(nullable = false)
  private String brand;

  @Column(nullable = false, length = 2)
  private String expMonth;

  @Column(nullable = false, length = 2)
  private String expYear;

  @Column(nullable = false)
  private String token;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserSchema user;
}
