package uz.pdp.appfullcontactbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appfullcontactbot.dto.UserDto;
import uz.pdp.appfullcontactbot.model.User;
import uz.pdp.appfullcontactbot.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/read-all")
    public ResponseEntity<?> readAll(@RequestParam Integer page, @RequestParam Integer size) {
        List<UserDto> result = userRepository.findAll(PageRequest.of(page, size)).stream().map(this::toDTO).toList();
        return ResponseEntity.ok(result);
    }


    @GetMapping("/read/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(userRepository.findById(id).orElseThrow()));
    }

    private UserDto toDTO(User u) {
        return new UserDto(u.getId(), u.getContactNumber(), u.isSubscribed(), u.getSubscriptionEndTime());
    }
}
