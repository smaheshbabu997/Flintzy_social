package com.flintzy.social.services;

import com.flintzy.social.entities.FacebookAccount;
import com.flintzy.social.entities.FacebookPage;
import com.flintzy.social.entities.User;
import com.flintzy.social.repositories.FacebookAccountRepository;
import com.flintzy.social.repositories.FacebookPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class FacebookService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final FacebookAccountRepository fbAccountRepo;
    private final FacebookPageRepository fbPageRepo;

    @Value("${facebook.app-id}")
    private String appId;
    @Value("${facebook.app-secret}")
    private String appSecret;

    public String getAuthUrl(String redirectUri, String state) {
        String scope = UriUtils.encode("pages_show_list,pages_manage_posts,email,public_profile", StandardCharsets.UTF_8);
        return "https://www.facebook.com/v19.0/dialog/oauth" +
                "?client_id=" + appId +
                "&redirect_uri=" + UriUtils.encode(redirectUri, StandardCharsets.UTF_8) +
                "&state=" + state +
                "&scope=" + scope;
    }

    public Map<String, Object> exchangeCodeForToken(String code, String redirectUri) {
        String url = String.format(
                "https://graph.facebook.com/v19.0/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s",
                appId, UriUtils.encode(redirectUri, StandardCharsets.UTF_8), appSecret, code);
        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getUserProfile(String userAccessToken) {
        String url = "https://graph.facebook.com/v19.0/me?fields=id,name,email&access_token=" + userAccessToken;
        return restTemplate.getForObject(url, Map.class);
    }

    public List<Map<String, Object>> getManagedPages(String userAccessToken) {
        String url = "https://graph.facebook.com/v19.0/me/accounts?access_token=" + userAccessToken;
        Map response = restTemplate.getForObject(url, Map.class);
        return (List<Map<String, Object>>) response.get("data");
    }

    public void saveAccountAndPages(User user, String fbUserId, String userToken, List<Map<String, Object>> pages) {
        FacebookAccount account = fbAccountRepo.findByUserId(user.getId())
                .orElseGet(() -> fbAccountRepo.save(FacebookAccount.builder()
                        .user(user).facebookUserId(fbUserId).accessToken(userToken).build()));

        for (Map<String, Object> page : pages) {
            String pageId = (String) page.get("id");
            String pageName = (String) page.get("name");
            String pageToken = (String) page.get("access_token");

            fbPageRepo.findByPageId(pageId).orElseGet(() -> fbPageRepo.save(FacebookPage.builder()
                    .facebookAccount(account).pageId(pageId).pageName(pageName).pageAccessToken(pageToken).build()));
        }
    }

    public String publishToPage(FacebookPage page, String message) {
        String url = "https://graph.facebook.com/v19.0/" + page.getPageId() + "/feed";
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("message", message);
        body.add("access_token", page.getPageAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(body, headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);

        Map<String, Object> responseBody = resp.getBody();
        return (String) responseBody.get("id");
    }
}
