package projet.Formation_IT3.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilitaire pour la gestion des tokens JWT.
 * Fournit les méthodes de génération, validation et extraction des claims JWT.
 */
@Component
public class JwtUtil {

    /** Clé secrète récupérée depuis application.properties */
    @Value("${jwt.secret}")
    private String secret;

    /** Durée de validité du token en millisecondes (24h par défaut) */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Génère un token JWT pour un utilisateur donné.
     * Le token contient l'email comme sujet et une date d'expiration.
     *
     * @param userDetails Les détails de l'utilisateur Spring Security
     * @return Le token JWT signé sous forme de chaîne
     */
    public String genererToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return creerToken(claims, userDetails.getUsername());
    }

    /**
     * Crée le token JWT avec les claims, le sujet et la signature.
     */
    private String creerToken(Map<String, Object> claims, String sujet) {
        return Jwts.builder()
                .claims(claims)
                .subject(sujet)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getCleSecrete())
                .compact();
    }

    /**
     * Extrait l'email (sujet) depuis un token JWT.
     *
     * @param token Le token JWT
     * @return L'email de l'utilisateur
     */
    public String extraireEmail(String token) {
        return extraireClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token.
     */
    public Date extraireExpiration(String token) {
        return extraireClaim(token, Claims::getExpiration);
    }

    /**
     * Méthode générique pour extraire n'importe quel claim d'un token.
     */
    public <T> T extraireClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraireTousClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait tous les claims d'un token JWT après vérification de la signature.
     */
    private Claims extraireTousClaims(String token) {
        return Jwts.parser()
                .verifyWith(getCleSecrete())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Vérifie si le token JWT est expiré.
     */
    private boolean estExpire(String token) {
        return extraireExpiration(token).before(new Date());
    }

    /**
     * Valide un token JWT en vérifiant le sujet et la date d'expiration.
     *
     * @param token Le token à valider
     * @param userDetails Les détails de l'utilisateur attendus
     * @return true si le token est valide
     */
    public boolean validerToken(String token, UserDetails userDetails) {
        final String email = extraireEmail(token);
        // Le token est valide si l'email correspond et qu'il n'est pas expiré
        return (email.equals(userDetails.getUsername()) && !estExpire(token));
    }

    /**
     * Construit la clé HMAC à partir de la clé secrète configurée.
     */
    private SecretKey getCleSecrete() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
