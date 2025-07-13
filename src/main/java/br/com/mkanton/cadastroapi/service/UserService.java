package br.com.mkanton.cadastroapi.service;

import br.com.mkanton.cadastroapi.dataaccess.IUserDao;
import br.com.mkanton.cadastroapi.domain.PasswordEncoder;
import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.domain.enums.Status;
import br.com.mkanton.cadastroapi.dto.Request.*;
import br.com.mkanton.cadastroapi.service.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;


@ApplicationScoped
public class UserService implements IUserService {

    @Inject
    private IUserDao IUserDao;

    @Inject
    private CodeService codeService;

    @Inject
    private EmailService emailService;

    private  static final int MAX_ATTEMPTS = 3;

    @Inject
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Register a new user and send the validation code by email
     * @param dto User data for registration
     * @return User created
     * @throws IllegalArgumentException if the email is already in use
     */
    public User register(RegisterDto dto) {

        if (IUserDao.findExactByEmail(dto.getEmail()) !=null) {
        logger.error("Email already exists");
        throw new IllegalArgumentException("Email already exists");}

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.hashPassword(dto.getPassword()));
        user.setStatus(Status.NOT_VALIDATED);
        logger.info("Save user: {}", user.getEmail());
        IUserDao.insert(user);

        // generates and sends a code by email
        String verificationCode = codeService.generateCode(user);
        logger.info("Send code: {}", verificationCode);
        emailService.sendEmail(user.getEmail(), verificationCode);

        return user;
    }

    /**
     * Validates the registration code for a user identified by token-based username.
     *
     * @param username the authenticated user's username (extracted from JWT)
     * @param code the code to validate
     * @return true if the code is valid and user confirmed; false otherwise
     */
    public boolean validateRegistrationCode(String username, String code) {
        User user = IUserDao.findByEmailOrUsername(username);
        if (user == null) return false;

        boolean isValid = codeService.validateCode(user, code);

        if (isValid) {
            user.setStatus(Status.VALIDATED);
            user.setCodeValidate(null);
            IUserDao.update(user);
            return true;
        } else {
            CodeService.CodeData codeData = codeService.getCodeData(user.getId());

            if (codeData == null || codeData.getAttempts() >= MAX_ATTEMPTS || codeData.getExpiration().isBefore(LocalDateTime.now())) {
                logger.warn("Deleting user due to failed registration validation: {}", user.getEmail());
                IUserDao.delete(user.getId());
            }
            return false;
        }
    }

    /**
     * Deletes a user account after verifying the provided password.
     *
     * @param username the authenticated user's username
     * @param password the user's current password
     * @throws IllegalArgumentException if the user does not exist
     * @throws SecurityException if the password is incorrect
     */
    public void deleteUser(String username, String password) {
        User user = IUserDao.findByEmailOrUsername(username);
        if (user == null) {
            logger.error("User not found");
            throw new IllegalArgumentException("User not found");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            logger.error("Invalid password");
            throw new SecurityException("Invalid password");
        }
        IUserDao.delete(user.getId());
    }

    /**
     * Initiates an email update for the user after verifying password and email uniqueness.
     *
     * @param username the authenticated user's username
     * @param newEmail the new email address
     * @param password the user's current password
     * @return the updated user object
     * @throws IllegalArgumentException if validation fails
     * @throws SecurityException if password verification fails
     */
    public User updateEmail(String username, String newEmail, String password) {
        User user = IUserDao.findByEmailOrUsername(username);
        if (user == null) throw new IllegalArgumentException("User not found");

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            logger.warn("Invalid password");
            throw new SecurityException("Invalid password");
        }

        if (newEmail.equalsIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("New email must be different from the current one");
        }

        User existing = IUserDao.findExactByEmail(newEmail);
        if (existing != null && !existing.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Email already exists");
        }


        user.setPendingEmail(newEmail);
        user.setStatus(Status.AWAITING_EMAIL_VALIDATION);
        IUserDao.update(user);

        String verificationCode = codeService.generateCode(user);
        emailService.sendEmail(newEmail, verificationCode);
        logger.info("Verification code sent to {}", newEmail);

        return user;
    }

    /**
     * Validates the email update confirmation code.
     *
     * @param username the authenticated user's username
     * @param code the email update confirmation code
     * @return true if email update is confirmed; false otherwise
     */
    public boolean validateEmailUpdate(String username, String code) {
        User user = IUserDao.findByEmailOrUsername(username);
        System.out.println("VALIDATING EMAIL " + user.getEmail()+code);
        if (user == null || user.getPendingEmail() == null) {
            logger.error("User not found or no pending email update for: {}", username);
            return false;
        }

        boolean isValid = codeService.validateCode(user, code);

        if (isValid) {
            user.setEmail(user.getPendingEmail());
            user.setPendingEmail(null);
            user.setStatus(Status.VALIDATED);
            user.setCodeValidate(null);
            IUserDao.update(user);
            logger.info("Email updated successfully for user {}", username);
        } else {
            handleFailedEmailUpdate(user);
        }

        return isValid;
    }

    /**
     * Reverts a failed email update due to expiration or exceeded attempts.
     *
     * @param user the user whose pending email should be cleared
     */
    private void handleFailedEmailUpdate(User user) {
        CodeService.CodeData codeData = codeService.getCodeData(user.getId());

        if (codeData != null && (codeData.getAttempts() >= MAX_ATTEMPTS || codeData.getExpiration().isBefore(LocalDateTime.now()))) {
            logger.warn("Reverting pending email update for user: {}", user.getId());
            user.setPendingEmail(null);
            user.setStatus(null);
            user.setCodeValidate(null);
            IUserDao.update(user);
        }
    }

    /**
     * Updates the username of the authenticated user.
     *
     * @param username the authenticated user's current username
     * @param newUsername the new desired username
     * @param password the user's current password
     * @return the updated user object
     * @throws IllegalArgumentException if input is invalid or user not found
     * @throws SecurityException if password verification fails
     */
    public User updateUsername(String username, String newUsername, String password) {
        User user = IUserDao.findByEmailOrUsername(username);
        if (user == null) throw new IllegalArgumentException("User not found");

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new SecurityException("Invalid password");
        }

        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("New name cannot be null or empty");
        }

        if (newUsername.equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("New name must be different from the current one");
        }

        user.setUsername(newUsername.trim());
        IUserDao.update(user);
        logger.info("Username updated successfully to {}", newUsername);

        return user;
    }


}
