package uz.pdp.appfullcontactbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import uz.pdp.appfullcontactbot.enums.LangFields;
import uz.pdp.appfullcontactbot.model.Card;
import uz.pdp.appfullcontactbot.model.User;
import uz.pdp.appfullcontactbot.repository.TransactionRepository;
import uz.pdp.appfullcontactbot.service.ButtonService;
import uz.pdp.appfullcontactbot.service.LangService;
import uz.pdp.appfullcontactbot.utils.AppConstants;
import uz.pdp.appfullcontactbot.utils.CommonUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ButtonServiceImpl implements ButtonService {
    private final LangService langService;
    private final CommonUtils commonUtils;
    private final TransactionRepository transactionRepository;

    @Override
    public ReplyKeyboard withString(List<String> list, int rowSize) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        int i = 1;
        for (String text : list) {
            row.add(new KeyboardButton(text));
            if (i == rowSize) {
                rows.add(row);
                row = new KeyboardRow();
                i = 0;
            }
            i++;
        }
        markup.setKeyboard(rows);
        return markup;
    }

    @Override
    public InlineKeyboardMarkup callbackKeyboard(List<Map<String, String>> textData) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (Map<String, String> map : textData) {

            for (String text : map.keySet()) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setCallbackData(map.get(text));
                button.setText(text);
                row.add(button);
            }

            rows.add(row);
            row = new ArrayList<>();

        }
        markup.setKeyboard(rows);
        return markup;
    }


    @Override
    public ReplyKeyboard start(Long userId) {
        User user = commonUtils.getUser(userId);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        if (user.isHasCard()) {
            //|| user.getSubscriptionEndTime().isAfter(LocalDateTime.now())
            if (!user.isPayment()) {
                String start = langService.getMessage(LangFields.START_PAYMENT_TEXT, userId);
                rows.add(new KeyboardRow(List.of(new KeyboardButton(start))));
            }

            String card = langService.getMessage(LangFields.BUTTON_MY_CARDS_LIST_TEXT, userId);
            rows.add(new KeyboardRow(List.of(new KeyboardButton(card))));

        } else {
            KeyboardButton activateSubscription = new KeyboardButton();
            activateSubscription.setText(langService.getMessage(LangFields.BUTTON_ACTIVATE_SUBSCRIPTION, userId));
            activateSubscription.setWebApp(new WebAppInfo(AppConstants.WEB_APP_LINK + userId));
            rows.add(new KeyboardRow(List.of(activateSubscription)));
        }

        if (!transactionRepository.findAllByUserId(userId).isEmpty()) {
            String history = langService.getMessage(LangFields.BUTTON_PAYMENT_HISTORY_TEXT, userId);
            rows.add(new KeyboardRow(List.of(new KeyboardButton(history))));
        }

        KeyboardButton contactToAdmin = new KeyboardButton();
        contactToAdmin.setText(langService.getMessage(LangFields.BUTTON_CONTACT_TO_ADMIN_TEXT, userId));
        rows.add(new KeyboardRow(List.of(contactToAdmin)));

        markup.setKeyboard(rows);
        return markup;
    }

    @Override
    public ReplyKeyboard requestContact(Long userId) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        //contact req
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton request = new KeyboardButton(langService.getMessage(LangFields.BUTTON_REQUEST_CONTACT_TEXT, userId));
        request.setRequestContact(true);
        row1.add(request);

        rows.add(row1);
        markup.setKeyboard(rows);
        return markup;
    }

    @Override
    public InlineKeyboardMarkup ofertaButton(Long userId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton link = new InlineKeyboardButton();
        link.setUrl(AppConstants.OFERTA_LINK);
        link.setText(langService.getMessage(LangFields.BUTTON_OFERTA_LINK_TEXT, userId));

        InlineKeyboardButton iAgree = new InlineKeyboardButton();
        iAgree.setText(langService.getMessage(LangFields.BUTTON_OFERTA_AGREE_TEXT, userId));
        iAgree.setCallbackData(AppConstants.OFERTA_I_AGREE_DATA);
        markup.setKeyboard(List.of(List.of(link), List.of(iAgree)));
        return markup;
    }

    @Override
    public ReplyKeyboard paymentHistory(Long userId) {
        List<String> strings = new LinkedList<>();

        strings.add(langService.getMessage(LangFields.GET_LINK_TEXT, userId));
        if (commonUtils.getUser(userId).isPayment())
            strings.add(langService.getMessage(LangFields.STOP_PAYMENT_TEXT, userId));

        strings.add(langService.getMessage(LangFields.BACK_TEXT, userId));

        return withString(strings);
    }

    @Override
    public InlineKeyboardMarkup usersCardsList(Long userId, List<Card> cards) {
        List<Map<String, String>> list = new ArrayList<>();
        for (Card card : cards) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put(card.getPan(), AppConstants.CARD_INFO_TEXT + card.getId());
            list.add(map);
        }
        InlineKeyboardMarkup markup = callbackKeyboard(list);

        List<List<InlineKeyboardButton>> keyboard = markup.getKeyboard();
        InlineKeyboardButton button = new InlineKeyboardButton(langService.getMessage(LangFields.INLINE_ADD_CARD_TEXT, userId));
        button.setWebApp(new WebAppInfo(AppConstants.WEB_APP_LINK + userId));
        keyboard.add(List.of(button));

        InlineKeyboardButton back = new InlineKeyboardButton(langService.getMessage(LangFields.BACK_TEXT, userId));
        back.setCallbackData(AppConstants.BACK_TO_START_DATA);
        keyboard.add(List.of(back));
        markup.setKeyboard(keyboard);

        return markup;
    }

    @Override
    public InlineKeyboardMarkup cardInfo(long cardId, boolean main) {
        return null;
    }
}
