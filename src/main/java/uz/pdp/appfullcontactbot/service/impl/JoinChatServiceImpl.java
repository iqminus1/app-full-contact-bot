package uz.pdp.appfullcontactbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;
import uz.pdp.appfullcontactbot.enums.LangFields;
import uz.pdp.appfullcontactbot.model.Group;
import uz.pdp.appfullcontactbot.model.User;
import uz.pdp.appfullcontactbot.repository.GroupRepository;
import uz.pdp.appfullcontactbot.repository.UserRepository;
import uz.pdp.appfullcontactbot.service.ButtonService;
import uz.pdp.appfullcontactbot.service.JoinChatService;
import uz.pdp.appfullcontactbot.service.LangService;
import uz.pdp.appfullcontactbot.service.telegram.Sender;
import uz.pdp.appfullcontactbot.utils.CommonUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JoinChatServiceImpl implements JoinChatService {
    private final Sender sender;
    private final LangService langService;
    private final GroupRepository groupRepository;
    private final CommonUtils commonUtils;
    private final UserRepository userRepository;
    private final ButtonService buttonService;


    @Override
    public void process(ChatJoinRequest chatJoinRequest) {
        Long groupId = chatJoinRequest.getChat().getId();
        Optional<Group> groupOptional = groupRepository.findByGroupId(groupId);
        if (groupOptional.isEmpty() || groupOptional.get().getGroupId() == null) {
            return;
        }
        Long userId = chatJoinRequest.getUser().getId();
        String name = chatJoinRequest.getChat().getTitle();
        User user = commonUtils.getUser(userId);

        if (user.getSubscriptionEndTime() != null) {
            if (user.getSubscriptionEndTime().isBefore(LocalDateTime.now())) {
                sender.sendMessage(userId, langService.getMessage(LangFields.PAID_GROUP_TEXT, chatJoinRequest.getUser().getLanguageCode()).formatted(name), buttonService.ofertaButton(userId));
            } else {
                sender.acceptJoinRequest(userId, groupId);
                user.setSubscribed(true);
                userRepository.save(user);
            }
        } else {
            sender.sendMessage(userId, langService.getMessage(LangFields.PAID_GROUP_TEXT, chatJoinRequest.getUser().getLanguageCode()).formatted(name), buttonService.ofertaButton(userId));
        }
    }

}
