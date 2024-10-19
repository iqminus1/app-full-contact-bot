package uz.pdp.appfullcontactbot.model;

import jakarta.persistence.*;
import lombok.*;
import uz.pdp.appfullcontactbot.enums.Lang;
import uz.pdp.appfullcontactbot.enums.State;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity(name = "users")
@Builder
public class User {
    @Id
    private Long id;

    private String contactNumber;

    private boolean subscribed;

    private LocalDateTime subscriptionEndTime;

    private boolean hasCard;

    private boolean agreed;

    @Enumerated(EnumType.STRING)
    private State state;

    private Lang lang;

    @Transient
    private String cardNumber;

    @Transient
    private String cardExpiry;
}
