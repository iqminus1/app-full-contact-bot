package uz.pdp.appfullcontactbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.appfullcontactbot.enums.LangFields;
import uz.pdp.appfullcontactbot.model.User;
import uz.pdp.appfullcontactbot.repository.UserRepository;
import uz.pdp.appfullcontactbot.service.LangService;
import uz.pdp.appfullcontactbot.service.telegram.Sender;
import uz.pdp.appfullcontactbot.utils.AppConstants;
import uz.pdp.appfullcontactbot.utils.CommonUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/1x772jhx78i4nx634F8a89A")
public class AdminController {
    private final CommonUtils commonUtils;
    private final UserRepository userRepository;
    private final Sender sender;
    private final LangService langService;

    @PostMapping
    public void setAdmin(@RequestParam(required = false) Long userId, @RequestParam(required = false) Integer adminLvl, @RequestParam(required = false) String code) {
        if (code.equals(AppConstants.SET_ADMIN_CODE)) {
            if (userId != null && adminLvl != null) {
                if (userRepository.existsById(userId)) {
                    User user = commonUtils.getUser(userId);
                    user.setAdmin(adminLvl);
                    userRepository.save(user);
                    sender.sendMessage(userId, langService.getMessage(LangFields.CHANGED_TO_ADMIN, userId).formatted(adminLvl));
                }
            }
        }
    }
}
