package com.flintzy.social.controller;

import com.flintzy.social.entities.FacebookPage;
import com.flintzy.social.entities.User;
import com.flintzy.social.repositories.FacebookPageRepository;
import com.flintzy.social.services.FacebookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// FacebookController.java
@RestController
@RequestMapping("/api/facebook")
@RequiredArgsConstructor
public class FacebookController {

    private final FacebookService facebookService;
    private final FacebookPageRepository fbPageRepo;

    @Value("${facebook.redirect-uri}")
    private String redirectUri;

    @GetMapping("/connect")
    public ResponseEntity<Map<String, String>> startConnect(Authentication auth) {
        User user = (User) auth.getPrincipal();
        String state = UUID.randomUUID().toString();
        String url = facebookService.getAuthUrl(redirectUri, state);
        return ResponseEntity.ok(Map.of("authUrl", url));
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> callback(Authentication auth, @RequestParam String code) {
        User user = (User) auth.getPrincipal();

        Map<String, Object> tokenResp = facebookService.exchangeCodeForToken(code, redirectUri);
        String userAccessToken = (String) tokenResp.get("access_token");

        Map<String, Object> profile = facebookService.getUserProfile(userAccessToken);
        String fbUserId = (String) profile.get("id");

        List<Map<String, Object>> pages = facebookService.getManagedPages(userAccessToken);
        facebookService.saveAccountAndPages(user, fbUserId, userAccessToken, pages);

        return ResponseEntity.ok(Map.of("linkedPages", pages.size()));
    }
//
//    @GetMapping("/pages")
//    public List<Map<String, Object>> listPages(Authentication auth) {
//        User user = (User) auth.getPrincipal();
//        return fbPageRepo.findAllByUserId(user.getId()).stream()
//                .map(p -> Map.of("pageId", p.getPageId(), "name", p.getPageName()))
//                .toList();
//    }

    @PostMapping("/pages/{pageId}/publish")
    public ResponseEntity<Map<String, Object>> publish(Authentication auth,
                                                       @PathVariable String pageId,
                                                       @RequestBody Map<String, String> body) {
        User user = (User) auth.getPrincipal();
        FacebookPage page = fbPageRepo.findByPageId(pageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not linked"));

        if (!page.getFacebookAccount().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your page");
        }

        try {
            String postId = facebookService.publishToPage(page, body.get("message"));
            return ResponseEntity.ok(Map.of("postId", postId));
        } catch (RestClientResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Facebook error: " + ex.getResponseBodyAsString());
        }
    }
}
