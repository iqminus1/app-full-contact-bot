package uz.pdp.appfullcontactbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appfullcontactbot.dto.request.CardBindingConfirmRequest;
import uz.pdp.appfullcontactbot.dto.request.CardBindingInitRequest;
import uz.pdp.appfullcontactbot.dto.request.CardRequest;
import uz.pdp.appfullcontactbot.dto.response.ApplyResponse;
import uz.pdp.appfullcontactbot.dto.response.CardBindingConfirmResponse;
import uz.pdp.appfullcontactbot.dto.response.CardBindingInitResponse;
import uz.pdp.appfullcontactbot.dto.response.CardRemovalResponse;
import uz.pdp.appfullcontactbot.enums.LangFields;
import uz.pdp.appfullcontactbot.enums.State;
import uz.pdp.appfullcontactbot.model.Card;
import uz.pdp.appfullcontactbot.model.Transaction;
import uz.pdp.appfullcontactbot.model.User;
import uz.pdp.appfullcontactbot.repository.CardRepository;
import uz.pdp.appfullcontactbot.repository.TransactionRepository;
import uz.pdp.appfullcontactbot.repository.UserRepository;
import uz.pdp.appfullcontactbot.service.AtmosService;
import uz.pdp.appfullcontactbot.service.ButtonService;
import uz.pdp.appfullcontactbot.service.LangService;
import uz.pdp.appfullcontactbot.service.telegram.Sender;
import uz.pdp.appfullcontactbot.utils.AppConstants;
import uz.pdp.appfullcontactbot.utils.CommonUtils;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

    private final AtmosService atmosService;
    private final CommonUtils commonUtils;
    private final UserRepository userRepository;
    private final Sender sender;
    private final LangService langService;
    private final TransactionRepository transactionRepository;
    private final ButtonService buttonService;
    private final CardRepository cardRepository;

    @PostMapping("/initializeCardBinding")
    public CardBindingInitResponse initializeCardBinding(@RequestBody CardBindingInitRequest request, @RequestParam Long userId) {
        String cardNumber = request.getCardNumber();

        if (!cardNumber.matches("\\d{16}"))
            return CardBindingInitResponse.builder().errorCode(AppConstants.ERROR_TEXT).errorMessage("card excetion").build();

        String expiry = request.getExpiry();
        if (!expiry.matches("^(0[1-9]|1[0-2])/(\\d{2}$)"))
            return CardBindingInitResponse.builder().errorCode(AppConstants.ERROR_TEXT).errorMessage("expire exception").build();

        String str = expiry.substring(3) + expiry.substring(0, 2);
        CardBindingInitResponse response = atmosService.initializeCardBinding(new CardBindingInitRequest(cardNumber, str));
        if (response.getTransactionId() != null) {
            commonUtils.addCard(response.getTransactionId(), Card.builder().userId(userId).transactionId(response.getTransactionId()).build());
            return response;
        }
        response.setCardHolder(null);
        response.setCardToken(null);
        response.setBalance(null);
        response.setCardId(null);
        response.setPan(null);
        return response;
    }

    @PostMapping("/confirmCardBinding")
    public CardBindingConfirmResponse confirmCardBinding(@RequestBody CardBindingConfirmRequest request, @RequestParam Long userId) {
        CardBindingConfirmResponse response = atmosService.confirmCardBinding(request);

        getPayment(request, userId, response);

        response.setCardHolder(null);
        response.setCardToken(null);
        response.setBalance(null);
        response.setPan(null);
        return response;
    }

    @PostMapping("/removeCard")
    public CardRemovalResponse removeCard(@RequestBody CardRequest cardRequest) {
        return atmosService.removeCard(cardRequest);
    }

    @Async
    public void getPayment(CardBindingConfirmRequest request, Long userId, CardBindingConfirmResponse response) {
        if (response.getErrorCode() == null) {
            Card card = commonUtils.getCard(request.getTransactionId());
            if (card == null)
                return;
            if (commonUtils.getUser(userId).getSubscriptionEndTime().isAfter(LocalDateTime.now())) {
                sender.sendMessage(userId, langService.getMessage(LangFields.CARD_ADDED_SUCCESSFULLY, userId));
                return;
            }

            card.setPan(response.getPan());
            if (!commonUtils.getUser(userId).isHasCard())
                card.setMain(true);
            card.setId(response.getCardId());
            card.setToken(response.getCardToken());
            card.setPhone(response.getPhone());
            cardRepository.save(card);
            commonUtils.removeCard(request.getTransactionId());
            ApplyResponse applyResponse = atmosService.autoPayment(userId);

            User user = commonUtils.getUser(userId);
            if (applyResponse.getErrorMessage() == null) {
                AppConstants.setSubscriptionTime(user);
                user.setState(State.START);
                user.setHasCard(true);
                userRepository.save(user);
                transactionRepository.save(new Transaction(applyResponse, card.getPan()));
                sender.sendLink(userId, buttonService.start(userId));
            } else
                sender.sendMessage(userId, langService.getMessage(LangFields.EXCEPTION_ATMOS_TEXT, userId).formatted(applyResponse.getErrorMessage(), buttonService.start(userId)));
        }
    }

}
