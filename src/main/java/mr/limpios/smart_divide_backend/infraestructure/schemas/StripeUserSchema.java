package mr.limpios.smart_divide_backend.infraestructure.schemas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Entity(name = "stripe_user_info")
public class StripeUserSchema {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "stripe_account_id", unique = true)
  private String stripeAccountId;

  @Column(name = "stripe_customer_id", unique = true)
  private String stripeCustomerId;

  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private UserSchema user;
}
