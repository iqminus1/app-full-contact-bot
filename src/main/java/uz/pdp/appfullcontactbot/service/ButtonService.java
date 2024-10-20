package uz.pdp.appfullcontactbot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.pdp.appfullcontactbot.model.Card;

import java.util.List;
import java.util.Map;

public interface ButtonService {

    default ReplyKeyboard withString(List<String> list) {
        return withString(list, 1);
    }

    ReplyKeyboard withString(List<String> list, int rowSize);

    InlineKeyboardMarkup callbackKeyboard(List<Map<String, String>> textData);

    ReplyKeyboard start(Long userId);

    ReplyKeyboard requestContact(Long userId);


    InlineKeyboardMarkup ofertaButton(Long userId);

    ReplyKeyboard paymentHistory(Long userId);

    InlineKeyboardMarkup usersCardsList(Long userId, List<Card> cards);
}
