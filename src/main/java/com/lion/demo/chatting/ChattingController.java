package com.lion.demo.chatting;

import com.lion.demo.entity.User;
import com.lion.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/chatting")
public class ChattingController {
    @Autowired private ChatMessageService chatMessageService;
    @Autowired private RecipientService recipientService;
    @Autowired private UserService userService;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String sessUid = (String) session.getAttribute("sessUid");
        session.setAttribute("chattingStatus", "home");
        User user = userService.findByUid(sessUid);
        model.addAttribute("user", user);
        session.setAttribute("menu", "chatting");
        return "chatting/home";
    }


    @GetMapping("/mock")
    public String mockForm() {
        return "chatting/mock";
    }

    @PostMapping("/mock")
    public String mockProc(String senderUid, String recipientUid, String message, LocalDateTime timestamp) {
        User sender = userService.findByUid(senderUid);
        User recipient = userService.findByUid(recipientUid);
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender).recipient(recipient).message(message).timestamp(timestamp).hasRead(0)
                .build();
        chatMessageService.insertChatMessage(chatMessage);
//        recipientService.insertFriend(sender, recipient);
        return "redirect:/chatting/mock";
    }
}
