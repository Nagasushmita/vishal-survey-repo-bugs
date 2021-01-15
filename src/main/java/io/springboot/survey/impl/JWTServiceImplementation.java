package io.springboot.survey.impl;

import io.jsonwebtoken.*;
import io.springboot.survey.exception.AuthorizationException;
import io.springboot.survey.models.UserModel;
import io.springboot.survey.service.JwtUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.springboot.survey.utils.Constants.AuthorizationModuleConstant.*;
import static io.springboot.survey.utils.Constants.LoggerConstants.EXITING_METHOD_EXECUTION;
import static io.springboot.survey.utils.Constants.LoggerConstants.STARTING_METHOD_EXECUTION;


@Component
public class JWTServiceImplementation implements JwtUtil {

    private static final Logger logger= LoggerFactory.getLogger(JWTServiceImplementation.class.getSimpleName());

    /**
     * Generate jwt token for the user
     *
     * @param userModel : UserModel.
     * @return : String
     */
    public String generateToken(@NotNull UserModel userModel) {
        logger.info(STARTING_METHOD_EXECUTION);
        Map<String, Object> claims = new HashMap<>();
        logger.info(EXITING_METHOD_EXECUTION);
        return BEARER+createToken(claims, userModel.getUserEmail());
    }

    /**
     * jwt token for the user having email(subject)
     *
     * @param claims : Map<String, Object>
     * @param subject : email of the user
     * @return : String
     */
    private String createToken(Map<String, Object> claims, String subject) {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1800 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    /**
     * Extracting all claims
     *
     * @param token : jwt token
     * @return  Claims
     */
    private Claims extractAllClaims(String token) {
        logger.info(STARTING_METHOD_EXECUTION);
        try {
            logger.info(EXITING_METHOD_EXECUTION);
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        }
        catch (SignatureException|ExpiredJwtException|MalformedJwtException|IllegalArgumentException jwtException) {
            throw new AuthorizationException(jwtException.getMessage());
        }
    }

    /**
     * Extract claim
     *
     * @param token : jwt token
     * @param claimsResolver : Claims
     * @param <T> : claim to be extracted
     * @return <T>
     */
    public <T> T extractClaim(String token, @NotNull Function<Claims, T> claimsResolver) {
        logger.info(STARTING_METHOD_EXECUTION);
        final Claims claims = extractAllClaims(token);
        logger.info(EXITING_METHOD_EXECUTION);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract User email from token
     *
     * @param token : token
     * @return String
     */
    public String extractUserEmail(String token)
    {
        logger.info(STARTING_METHOD_EXECUTION);
        logger.info(EXITING_METHOD_EXECUTION);
        return extractClaim(token, Claims::getSubject);
    }



}
