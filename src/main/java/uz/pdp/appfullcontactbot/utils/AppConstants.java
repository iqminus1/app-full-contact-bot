package uz.pdp.appfullcontactbot.utils;

import org.telegram.telegrambots.meta.api.objects.Chat;
import uz.pdp.appfullcontactbot.model.User;

import java.time.LocalDateTime;

public interface AppConstants {
    String BOT_TOKEN = "7637751901:AAH-CWlyqsGZ_OXt5Cwq_dIkDjV7i3tI0RU";
    String BOT_USERNAME = "SelfDevelopment_uz_bot";
    String START = "/start";
    Long PRICE = 1000L;
    String OFERTA_I_AGREE_DATA = "iAgree";

    //Auth Atmos
    String CLIENT_ID = "XGxjoucAcCffzaH_k4YG4TKfnXEa";
    String CLIENT_SECRET = "Cu83htpzy_e2xvErKM3DN54iaEAa";
    Integer STORE_ID = 7997;

    //ATMOS API URLs
    String ATMOS_AUTH_URL = "https://partner.atmos.uz/token";
    String ATMOS_BIND_CARD_INIT_URL = "https://partner.atmos.uz/partner/bind-card/init";
    String ATMOS_BIND_CARD_CONFIRM_URL = "https://partner.atmos.uz/partner/bind-card/confirm";
    String ATMOS_CREATE_TRANSACTION_URL = "https://partner.atmos.uz/merchant/pay/create";
    String ATMOS_PRE_APPLY_URL = "https://partner.atmos.uz/merchant/pay/pre-apply";
    String ATMOS_APPLY_URL = "https://partner.atmos.uz/merchant/pay/apply-ofd";
    String ATMOS_REMOVE_CARD_URL = "https://partner.atmos.uz/partner/remove-card";

    String WEB_APP_LINK = "https://web-page-one-theta.vercel.app/";
    String OFERTA_LINK = "https://behad.uz/marifat/oferta.pdf";

    String ERROR_TEXT = "STPIMS-ERR-";
    int ERROR_LENGTH = ERROR_TEXT.length();
    String PHOTO_PATH = "C:\\Users\\User\\Desktop\\projects\\app-full-contact-bot\\files/banner_photo.jpg";
    String CARD_INFO_TEXT = "cardInfo:";

    static User setSubscriptionTime(User user) {
        return setSubscriptionTime(user, 1);
    }

    static User setSubscriptionTime(uz.pdp.appfullcontactbot.model.User user, Integer month) {
        if (user.getSubscriptionEndTime().isBefore(LocalDateTime.now())) {
            user.setSubscriptionEndTime(LocalDateTime.now().plusMonths(month));
        } else
            user.setSubscriptionEndTime(user.getSubscriptionEndTime().plusMonths(month));
        return user;
    }

    static String getChatToString(Chat chat) {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(chat.getId());
        if (chat.getUserName() != null) {
            sb.append(" @").append(chat.getUserName());
        }
        if (chat.getFirstName() != null) {
            sb.append(" ").append(chat.getFirstName());
        }
        return sb.toString();
    }

}
