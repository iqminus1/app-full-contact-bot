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
import uz.pdp.appfullcontactbot.model.User;
import uz.pdp.appfullcontactbot.service.ButtonService;
import uz.pdp.appfullcontactbot.service.LangService;
import uz.pdp.appfullcontactbot.utils.AppConstants;
import uz.pdp.appfullcontactbot.utils.CommonUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ButtonServiceImpl implements ButtonService {
    private final LangService langService;
    private final CommonUtils commonUtils;

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
        List<String> strings = new LinkedList<>();
        User user = commonUtils.getUser(userId);
        if (user.isHasCard()) {
            //TODO: sharoitga qarab
        } else {
            strings.add(langService.getMessage(LangFields.ACTIVATE_SUBSCRIPTION, userId));
        }

        strings.add(langService.getMessage(LangFields.CONTACT_TO_ADMIN_TEXT, userId));
        return withString(strings);
    }

    @Override
    public ReplyKeyboard requestContact(Long userId) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        //contact req
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton request = new KeyboardButton(langService.getMessage(LangFields.REQUEST_CONTACT_TEXT, userId));
        request.setRequestContact(true);
        row1.add(request);

        rows.add(row1);
        markup.setKeyboard(rows);
        return markup;
    }

    @Override
    public ReplyKeyboard paymentMethods(Long userId) {
        List<String> strings = new ArrayList<>();
        return withString(strings);
    }

    @Override
    public InlineKeyboardMarkup ofertaButton(Long userId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton link = new InlineKeyboardButton();
        link.setUrl(AppConstants.OFERTA_LINK);
        link.setText(langService.getMessage(LangFields.OFERTA_LINK_TEXT, userId));

        InlineKeyboardButton iAgree = new InlineKeyboardButton();
        iAgree.setText(langService.getMessage(LangFields.OFERTA_AGREE_TEXT, userId));
        iAgree.setCallbackData(AppConstants.OFERTA_I_AGREE_DATA);
        markup.setKeyboard(List.of(List.of(link), List.of(iAgree)));
        return markup;
    }

    @Override
    public ReplyKeyboard withWebApp(Long userId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(langService.getMessage(LangFields.WEB_APP_BUTTON, userId));
        inlineKeyboardButton.setWebApp(new WebAppInfo(AppConstants.WEB_APP_LINK + userId));
        row.add(inlineKeyboardButton);
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }
}
