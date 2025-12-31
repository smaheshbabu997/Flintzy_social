package com.flintzy.social.repositories;

import com.flintzy.social.entities.FacebookPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FacebookPageRepository extends JpaRepository<FacebookPage, Long> {
    Optional<FacebookPage> findByPageId(String pageId);

    @Query("select p from FacebookPage p where p.facebookAccount.user.id = :userId")
    List<FacebookPage> findAllByUserId(@Param("userId") Long userId);
}