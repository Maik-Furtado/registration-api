package br.com.mkanton.cadastroapi.service;

import br.com.mkanton.cadastroapi.dataaccess.IUserDao;
import br.com.mkanton.cadastroapi.domain.PasswordEncoder;
import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.dto.Request.LoginDto;
import br.com.mkanton.cadastroapi.service.interfaces.IAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class AuthService implements IAuthService {
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Inject
    private IUserDao IUserDao;

    @Inject
    private PasswordEncoder passwordEncoder;

    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    /**
     * Authenticates a user using a DTO containing email/username and raw password.
     *
     * @param loginDto The DTO containing login credentials.
     * @return The authenticated User if credentials are valid and user is validated.
     * @throws IllegalArgumentException if credentials are invalid or user is not validated.
     */

    public User authenticate(LoginDto loginDto) {

        String value = loginDto.getValue();
        String rawPassword = loginDto.getPassword();


        User user;

        try {
            user = IUserDao.findByEmailOrUsername(value);
        } catch (Exception e) {
            logger.error("Error fetching user during authentication", e);
            throw new IllegalArgumentException("Invalid username or password.", e);
        }

        // verifies if the user exists and the password is correct
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            logger.warn("Invalid login attempt for value: {}", value);
            throw new IllegalArgumentException("Invalid username or password.");
        }

        // Verifies if the user's account is validated.
        if (user.getStatus() != null && user.getStatus().name().equals("NOT_VALIDATED")) {
            logger.error("User not validated: {}", value);
            throw new IllegalArgumentException("User not validated, check your email");
        }
        return user;
    }


    public void logout(String token) {
        tokenBlacklist.add(token);
    }
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
}
