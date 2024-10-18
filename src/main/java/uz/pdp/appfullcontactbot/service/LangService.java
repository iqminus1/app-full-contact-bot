package uz.pdp.appfullcontactbot.service;

import uz.pdp.appfullcontactbot.enums.Lang;
import uz.pdp.appfullcontactbot.enums.LangFields;

public interface LangService {
    String getMessage(LangFields keyword, Long userId);

    String getMessage(LangFields keyword, String text);

    Lang getLanguageEnum(String text);
}
