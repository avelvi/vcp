package com.aivlev.vcp.security;

import com.aivlev.vcp.model.Token;
import com.aivlev.vcp.model.User;
import com.aivlev.vcp.repository.storage.TokenRepository;
import com.aivlev.vcp.repository.storage.UserRepository;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

import static com.aivlev.vcp.config.SecurityConfig.REMEMBER_ME_KEY;

/**
 * Created by aivlev on 4/27/16.
 */
@Service
public class RememberMeServices extends AbstractRememberMeServices {

    private final Logger LOGGER = LoggerFactory.getLogger(RememberMeServices.class);

    private static final int TOKEN_VALIDITY_DAYS = 31;

    private static final int TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * TOKEN_VALIDITY_DAYS;

    private static final int DEFAULT_SERIES_LENGTH = 16;

    private static final int DEFAULT_TOKEN_LENGTH = 16;

    private SecureRandom random;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public RememberMeServices(Environment env, org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
        super(REMEMBER_ME_KEY, userDetailsService);
        super.setParameter("rememberme");
        random = new SecureRandom();
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {

        Token token = getPersistentToken(cookieTokens);
        String login = token.getUserLogin();

        // Token also matches, so login is valid. Update the token value, keeping the *same* series number.
        LOGGER.debug("Refreshing persistent login token for user '{}', series '{}'", login, token.getSeries());
        token.setDate(new Date());
        token.setValue(generateTokenData());
        token.setIpAddress(request.getRemoteAddr());
        token.setUserAgent(request.getHeader("User-Agent"));
        try {
            tokenRepository.save(token);
            addCookie(token, request, response);
        } catch (DataAccessException e) {
            LOGGER.error("Failed to update token: ", e);
            throw new RememberMeAuthenticationException("Autologin failed due to data access problem", e);
        }
        return getUserDetailsService().loadUserByUsername(login);
    }

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        String login = successfulAuthentication.getName();

        LOGGER.debug("Creating new persistent login for user {}", login);
        User user = userRepository.findByLogin(login);
        Token token = new Token();
        token.setSeries(generateSeriesData());
        token.setUserLogin(user.getLogin());
        token.setValue(generateTokenData());
        token.setDate(new Date());
        token.setIpAddress(request.getRemoteAddr());
        token.setUserAgent(request.getHeader("User-Agent"));
        try {
            tokenRepository.save(token);
            addCookie(token, request, response);
        } catch (DataAccessException e) {
            LOGGER.error("Failed to save persistent token ", e);
        }
    }

    /**
     * When logout occurs, only invalidate the current token, and not all user sessions.
     * <p/>
     * The standard Spring Security implementations are too basic: they invalidate all tokens for the
     * current user, so when he logs out from one browser, all his other sessions are destroyed.
     */
    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String rememberMeCookie = extractRememberMeCookie(request);
        if (rememberMeCookie != null && rememberMeCookie.length() != 0) {
            try {
                String[] cookieTokens = decodeCookie(rememberMeCookie);
                Token token = getPersistentToken(cookieTokens);
                tokenRepository.delete(token.getSeries());
            } catch (InvalidCookieException ice) {
                LOGGER.info("Invalid cookie, no persistent token could be deleted");
            } catch (RememberMeAuthenticationException rmae) {
                LOGGER.debug("No persistent token found, so no token could be deleted");
            }
        }
        super.logout(request, response, authentication);
    }

    /**
     * Validate the token and return it.
     */
    private Token getPersistentToken(String[] cookieTokens) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];

        Token token = null;
        try {
            token = tokenRepository.findOne(presentedSeries);
        } catch (DataAccessException e) {
            LOGGER.error("Error to access database", e);
        }

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        // We have a match for this user/series combination
        LOGGER.info("presentedToken={} / tokenValue={}", presentedToken, token.getValue());
        if (!presentedToken.equals(token.getValue())) {
            // Token doesn't match series value. Delete this session and throw an exception.
            tokenRepository.delete(token.getSeries());
            throw new CookieTheftException("Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack.");
        }

        if (DateUtils.addDays(token.getDate(), TOKEN_VALIDITY_DAYS).before(new Date())) {
            tokenRepository.delete(token.getSeries());
            throw new RememberMeAuthenticationException("Remember-me login has expired");
        }
        return token;
    }

    private String generateSeriesData() {
        byte[] newSeries = new byte[DEFAULT_SERIES_LENGTH];
        random.nextBytes(newSeries);
        return new String(Base64.encode(newSeries));
    }

    private String generateTokenData() {
        byte[] newToken = new byte[DEFAULT_TOKEN_LENGTH];
        random.nextBytes(newToken);
        return new String(Base64.encode(newToken));
    }

    private void addCookie(Token token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(
                new String[]{token.getSeries(), token.getValue()},
                TOKEN_VALIDITY_SECONDS, request, response);
    }
}