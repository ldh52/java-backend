package com.lion.demo.chatting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    @Autowired private ChatMessageRepository chatMessageRepository;

    @Override
    public List<ChatMessage> getListByUser(String uid, String friendUid) {
        return List.of();
    }

    @Override
    public ChatMessage insertChatMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }
}
