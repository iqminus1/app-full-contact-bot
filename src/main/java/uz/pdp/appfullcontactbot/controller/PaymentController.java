//package uz.pdp.appfullcontactbot.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import uz.pdp.appfullcontactbot.dto.request.CardBindingConfirmRequest;
//import uz.pdp.appfullcontactbot.dto.request.CardBindingInitRequest;
//import uz.pdp.appfullcontactbot.dto.response.ApplyResponse;
//import uz.pdp.appfullcontactbot.dto.response.CardBindingConfirmResponse;
//import uz.pdp.appfullcontactbot.dto.response.CardBindingInitResponse;
//import uz.pdp.appfullcontactbot.enums.LangFields;
//import uz.pdp.appfullcontactbot.enums.PaymentMethod;
//import uz.pdp.appfullcontactbot.enums.State;
//import uz.pdp.appfullcontactbot.model.Transaction;
//import uz.pdp.appfullcontactbot.model.User;
//import uz.pdp.appfullcontactbot.repository.TransactionRepository;
//import uz.pdp.appfullcontactbot.repository.UserRepository;
//import uz.pdp.appfullcontactbot.service.AtmosService;
//import uz.pdp.appfullcontactbot.service.ButtonService;
//import uz.pdp.appfullcontactbot.service.LangService;
//import uz.pdp.appfullcontactbot.service.telegram.Sender;
//import uz.pdp.appfullcontactbot.utils.AppConstants;
//import uz.pdp.appfullcontactbot.utils.CommonUtils;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api")
//public class PaymentController {
//
//    private final AtmosService atmosService;
//    private final CommonUtils commonUtils;
//    private final UserRepository userRepository;
//    private final Sender sender;
//    private final LangService langService;
//    private final TransactionRepository transactionRepository;
//    private final ButtonService buttonService;
//
//    @PostMapping("/initializeCardBinding")
//    public CardBindingInitResponse initializeCardBinding(@RequestBody CardBindingInitRequest request, @RequestParam Long userId) {
//        String cardNumber = request.getCardNumber();
//
//        if (!cardNumber.matches("\\d{16}"))
//            return CardBindingInitResponse.builder().errorCode(AppConstants.ERROR_TEXT).errorMessage("card excetion").build();
//
//
//        String expiry = request.getExpiry();
//        if (!expiry.matches("^(0[1-9]|1[0-2])/(\\d{2}$)"))
//            return CardBindingInitResponse.builder().errorCode(AppConstants.ERROR_TEXT).errorMessage("expire exception").build();
//
//        if (userRepository.existsByCardNumber(cardNumber))
//            return CardBindingInitResponse.builder().errorCode(AppConstants.ERROR_TEXT).errorMessage("card number exists by another user").build();
//
//        User user = commonUtils.getUser(userId);
//
//        String str = expiry.substring(3) + expiry.substring(0, 2);
//        CardBindingInitResponse cardBindingInitResponse = atmosService.initializeCardBinding(new CardBindingInitRequest(cardNumber, str));
//        if (cardBindingInitResponse.getTransactionId() != null) {
//            commonUtils.setState(userId, State.SENDING_CARD_CODE);
//            user.setTransactionId(cardBindingInitResponse.getTransactionId());
//            user.setCardPhone(cardBindingInitResponse.getPhone());
//
//            return cardBindingInitResponse;
//        }
//        cardBindingInitResponse.setCardHolder(null);
//        cardBindingInitResponse.setCardToken(null);
//        cardBindingInitResponse.setBalance(null);
//        cardBindingInitResponse.setCardId(null);
//        cardBindingInitResponse.setPan(null);
//        return cardBindingInitResponse;
//    }
//
//    @PostMapping("/confirmCardBinding")
//    public CardBindingConfirmResponse confirmCardBinding(@RequestBody CardBindingConfirmRequest request, @RequestParam Long userId) {
//        CardBindingConfirmResponse cardBindingConfirmResponse = atmosService.confirmCardBinding(request);
//        User user = commonUtils.getUser(userId);
//
//        if (cardBindingConfirmResponse.getErrorCode() == null) {
//            user.setCardToken(cardBindingConfirmResponse.getCardToken());
//            user.setCardPhone(cardBindingConfirmResponse.getPhone());
//            ApplyResponse applyResponse = atmosService.autoPayment(userId);
//            user.setMethod(PaymentMethod.PAYMENT);
//            if (applyResponse.getErrorMessage() == null) {
//                AppConstants.setSubscriptionTime(user);
//                userRepository.save(user);
//                transactionRepository.save(new Transaction(applyResponse));
//                sender.sendLink(userId);
//            } else
//                sender.sendMessage(userId, langService.getMessage(LangFields.EXCEPTION_ATMOS_TEXT, userId).formatted(applyResponse.getErrorMessage(), buttonService.start(userId)));
//        } else
//            sender.sendMessage(userId, langService.getMessage(LangFields.EXCEPTION_ATMOS_TEXT, userId).formatted(cardBindingConfirmResponse.getErrorMessage(), buttonService.start(userId)));
//
//        cardBindingConfirmResponse.setCardHolder(null);
//        cardBindingConfirmResponse.setCardToken(null);
//        cardBindingConfirmResponse.setBalance(null);
//        cardBindingConfirmResponse.setPan(null);
//        return cardBindingConfirmResponse;
//    }
//
//}
