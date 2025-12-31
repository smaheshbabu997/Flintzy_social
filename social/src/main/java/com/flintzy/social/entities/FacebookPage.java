package com.flintzy.social.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facebook_pages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fb_account_id")
    private FacebookAccount facebookAccount;
    private String pageId;
    private String pageName;
    @Column(columnDefinition = "TEXT")
    private String pageAccessToken;
}