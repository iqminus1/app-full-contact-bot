package uz.pdp.appfullcontactbot.service;

import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;

public interface MyChatMemberService {
    void process(ChatMemberUpdated myChatMember);
}
