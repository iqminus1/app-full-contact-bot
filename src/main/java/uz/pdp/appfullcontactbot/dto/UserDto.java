package uz.pdp.appfullcontactbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String contactNumber;
    private boolean subscribed;
    private LocalDateTime subscriptionEndTime;
}
