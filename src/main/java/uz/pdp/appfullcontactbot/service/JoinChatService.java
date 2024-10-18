package uz.pdp.appfullcontactbot.service;

import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;

public interface JoinChatService {
    void process(ChatJoinRequest chatJoinRequest);
}
