package br.com.mkanton.cadastroapi.service.interfaces;

import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.dto.Request.*;

public interface IUserService {

    /**
     * Registers a new user in the system.
     *
     * @param dto DTO containing username, email, and password
     * @return the created User object
     */
    User register(RegisterDto dto);

    /**
     * Validates a registration code using the username and code.
     *
     * @param username the authenticated user's username
     * @param code the code to validate
     * @return true if the code is valid
     */
    boolean validateRegistrationCode(String username, String code);

    /**
     * Deletes a user account after validating the password.
     *
     * @param username the authenticated user's username
     * @param password the user's current password
     */
    void deleteUser(String username, String password);

    /**
     * Updates the user's email address after password verification.
     *
     * @param username the authenticated user's username
     * @param newEmail the new email to apply
     * @param password the current password for confirmation
     * @return the updated User object
     */
    User updateEmail(String username, String newEmail, String password);

    /**
     * Validates an email update code.
     *
     * @param username the authenticated user's username
     * @param code the code received via email
     * @return true if the code is valid and email is updated
     */
    boolean validateEmailUpdate(String username, String code);

    /**
     * Updates the username after validating the password.
     *
     * @param username the authenticated user's current username
     * @param newUsername the new desired username
     * @param password the current password for confirmation
     * @return the updated User object
     */
    User updateUsername(String username, String newUsername, String password);

}
