package com.example.hoodDeals.controllers;

import com.example.hoodDeals.dto.conversation.ConversationResponse;
import com.example.hoodDeals.dto.conversation.CreateConversationRequest;
import com.example.hoodDeals.entities.Conversations;
import com.example.hoodDeals.repositories.ConversationsRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/conversations")
public class ConversationsController {

    private final ConversationsRepository conversationsRepo;

    public ConversationsController(ConversationsRepository conversationsRepo) {
        this.conversationsRepo = conversationsRepo;
    }

    // POST /conversations : create or reuse a conversation for a listing
    @PostMapping
    public ResponseEntity<ConversationResponse> create(
            @Valid @RequestBody CreateConversationRequest req,
            UriComponentsBuilder uriBuilder
    ) {
        //  same user both sides not allowed
        if (req.user1Id().equals(req.user2Id())) {
            return ResponseEntity.badRequest().build();
        }

        var existing = conversationsRepo.findByUsersAndListingAnyOrder(
                req.user1Id(), req.user2Id(), req.listingId(), req.receiverName(), req.receiverPicture()
        );

        boolean wasCreated = existing.isEmpty();
        Conversations conv = existing.orElseGet(() ->
                conversationsRepo.save(new Conversations(
                        req.user1Id(), req.user2Id(), req.listingId(), req.receiverName(), req.receiverPicture()
                ))
        );

        var dto = new ConversationResponse(
                conv.getId(), conv.getUser1Id(), conv.getUser2Id(),
                conv.getListingId(), conv.getLastMessageAt(), conv.getCreatedAt(), conv.getName(), conv.getPicture()
        );

        if (wasCreated) {
            var location = uriBuilder
                    .path("/conversations/{id}")
                    .buildAndExpand(conv.getId())
                    .toUri();
            return ResponseEntity.created(location).body(dto);
        } else {
            return ResponseEntity.ok(dto);
        }
    }

    // GET /conversations?userId=123&page=0&size=20 : list a user's conversations, newest first
    @GetMapping
    public ResponseEntity<Page<ConversationResponse>> listByUser(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("lastMessageAt").descending());
        Page<Conversations> pageResult = conversationsRepo.findByUserId(userId, pageable);

        Page<ConversationResponse> dtoPage = pageResult.map(c ->
                new ConversationResponse(
                        c.getId(), c.getUser1Id(), c.getUser2Id(),
                        c.getListingId(), c.getLastMessageAt(), c.getCreatedAt(), c.getName(), c.getPicture()
                )
        );
        return ResponseEntity.ok(dtoPage);
    }
}
