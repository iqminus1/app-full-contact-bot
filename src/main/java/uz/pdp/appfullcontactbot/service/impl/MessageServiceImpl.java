package uz.pdp.appfullcontactbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.pdp.appfullcontactbot.enums.LangFields;
import uz.pdp.appfullcontactbot.enums.State;
import uz.pdp.appfullcontactbot.model.User;
import uz.pdp.appfullcontactbot.repository.UserRepository;
import uz.pdp.appfullcontactbot.service.ButtonService;
import uz.pdp.appfullcontactbot.service.LangService;
import uz.pdp.appfullcontactbot.service.MessageService;
import uz.pdp.appfullcontactbot.service.telegram.Sender;
import uz.pdp.appfullcontactbot.utils.AppConstants;
import uz.pdp.appfullcontactbot.utils.CommonUtils;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final CommonUtils commonUtils;
    private final LangService langService;
    private final ButtonService buttonService;
    private final Sender sender;
    private final UserRepository userRepository;

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

                }
            } else if (message.hasContact()) {
                if (commonUtils.getState(message.getFrom().getId()).equals(State.SENDING_CONTACT_NUMBER))
                    checkContact(message);
            }
        }
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
