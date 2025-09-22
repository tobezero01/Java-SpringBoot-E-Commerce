package com.eshop.client.helper;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoogleVerifier {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public GoogleIdToken.Payload verify(String idTokenStr) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
        GoogleIdToken idToken = verifier.verify(idTokenStr);
        if (idToken == null) throw new IllegalArgumentException("Invalid Google ID token");
        GoogleIdToken.Payload payload = idToken.getPayload();
        String iss = payload.getIssuer();
        if (!List.of("accounts.google.com", "https://accounts.google.com").contains(iss)) {
            throw new IllegalArgumentException("Invalid issuer: " + iss);
        }
        return payload;
    }
}
