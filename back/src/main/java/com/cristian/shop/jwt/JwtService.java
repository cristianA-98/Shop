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
    private static final long jwtExpirationDate = 30L * 24 * 60 * 60 * 1000; //! 30 days expiration access token
    private static final long expirationResetPassword = 15L * 60 * 1000; //! 15 min expiration reset password

    public String generateJwt(String email) {
        Date cuurentDate = new Date();
        Date ExpirateDate = new Date(cuurentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .subject(email)
                .issuedAt(cuurentDate)
                .expiration(ExpirateDate)
                .signWith(getKey(), SignatureAlgorithm.HS256).
                compact();


        return "Bearer " + token;
    }

    public String generateJwtPasswordReset(String email) {
        Date cuurentDate = new Date();
        Date ExpirateDate = new Date(cuurentDate.getTime() + expirationResetPassword);
        String token = Jwts.builder()
                .subject(email)
                .issuedAt(cuurentDate)
                .expiration(ExpirateDate)
                .signWith(getKey(), SignatureAlgorithm.HS256).
                compact();

        return "Bearer " + token;
    }

    public String extractJwt(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public Boolean validateJwt(String token) {
        Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parse(token);
        return true;
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(private_key));
    }

}
