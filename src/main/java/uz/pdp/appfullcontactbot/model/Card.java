package uz.pdp.appfullcontactbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity
@Builder
public class Card {
    @Id
    private Long id;

    private String token;

    private String phone;

    private String pan;

    private Long userId;

    private boolean main;

    private String transactionId;
}
