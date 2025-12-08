package mr.limpios.smart_divide_backend.infrastructure.schemas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Entity(name = "payment")
public class PaymentSchema {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "from_user_id", nullable = false)
  private UserSchema fromUser;

  @ManyToOne(optional = false)
  @JoinColumn(name = "to_user_id", nullable = false)
  private UserSchema toUser;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @ManyToOne(optional = false)
  @JoinColumn(name = "group_id", nullable = false)
  private GroupSchema group;

  @Builder.Default
  @Column(nullable = false)
  private Boolean paidWithCard = false;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
