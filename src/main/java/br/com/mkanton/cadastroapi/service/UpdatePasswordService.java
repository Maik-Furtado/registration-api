package br.com.mkanton.cadastroapi.service;

import br.com.mkanton.cadastroapi.dataaccess.IUserDao;
import br.com.mkanton.cadastroapi.domain.PasswordEncoder;
import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.domain.enums.Status;
import br.com.mkanton.cadastroapi.dto.Request.*;
import br.com.mkanton.cadastroapi.dto.response.PasswordUpdateResponseDto;
import br.com.mkanton.cadastroapi.security.JwtUtil;
import br.com.mkanton.cadastroapi.service.interfaces.IUpdatePasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;

@ApplicationScoped
public class UpdatePasswordService  implements IUpdatePasswordService {

    @Inject
    private IUserDao userDao;

    @Inject
    private CodeService codeService;

    @Inject
    private EmailService emailService;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private AuthService authService;

    @Context
    private SecurityContext securityContext;

    private  static final int MAX_ATTEMPTS = 3;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * @param dto
     */
    @Override
    public User startPasswordUpdate(StartUpdatePasswordDto dto) {
        String email = dto.getEmail().trim();

        User user = userDao.findByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found with the given email."));

        user.setStatus(Status.NOT_VALIDATED);
        userDao.update(user);

        String code = codeService.generateCode(user);
        emailService.sendEmail(email, code);

        String token = JwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return user;
    }



    /**
     * Validates the recovery code for a password update process using the authenticated username.
     *
     * @param username Authenticated user's username (extracted from JWT)
     * @param dto      DTO containing the recovery code
     * @return true if the code is valid and user status is updated; false otherwise
     */
    @Override
    public boolean validateCodePswUpdate(String username, ValidatePasswordUpdateCodeDto dto) {
        User user = userDao.findByEmailOrUsername(username);
        if (user == null) {
            logger.warn("User not found: {}", username);
            throw new IllegalArgumentException("User not found.");
        }

        if (user.getStatus() != Status.NOT_VALIDATED) {
            logger.warn("User not in recovery state: {}", username);
            return false;
        }

        boolean isValid = codeService.validateCode(user, dto.getCode());

        if (isValid) {
            user.setStatus(Status.VALIDATED);
            user.setCodeValidate(null);
            userDao.update(user);
            logger.info("Password recovery code validated successfully for user: {}", username);
        } else {
            handleFailedPasswordCodeValidation(user);
        }

        return isValid;
    }


    /**
     * Handles failed password recovery attempts: resets status if attempts exceeded or code expired.
     *
     * @param user the user undergoing password recovery.
     */
    public void handleFailedPasswordCodeValidation(User user) {
        CodeService.CodeData codeData = codeService.getCodeData(user.getId());

        if (codeData != null && (codeData.getAttempts() >= MAX_ATTEMPTS ||
                codeData.getExpiration().isBefore(LocalDateTime.now()))) {

            logger.warn("Resetting password update for user: {}", user.getId());
            user.setStatus(Status.VALIDATED);
            user.setCodeValidate(null);
            userDao.update(user);
        }
    }

    /**
     * Updates the password of a user after verifying the provided code and password confirmation.
     *
     * @param dto The data transfer object containing the code, new password, and confirmation.
     * @throws IllegalArgumentException if any validation fails (user not validated, weak password).
     * @throws IllegalStateException if an error occurs while updating the password in the database.
     */
    @Override
    public User updatePassword(NewPasswordDto dto, String username) {
        User user = userDao.findByEmailOrUsername(username);

        if (user.getStatus() != Status.VALIDATED) {
            throw new IllegalArgumentException("User not validated.");
        }

        String newPassword = dto.getNewPassword().trim();
        String confirmPassword = dto.getConfirmPassword().trim();

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password too weak.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        try {
            user.setPasswordHash(passwordEncoder.hashPassword(newPassword));
            user.setStatus(Status.VALIDATED);
            user.setCodeValidate(null);
            userDao.update(user);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to update password.");
        }


        emailService.sendEmail(user.getEmail(), "Password updated successfully.");

        return user;
    }

}
