package uz.pdp.appfullcontactbot.service;

import uz.pdp.appfullcontactbot.dto.request.*;
import uz.pdp.appfullcontactbot.dto.response.*;

public interface AtmosService {
    String getToken();

    CardBindingInitResponse initializeCardBinding(CardBindingInitRequest request);

    CardBindingConfirmResponse confirmCardBinding(CardBindingConfirmRequest request);

    TransactionResponse createTransaction(TransactionRequest request);

    PreApplyResponse preApplyPayment(PreApplyRequest request);

    ApplyResponse applyPayment(ApplyRequest request);

    ApplyResponse autoPayment(Long userId);

    CardRemovalResponse removeCard(CardRequest request);
}
