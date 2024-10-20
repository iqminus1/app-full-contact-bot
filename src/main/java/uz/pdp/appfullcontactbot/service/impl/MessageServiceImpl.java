package uz.pdp.appfullcontactbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.pdp.appfullcontactbot.enums.LangFields;
import uz.pdp.appfullcontactbot.enums.State;
import uz.pdp.appfullcontactbot.model.Card;
import uz.pdp.appfullcontactbot.model.Transaction;
import uz.pdp.appfullcontactbot.model.User;
import uz.pdp.appfullcontactbot.repository.CardRepository;
import uz.pdp.appfullcontactbot.repository.TransactionRepository;
import uz.pdp.appfullcontactbot.repository.UserRepository;
import uz.pdp.appfullcontactbot.service.ButtonService;
import uz.pdp.appfullcontactbot.service.LangService;
import uz.pdp.appfullcontactbot.service.MessageService;
import uz.pdp.appfullcontactbot.service.telegram.Sender;
import uz.pdp.appfullcontactbot.utils.AppConstants;
import uz.pdp.appfullcontactbot.utils.CommonUtils;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final CommonUtils commonUtils;
    private final LangService langService;
    private final ButtonService buttonService;
    private final Sender sender;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final DateTimeFormatter formatter;
    private final CardRepository cardRepository;

    @Override
    public void process(Message message) {
        if (message.getChat().getType().equals("private")) {
            if (message.hasText()) {
                String text = message.getText();
                Long userId = message.getFrom().getId();
                User user = commonUtils.getUser(userId);

                if (!user.isAgreed()) {
                    oferta(userId);
                    return;
                } else if (user.getContactNumber() == null) {
                    commonUtils.setState(userId, State.SENDING_CONTACT_NUMBER);
                    sender.sendMessage(userId, langService.getMessage(LangFields.SEND_CONTACT_TEXT, userId), buttonService.requestContact(userId));
                    return;
                } else if (text.equals(AppConstants.START)) {
                    start(userId);
                    return;
                }
                switch (commonUtils.getState(userId)) {
                    case START -> {
                        if (text.equals(langService.getMessage(LangFields.BUTTON_CONTACT_TO_ADMIN_TEXT, userId))) {
                            sendContactAdmin(userId);
                        } else if (text.equals(langService.getMessage(LangFields.BUTTON_PAYMENT_HISTORY_TEXT, userId))) {
                            sendUserPaymentHistory(userId);
                        } else if (text.equals(langService.getMessage(LangFields.BUTTON_MY_CARDS_LIST_TEXT, userId))) {
                            sendUsersCards(userId);
                        } else if (text.equals(langService.getMessage(LangFields.START_PAYMENT_TEXT, userId))) {
                            startStoppedPayment(userId);
                        } else
                            sender.sendMessage(userId, langService.getMessage(LangFields.USE_BUTTONS, userId));
                    }
                    case PAYMENT_HISTORY -> {
                        if (text.equals(langService.getMessage(LangFields.BACK_TEXT, userId))) {
                            start(userId);
                        } else if (text.equals(langService.getMessage(LangFields.GET_LINK_TEXT, userId))) {
                            sender.sendLink(userId, buttonService.start(userId));
                            commonUtils.setState(userId, State.START);
                        } else if (text.equals(langService.getMessage(LangFields.STOP_PAYMENT_TEXT, userId))) {
                            user.setPayment(false);
                            user.setState(State.START);
                            userRepository.save(user);
                            sender.sendMessage(userId, langService.getMessage(LangFields.PAYMENT_STOPPED_TEXT, userId), buttonService.start(userId));
                        } else
                            sender.sendMessage(userId, langService.getMessage(LangFields.USE_BUTTONS, userId));
                    }
                }
            } else if (message.hasContact()) {
                if (commonUtils.getState(message.getFrom().getId()).equals(State.SENDING_CONTACT_NUMBER))
                    checkContact(message);
            }
        }
    }

    private void startStoppedPayment(Long userId) {
        User user = commonUtils.getUser(userId);
        if (!user.isHasCard()) {
            sender.sendMessageWithMarkdown(userId, langService.getMessage(LangFields.HAS_NOT_ANY_CARD_TEXT, userId), null);
            return;
        }
        user.setPayment(true);
        userRepository.save(user);
        sender.sendMessage(userId, langService.getMessage(LangFields.PAYMENT_STARTED_TEXT, userId), buttonService.start(userId));
    }

    private void sendUsersCards(Long userId) {
        User user = commonUtils.getUser(userId);
        if (!user.isHasCard()) {
            return;
        }
        List<Card> cards = cardRepository.findAllByUserId(userId);
        cards.sort(Comparator.comparing(Card::isMain));
        StringBuilder sb = new StringBuilder();
        sb.append(langService.getMessage(LangFields.CARD_INFO_TEXT, userId));
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.isMain())
                sb.append(langService.getMessage(LangFields.MAIN_CARD_TEXT, userId));
            sb
                    .append(i + 1)
                    .append(". ")
                    .append(card.getPan())
                    .append("\n\n");
        }
        commonUtils.setState(userId,State.USERS_CARDS_LIST);
        int messageId = sender.sendMessage(userId, langService.getMessage(LangFields.PLEASE_WAIT_TEXT, userId), buttonService.withString(List.of(langService.getMessage(LangFields.BACK_TEXT, userId))));
        sender.editMessage(userId,messageId, sb.toString(),buttonService.usersCardsList(userId,cards));
    }

    private void sendUserPaymentHistory(Long userId) {
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);
        if (transactions.isEmpty()) {
            sender.sendMessage(userId, langService.getMessage(LangFields.EMPTY_PAYMENT_HISTORY_TEXT, userId));
            return;
        }
        transactions.sort(Comparator.comparing(Transaction::getPayAt).reversed());
        StringBuilder sb = new StringBuilder();
        sb.append(langService.getMessage(LangFields.LIST_PAYMENT_HISTORY_TEXT, userId));
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            sb
                    .append(transactions.size() - i)
                    .append(". ")
                    .append(langService.getMessage(LangFields.CARD_PAN_TEXT, userId))
                    .append(" ")
                    .append(transaction.getPan())
                    .append("\n")
                    .append(langService.getMessage(LangFields.DATE_TEXT, userId))
                    .append(" ")
                    .append(transaction.getPayAt().format(formatter))
                    .append("\n")
                    .append(langService.getMessage(LangFields.AMOUNT_TEXT, userId))
                    .append("   ")
                    .append(transaction.getAmount())
                    .append("\n\n");
        }
        commonUtils.setState(userId, State.PAYMENT_HISTORY);
        sender.sendMessage(userId, sb.toString(), buttonService.paymentHistory(userId));
    }

    private void sendContactAdmin(Long userId) {
        String text = langService.getMessage(LangFields.SUPPORT_TEXT, userId);
        sender.sendMessage(userId, text);
    }

    private void oferta(Long userId) {
        String message = langService.getMessage(LangFields.OFERTA_TEXT, userId);
        InlineKeyboardMarkup button = buttonService.ofertaButton(userId);
        sender.sendMessageWithMarkdown(userId, message, button);
    }

    private void checkContact(Message message) {
        Long userId = message.getFrom().getId();
        String userLang = commonUtils.getLang(userId);
        if (message.getContact().getUserId().equals(message.getChat().getId())) {
            String phoneNumber = message.getContact().getPhoneNumber();
            User user = commonUtils.getUser(userId);
            user.setContactNumber(phoneNumber);
            user.setState(State.START);
            userRepository.save(user);
            start(userId);
            return;
        }
        sender.sendMessage(userId, langService.getMessage(LangFields.SEND_YOUR_PHONE_NUMBER_TEXT, userLang), buttonService.requestContact(userId));

    }

    private void start(Long userId) {
        commonUtils.setState(userId, State.START);
        sender.sendPhoto(userId, langService.getMessage(LangFields.HELLO, userId), AppConstants.PHOTO_PATH, buttonService.start(userId));
    }
}
