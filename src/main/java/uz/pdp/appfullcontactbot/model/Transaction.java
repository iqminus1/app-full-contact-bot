package uz.pdp.appfullcontactbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import uz.pdp.appfullcontactbot.dto.response.ApplyResponse;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String successTransId;

    private String transId;

    private Long userId;

    private Long amount;

    private LocalDateTime payAt;

    private String pan;

    public Transaction(ApplyResponse applyResponse,String pan) {
        this.amount = applyResponse.getAmount() / 100;
        this.userId = applyResponse.getUserId();
        this.transId = applyResponse.getTransId();
        this.successTransId = applyResponse.getSuccessTransId();
        this.payAt = LocalDateTime.now();
        this.pan = pan;
    }
}
