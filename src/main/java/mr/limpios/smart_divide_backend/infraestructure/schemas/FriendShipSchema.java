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
@Entity(name = "friendship")
public class FriendShipSchema {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "requester_id", nullable = false)
  private UserSchema requester;

  @ManyToOne(optional = false)
  @JoinColumn(name = "friend_id", nullable = false)
  private UserSchema friend;

  @Column(nullable = false)
  private Boolean confirmed;
}
