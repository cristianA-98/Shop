package com.cristian.shop.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private static final String private_key = "2ns7utNmJSCwdrx6KZpVEqUFdyoHMJoqGXQR7328y2Fi4UL2ggCKS4d8ZRIw8QHL";
    private static final long jwtExpirationDate = 3600000;


    public String generateToken(String email) {
        Date cuurentDate = new Date();
        Date ExpirateDate = new Date(cuurentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .subject(email)
                .issuedAt(cuurentDate)
                .expiration(ExpirateDate)
                .signWith(key(), SignatureAlgorithm.HS256).
                compact();


        return "Bearer " + token;

    }


    //Extract email from JWT
    public String extractToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }

    // Validate token
    public Boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parse(token);

        return true;

    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(private_key));
    }
}
