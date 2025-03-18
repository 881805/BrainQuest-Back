package com.project.demo.logic.entity.conversation;

import com.project.demo.logic.entity.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {


    @Query("SELECT c FROM Conversation c WHERE c.user1.userId = :userId OR c.user2.userId = :userId")
    Page<Conversation> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
