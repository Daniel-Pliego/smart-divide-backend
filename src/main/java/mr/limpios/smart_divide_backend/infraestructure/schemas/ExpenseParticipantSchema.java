package mr.limpios.smart_divide_backend.infraestructure.schemas;

import java.math.BigDecimal;

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
@Entity(name = "expense_participant")
public class ExpenseParticipantSchema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payer_id", nullable = false)
    private UserSchema payer;

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "must_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal mustPaid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private ExpenseSchema expense;
}
