package uz.pdp.appfullcontactbot.service.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatInviteLink;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.appfullcontactbot.enums.LangFields;
import uz.pdp.appfullcontactbot.model.Group;
import uz.pdp.appfullcontactbot.repository.GroupRepository;
import uz.pdp.appfullcontactbot.service.LangService;
import uz.pdp.appfullcontactbot.utils.AppConstants;

import java.util.List;

@Component
public class Sender extends DefaultAbsSender {

    private final GroupRepository groupRepository;
    private final LangService langService;
    private String link;

    public Sender(GroupRepository groupRepository, LangService langService) {
        super(new DefaultBotOptions(), AppConstants.BOT_TOKEN);
        this.groupRepository = groupRepository;
        this.langService = langService;
    }

    public void sendMessage(Long userId, String text) {
        final int MAX_LENGTH = 1024;
        if (text.length() > MAX_LENGTH) {

            int startIndex = 0;

            while (startIndex < text.length() - 1) {
                int endIndex = Math.min(startIndex + MAX_LENGTH, text.length());


                int lastNewLineIndex = text.lastIndexOf("\n\n", endIndex);
                if (lastNewLineIndex != -1 && lastNewLineIndex > startIndex) {
                    endIndex = lastNewLineIndex;
                }

                String part = text.substring(startIndex, endIndex).trim();

                SendMessage message = new SendMessage(userId.toString(), part);

                try {
                    execute(message);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                startIndex = endIndex + 1;
            }
        } else {
            try {
                execute(new SendMessage(userId.toString(), text));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void sendMessageWithMarkdown(Long userId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage(userId.toString(), text);
        if (replyKeyboard != null)
            sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setParseMode("Markdown");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public int sendMessage(Long userId, String text, ReplyKeyboard replyKeyboard) {
        final int MAX_LENGTH = 1024;
        int messageId = 0;
        if (text.length() > MAX_LENGTH) {
            int startIndex = 0;

            while (startIndex < text.length() - 1) {
                int endIndex = Math.min(startIndex + MAX_LENGTH, text.length());

                int lastNewLineIndex = text.lastIndexOf("\n\n", endIndex);
                if (lastNewLineIndex != -1 && lastNewLineIndex > startIndex) {
                    endIndex = lastNewLineIndex;
                }

                String part = text.substring(startIndex, endIndex).trim();

                SendMessage message = new SendMessage(userId.toString(), part);

                try {
                    if (replyKeyboard != null) {
                        message.setReplyMarkup(replyKeyboard);
                        messageId = execute(message).getMessageId();
                    } else messageId = execute(message).getMessageId();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                startIndex = endIndex + 1;
            }
            return messageId;
        } else {
            try {
                SendMessage sendMessage = new SendMessage(userId.toString(), text);
                sendMessage.setReplyMarkup(replyKeyboard);
                return execute(sendMessage).getMessageId();
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void editMessage(Long userId, Integer messageId, String text, InlineKeyboardMarkup replyKeyboard) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(messageId);
        editMessageText.setChatId(userId);
        editMessageText.setText(text);
        editMessageText.setReplyMarkup(replyKeyboard);
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка редактирования сообщения: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void leaveChat(Long groupId) {
        try {
            executeAsync(new LeaveChat(groupId.toString()));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void acceptJoinRequest(Long userId, Long groupId) {
        ApproveChatJoinRequest acceptJoinReq = new ApproveChatJoinRequest();
        acceptJoinReq.setUserId(userId);
        acceptJoinReq.setChatId(groupId);
        try {
            execute(acceptJoinReq);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void kickChatMember(Long userId, Long groupId) {
        try {
            String string = groupId.toString();
            BanChatMember banChatMember = new BanChatMember(string, userId);
            execute(banChatMember);
            UnbanChatMember unbanChatMember = new UnbanChatMember(string, userId);
            execute(unbanChatMember);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLink(Long groupId) {
        try {
            EditChatInviteLink editChatInviteLink = new EditChatInviteLink();
            editChatInviteLink.setChatId(groupId);
            editChatInviteLink.setCreatesJoinRequest(true);
            editChatInviteLink.setName("Link by bot");
            if (link != null)
                editChatInviteLink.setInviteLink(link);
            ChatInviteLink execute = execute(editChatInviteLink);
            return execute.getInviteLink();
        } catch (TelegramApiException e) {
            try {
                CreateChatInviteLink createChatInviteLink = new CreateChatInviteLink();
                createChatInviteLink.setName("Link by bot");
                createChatInviteLink.setCreatesJoinRequest(true);
                createChatInviteLink.setChatId(groupId);

                return execute(createChatInviteLink).getInviteLink();
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    public boolean checkChatMember(Long userId, Long groupId) {
        try {
            return !execute(new GetChatMember(groupId.toString(), userId)).getStatus().equals("left");
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Chat getChat(Long userId) {
        try {
            return execute(new GetChat(userId.toString()));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPhoto(Long userId, String caption, String path, ReplyKeyboard keyboard) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setCaption(caption);
        InputFile photo = new InputFile();
        photo.setMedia(new java.io.File(path));
        sendPhoto.setPhoto(photo);
        sendPhoto.setChatId(userId);
        sendPhoto.setReplyMarkup(keyboard);
        sendPhoto.setParseMode("Markdown");
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(Long userId, Integer messageId) {
        try {
            executeAsync(new DeleteMessage(userId.toString(), messageId));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: Yaxshiroq qilish kerak
    public void sendLink(Long userId, ReplyKeyboard replyKeyboard) {
        if (link != null) {
            sendMessage(userId, langService.getMessage(LangFields.LINK_TEXT, userId) + " " + link);
            return;
        }
        List<Group> groups = groupRepository.findAll();
        if (groups.size() != 1)
            return;
        Group group = groups.get(0);
        String link = getLink(group.getGroupId());
        sendMessage(userId, langService.getMessage(LangFields.LINK_TEXT, userId) + " " + link, replyKeyboard);
        this.link = link;
    }
}
