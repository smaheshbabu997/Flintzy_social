package com.flintzy.social.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "facebook_accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private  User user;
    private String facebookUserId;
    @Column(columnDefinition = "TEXT")
    private String accessToken;
    private Instant tokenExpiresAt;
}
