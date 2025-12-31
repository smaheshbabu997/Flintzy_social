package com.flintzy.social.repositories;

import com.flintzy.social.entities.FacebookAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacebookAccountRepository extends JpaRepository<FacebookAccount, Long> {
    Optional<FacebookAccount> findByUserId(Long id);
}
