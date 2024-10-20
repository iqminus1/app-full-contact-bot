package uz.pdp.appfullcontactbot.scheduled;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.pdp.appfullcontactbot.dto.response.ApplyResponse;
import uz.pdp.appfullcontactbot.model.Transaction;
import uz.pdp.appfullcontactbot.repository.CardRepository;
import uz.pdp.appfullcontactbot.repository.TransactionRepository;
import uz.pdp.appfullcontactbot.service.AtmosService;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ScheduledProcess {
    private final CardRepository cardRepository;
    private final AtmosService atmosService;
    private final TransactionRepository transactionRepository;
//
//    @Async
//    public void rememberPayment() {
//        LocalDateTime now = LocalDateTime.now();
//        LocalDate localDate = now.toLocalDate();
//        List<User> users = userRepository.findAllBySubscribedAndSubscriptionEndTimeIsBetween(true, now, LocalDateTime.now().plusDays(4));
//        for (User user : users) {
//            long day = ChronoUnit.DAYS.between(localDate, user.getSubscriptionEndTime().toLocalDate());
//            if (day == 0) {
//                continue;
//            }
//            if (user.getCardToken() != null)
//                sender.sendMessage(user.getId(), langService.getMessage(LangFields.REMEMBER_PAYMENT_TEXT, user.getId()).formatted(day));
//            else
//                sender.sendMessage(user.getId(), langService.getMessage(LangFields.REMEMBER_PAYMENT_NO_CARD_TEXT, user.getId()).formatted(day), buttonService.start(user.getId()));
//
//        }
//    }
//
//    @Scheduled(cron = "0 0 3 * * ?")
////    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
//    public void getPayment() {
//        //Save qivolib userlani keyin paymant yechiladi.
//        //State tudum sudmlari o`zgarib ketmasligi uchun.
//        commonUtils.saveUsers();
//        List<Group> groups = groupRepository.findAll();
//        LocalDateTime localDateTime = LocalDateTime.now().minusDays(4);
//        rememberPayment();
//        if (groups.size() == 1) {
//            Long groupId = groups.get(0).getGroupId();
//            List<User> users = userRepository.findAllBySubscribedAndSubscriptionEndTimeIsBefore(true, LocalDateTime.now());
//            for (User user : users) {
//                Long userId = user.getId();
//                if (!sender.checkChatMember(userId, groupId)) {
//                    kickUserAndSendMessage(user, groupId, LangFields.NOT_CHAT_MEMBER_TEXT);
//                } else if (user.getCardToken() == null) {
//                    kickUserAndSendMessage(user, groupId, LangFields.REMOVED_CARD_NUMBER_TEXT);
//                } else if (!user.isPayment()) {
//                    kickUserAndSendMessage(user, groupId, LangFields.STOPPED_PAYMENT_END_ORDER_TEXT);
//                } else {
//                    if (localDateTime.isAfter(user.getSubscriptionEndTime()))
//                        kickUserAndSendMessage(user, groupId, LangFields.DONT_PAY_WITHIN_DAYS_TEXT);
//                    else
//                        withdrawMoney(user);
//                }
//            }
//        }
//    }
//
//    private void withdrawMoney(User user) {
//        ApplyResponse applyResponse = atmosService.autoPayment(user.getId());
//        if (applyResponse.getErrorMessage() != null) {
//            sender.sendMessage(user.getId(), applyResponse.getErrorMessage());
//            return;
//        }
//        transactionRepository.save(new Transaction(applyResponse));
//        AppConstants.setSubscriptionTime(user);
//        userRepository.save(user);
//        sender.sendMessage(user.getId(), langService.getMessage(LangFields.YOU_PAID_TEXT, user.getId()));
//    }
//
//    @Async
//    void kickUserAndSendMessage(User user, Long groupId, LangFields field) {
//        sender.kickChatMember(user.getId(), groupId);
//        user.setSubscribed(false);
//        userRepository.save(user);
//        sender.sendMessage(user.getId(), langService.getMessage(field, user.getId()));
//
//    }

//    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
//    public void dasfddas() {
//        ApplyResponse applyResponse = atmosService.autoPayment(5182943798L);
//        transactionRepository.save(new Transaction(applyResponse, "860033******9390"));
//    }
}