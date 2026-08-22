package com.tech.wixblog.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtConfig.JwtRsaProperties.class)
public class JwtConfig {

    @Bean
    public JwtEncoder jwtEncoder(JwtRsaProperties rsaProperties) throws Exception {
        // Programmatically convert the Resource streams into real RSA keys
        RSAPublicKey publicKey = parsePublicKey(rsaProperties.publicKeyLocation());
        RSAPrivateKey privateKey = parsePrivateKey(rsaProperties.privateKeyLocation());

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(JwtRsaProperties rsaProperties) throws Exception {
        RSAPublicKey publicKey = parsePublicKey(rsaProperties.publicKeyLocation());
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    // Helper methods using Spring Security converters to decode key file bytes
    private RSAPublicKey parsePublicKey(Resource resource) throws Exception {
        try (InputStream is = resource.getInputStream()) {
            return RsaKeyConverters.x509().convert(is);
        }
    }

    private RSAPrivateKey parsePrivateKey(Resource resource) throws Exception {
        try (InputStream is = resource.getInputStream()) {
            return RsaKeyConverters.pkcs8().convert(is);
        }
    }

    // Map your configuration values cleanly as file Resources
    @ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
    public record JwtRsaProperties(
            Resource publicKeyLocation,
            Resource privateKeyLocation
    ) {}
}
