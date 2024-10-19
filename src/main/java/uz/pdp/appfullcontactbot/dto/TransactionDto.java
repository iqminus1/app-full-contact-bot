package uz.pdp.appfullcontactbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {
    private String successTransId;

    private String transId;

    private Long userId;

    private Long amount;

    private LocalDateTime payAt;
}
