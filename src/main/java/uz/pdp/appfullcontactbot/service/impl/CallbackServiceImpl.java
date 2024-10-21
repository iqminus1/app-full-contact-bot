package uz.pdp.appfullcontactbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.appfullcontactbot.enums.LangFields;
import uz.pdp.appfullcontactbot.enums.State;
import uz.pdp.appfullcontactbot.model.Card;
import uz.pdp.appfullcontactbot.model.User;
import uz.pdp.appfullcontactbot.repository.CardRepository;
import uz.pdp.appfullcontactbot.repository.UserRepository;
import uz.pdp.appfullcontactbot.service.ButtonService;
import uz.pdp.appfullcontactbot.service.CallbackService;
import uz.pdp.appfullcontactbot.service.LangService;
import uz.pdp.appfullcontactbot.service.telegram.Sender;
import uz.pdp.appfullcontactbot.utils.AppConstants;
import uz.pdp.appfullcontactbot.utils.CommonUtils;

@Service
@RequiredArgsConstructor
public class CallbackServiceImpl implements CallbackService {
    private final CommonUtils commonUtils;
    private final Sender sender;
    private final UserRepository userRepository;
    private final LangService langService;
    private final ButtonService buttonService;
    private final CardRepository cardRepository;

    @Override
    public void process(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        Long userId = callbackQuery.getFrom().getId();
        switch (commonUtils.getUser(userId).getState()) {
            case USERS_CARDS_LIST -> {
                if (data.equals(AppConstants.BACK_TO_START_DATA)) {
                    backToStart(callbackQuery);
                } else if (data.startsWith(AppConstants.CARD_INFO_TEXT)) {
                    cardInfo(callbackQuery);
                }
            }
        }
        if (data.equals(AppConstants.OFERTA_I_AGREE_DATA)) {
            setAgree(callbackQuery);
        }
    }

    private void cardInfo(CallbackQuery callbackQuery) {
        Long userId = callbackQuery.getFrom().getId();
        long cardId = Long.parseLong(callbackQuery.getData().split(":")[1]);
        Card card = cardRepository.findById(cardId).orElseThrow();
        InlineKeyboardMarkup keyboardMarkup = buttonService.cardInfo(cardId, card.isMain());
    }

    private void backToStart(CallbackQuery callbackQuery) {
        Integer messageId = callbackQuery.getMessage().getMessageId();
        Long userId = callbackQuery.getFrom().getId();
        sender.deleteMessage(userId, messageId);
        commonUtils.setState(userId, State.START);
        sender.sendPhoto(userId, langService.getMessage(LangFields.HELLO, userId), AppConstants.BANNER_PHOTO_JPG, buttonService.start(userId));
    }


    private void setAgree(CallbackQuery callbackQuery) {
        Long userId = callbackQuery.getFrom().getId();
        User user = commonUtils.getUser(userId);
        sender.deleteMessage(userId, callbackQuery.getMessage().getMessageId());
        if (user.isAgreed())
            return;
        user.setAgreed(true);
        userRepository.save(user);
        user.setState(State.SENDING_CONTACT_NUMBER);
        sender.sendMessage(userId, langService.getMessage(LangFields.SEND_CONTACT_TEXT, userId), buttonService.requestContact(userId));
    }
}
