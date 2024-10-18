package uz.pdp.appfullcontactbot.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackService {
    void process(CallbackQuery callbackQuery);
}
