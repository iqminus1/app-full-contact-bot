package uz.pdp.appfullcontactbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import uz.pdp.appfullcontactbot.enums.Lang;
import uz.pdp.appfullcontactbot.enums.LangFields;
import uz.pdp.appfullcontactbot.service.LangService;
import uz.pdp.appfullcontactbot.utils.CommonUtils;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LangServiceImpl implements LangService {
    private final MessageSource messageSource;
    private final CommonUtils commonUtils;

    @Override
    public String getMessage(LangFields keyword, Long userId) {
        String lang = commonUtils.getLang(userId);
        try {
            return messageSource.getMessage(keyword.name(), null, new Locale(lang));
        } catch (Exception e) {
            return messageSource.getMessage(keyword.name(), null, new Locale(Lang.UZ.name()));
        }
    }

    public String getMessage(LangFields keyword, String lang) {
        try {
            return messageSource.getMessage(keyword.name(), null, new Locale(lang));
        } catch (Exception e) {
            return messageSource.getMessage(keyword.name(), null, new Locale(Lang.UZ.name()));
        }
    }

    @Override
    public Lang getLanguageEnum(String text) {
        if (text.equals(getMessage(LangFields.BUTTON_LANGUAGE_UZBEK, "uz"))) {
            return Lang.UZ;
        } else if (text.equals(getMessage(LangFields.BUTTON_LANGUAGE_UZBEKKR, "uz"))) {
            return Lang.UZKR;
        }
        return null;
    }
}
