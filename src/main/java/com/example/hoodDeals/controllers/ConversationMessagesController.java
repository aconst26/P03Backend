package com.example.hoodDeals.controllers;

import com.example.hoodDeals.dto.message.CreateMessageRequest;
import com.example.hoodDeals.dto.message.MessageResponse;
import com.example.hoodDeals.entities.Messages;
import com.example.hoodDeals.repositories.ConversationsRepository;
import com.example.hoodDeals.repositories.MessagesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/conversations/{conversationId}/messages")
public class ConversationMessagesController {

    private final MessagesRepository messagesRepo;
    private final ConversationsRepository conversationsRepo;

    public ConversationMessagesController(MessagesRepository messagesRepo,
                                          ConversationsRepository conversationsRepo) {
        this.messagesRepo = messagesRepo;
        this.conversationsRepo = conversationsRepo;
    }

    // ✅ GET /conversations/{conversationId}/messages?page=&size=
    @GetMapping
    public ResponseEntity<Page<MessageResponse>> list(
            @PathVariable Long conversationId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        // Make sure conversation exists (optional but nice)
        conversationsRepo.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        Page<Messages> page = messagesRepo
                .findByConversationIdOrderByCreatedAtAsc(conversationId, pageable);

        Page<MessageResponse> dtoPage = page.map(m -> new MessageResponse(
                m.getId(),
                m.getConversationId(),
                m.getSenderId(),
                m.getReceiverId(),
                m.getContent(),
                m.getCreatedAt(),
                m.getUpdatedAt(),
                m.getName()
        ));

        return ResponseEntity.ok(dtoPage);
    }

    // POST /conversations/{conversationId}/messages
    @PostMapping
    public ResponseEntity<MessageResponse> send(@PathVariable Long conversationId,
                                                @RequestBody CreateMessageRequest req) {
        var conv = conversationsRepo.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        // Validate membership: sender must be one of the two participants
        Long user1 = conv.getUser1Id();
        Long user2 = conv.getUser2Id();
        if (!(req.senderId().equals(user1) || req.senderId().equals(user2))) {
            return ResponseEntity.badRequest().build();
        }

        // Derive/validate receiver
        Long expectedReceiver = req.senderId().equals(user1) ? user2 : user1;
        Long receiver = (req.receiverId() != null && req.receiverId().equals(expectedReceiver))
                ? req.receiverId()
                : expectedReceiver;

        var msg = new Messages();
        msg.setConversationId(conversationId);
        msg.setSenderId(req.senderId());
        msg.setReceiverId(receiver);
        msg.setContent(req.content());
        msg.setListingId(conv.getListingId());
        msg.setIsRead(false);
        msg.setName(conv.getName());

        var saved = messagesRepo.save(msg);

        // Bump conversation activity
        conv.setLastMessageAt(LocalDateTime.now());
        conversationsRepo.save(conv);

        var dto = new MessageResponse(
                saved.getId(),
                saved.getConversationId(),
                saved.getSenderId(),
                saved.getReceiverId(),
                saved.getContent(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getName()
        );
        return ResponseEntity.ok(dto);
    }

    // PATCH /conversations/{conversationId}/messages/{messageId}/read
    @PatchMapping("/{messageId}/read")
    public ResponseEntity<MessageResponse> markRead(@PathVariable Long conversationId,
                                                    @PathVariable Long messageId) {
        var conv = conversationsRepo.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        var msg = messagesRepo.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if (!conversationId.equals(msg.getConversationId())) {
            return ResponseEntity.badRequest().build();
        }

        var user1 = conv.getUser1Id();
        var user2 = conv.getUser2Id();
        if (!(msg.getSenderId().equals(user1) || msg.getSenderId().equals(user2))) {
            return ResponseEntity.badRequest().build();
        }

        msg.setIsRead(true);
        var saved = messagesRepo.save(msg);

        var dto = new MessageResponse(
                saved.getId(), saved.getConversationId(), saved.getSenderId(), saved.getReceiverId(),
                saved.getContent(), saved.getCreatedAt(), saved.getUpdatedAt(), saved.getName()
        );
        return ResponseEntity.ok(dto);
    }

    // PATCH /conversations/{conversationId}/messages/{messageId}/unread
    @PatchMapping("/{messageId}/unread")
    public ResponseEntity<MessageResponse> markUnread(@PathVariable Long conversationId,
                                                      @PathVariable Long messageId) {
        var conv = conversationsRepo.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        var msg = messagesRepo.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if (!conversationId.equals(msg.getConversationId())) {
            return ResponseEntity.badRequest().build();
        }

        var user1 = conv.getUser1Id();
        var user2 = conv.getUser2Id();
        if (!(msg.getSenderId().equals(user1) || msg.getSenderId().equals(user2))) {
            return ResponseEntity.badRequest().build();
        }

        msg.setIsRead(false);
        var saved = messagesRepo.save(msg);

        var dto = new MessageResponse(
                saved.getId(), saved.getConversationId(), saved.getSenderId(), saved.getReceiverId(),
                saved.getContent(), saved.getCreatedAt(), saved.getUpdatedAt(), saved.getName()
        );
        return ResponseEntity.ok(dto);
    }
}
