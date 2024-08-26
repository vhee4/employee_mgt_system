package com.EmployeeMgtSystem.AuthenticationServer.config;

import com.EmployeeMgtSystem.AuthenticationServer.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
public class JwtConfig {

    @Value("${issuer}")
    private String issuer;

    @Value("${audience}")
    private String audience;

    @Value("${private_key}")
    private String private_key;

    @Value("${public_key}")
    private String public_key;


    private RSAPrivateKey getPrivateKeyFromFile(String keyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyFile));
        String privateKeySTr = new String(keyBytes, StandardCharsets.UTF_8);

        PemObject pem = new PemReader(new StringReader(privateKeySTr)).readPemObject();
        byte[] der = pem.getContent();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(der);
        RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(ks);
        return privKey;
    }
    public RSAPublicKey getPublicKeyFromFile(String keyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Read file content directly using absolute path
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyFile));
        String publicKeyStr = new String(keyBytes, StandardCharsets.UTF_8);

        // Parse PEM object
        PemObject pemObject = new PemReader(new StringReader(publicKeyStr)).readPemObject();
        byte[] der = pemObject.getContent();

        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPublicKey publicKey = getPublicKeyFromFile(public_key);
        RSAPrivateKey privateKey = getPrivateKeyFromFile(private_key);
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

//    public String getClaimFromToken(String token, String claimName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
////        Jwt jwt = decodeToken(token);
//        Jwt jwt = jwtDecoder(jwkSource()).decode(token);
//        return jwt.getClaim(claimName);
//    }

//    public Jwt decodeToken(String token) {
//        return jwtDecoder.decode(token);
//    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    public String generateToken(String subject) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(360000))
                .subject(subject)
                .build();

        JwtEncoder encoder = jwtEncoder(jwkSource());
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateToken(String subject, Collection<? extends GrantedAuthority> authorities, List<String> permissions, User user) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Instant now = Instant.now();

        // Extract roles from authorities
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(subject)
                .claim("userRole", roles) // Add roles to claims
                .claim("userPermissions", permissions) // Add permissions to claims
                .claim("username",user.getUsername())
                .claim("id",user.getId())
                .claim("email",user.getEmail())
                .build();

        JwtEncoder encoder = jwtEncoder(jwkSource());
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
