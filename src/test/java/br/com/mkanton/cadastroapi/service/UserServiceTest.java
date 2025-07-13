package br.com.mkanton.cadastroapi.service;

import br.com.mkanton.cadastroapi.dataaccess.IUserDao;
import br.com.mkanton.cadastroapi.domain.PasswordEncoder;
import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.domain.enums.Status;
import br.com.mkanton.cadastroapi.dto.Request.RegisterDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private IUserDao userDao;

    @Mock
    private CodeService codeService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_should_succeed() {
        RegisterDto dto = new RegisterDto("user@email.com", "user", "password123");
        when(userDao.findExactByEmail(dto.getEmail())).thenReturn(null);
        when(passwordEncoder.hashPassword(dto.getPassword())).thenReturn("hashed");
        when(codeService.generateCode(any())).thenReturn("123456");

        User result = userService.register(dto);
        assertEquals(Status.NOT_VALIDATED, result.getStatus());
        verify(userDao).insert(any());
        verify(emailService).sendEmail(eq(dto.getEmail()), eq("123456"));
    }

    @Test
    void register_should_fail_if_email_exists() {
        RegisterDto dto = new RegisterDto("user@email.com", "user", "password123");
        when(userDao.findExactByEmail(dto.getEmail())).thenReturn(new User());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.register(dto));
        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void deleteUser_should_succeed() {
        User user = new User();
        user.setPasswordHash("hashed");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);

        userService.deleteUser("user", "password123");
        verify(userDao).delete(user.getId());
    }

    @Test
    void deleteUser_should_fail_if_user_not_found() {
        when(userDao.findByEmailOrUsername("user")).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser("user", "123"));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void deleteUser_should_fail_if_password_invalid() {
        User user = new User();
        user.setPasswordHash("hashed");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        Exception ex = assertThrows(SecurityException.class, () -> userService.deleteUser("user", "wrong"));
        assertEquals("Invalid password", ex.getMessage());
    }
    @Test
    void updateEmail_should_succeed() {
        User user = new User();
        user.setPasswordHash("hash"); // Adicionado
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);
        when(userDao.findExactByEmail("new@email.com")).thenReturn(null);
        when(codeService.generateCode(user)).thenReturn("code");

        User result = userService.updateEmail("user", "new@email.com", "pass");

        assertEquals("new@email.com", result.getPendingEmail());
        assertEquals(Status.AWAITING_EMAIL_VALIDATION, result.getStatus());
        verify(emailService).sendEmail(eq("new@email.com"), eq("code"));
    }

    @Test
    void updateEmail_should_fail_if_password_incorrect() {
        User user = new User();
        user.setPasswordHash("hash");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThrows(SecurityException.class, () ->
                userService.updateEmail("user", "new@email.com", "wrong"));
    }

    @Test
    void updateEmail_should_fail_if_email_already_exists() {
        User user = new User();
        user.setId(1L);
        user.setPasswordHash("hash");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);

        User existing = new User();
        existing.setId(2L);
        when(userDao.findExactByEmail("new@email.com")).thenReturn(existing);

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateEmail("user", "new@email.com", "pass"));
    }

    @Test
    void updateEmail_should_fail_if_same_email() {
        User user = new User();
        user.setEmail("user@email.com");
        user.setPasswordHash("hash");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateEmail("user", "user@email.com", "pass"));
    }

    @Test
    void updateEmail_should_fail_if_user_not_found() {
        User user = new User();
        when(userDao.findByEmailOrUsername("user")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateEmail("user", "new@email.com", "pass"));
    }

    // ====== UPDATE USERNAME ======

    @Test
    void updateUsername_should_succeed() {
        User user = new User();
        user.setUsername("oldUser");
        user.setPasswordHash("hash");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);

        User result = userService.updateUsername("user", "newName", "pass");

        assertEquals("newName", result.getUsername());
        verify(userDao).update(user);
    }

    @Test
    void updateUsername_should_fail_if_same_username() {
        User user = new User();
        user.setUsername("user");
        user.setPasswordHash("hash");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUsername("user", "user", "pass"));
    }

    @Test
    void updateUsername_should_fail_if_invalid_new_username() {
        User user = new User();
        user.setPasswordHash("hash");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUsername("user", "   ", "pass"));
    }

    @Test
    void updateUsername_should_fail_if_password_wrong() {
        User user = new User();
        user.setPasswordHash("hash");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThrows(SecurityException.class, () ->
                userService.updateUsername("user", "newName", "wrong"));
    }

    @Test
    void updateUsername_should_fail_if_user_not_found() {
        User user = new User();
        when(userDao.findByEmailOrUsername("user")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUsername("user", "newName", "pass"));
    }

    // ====== VALIDATE REGISTRATION CODE ======

    @Test
    void validateRegistrationCode_should_succeed() {
        User user = new User();
        user.setStatus(Status.NOT_VALIDATED);
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(codeService.validateCode(user, "123456")).thenReturn(true);

        boolean result = userService.validateRegistrationCode("user", "123456");

        assertTrue(result);
        assertEquals(Status.VALIDATED, user.getStatus());
        verify(userDao).update(user);
    }

    @Test
    void validateRegistrationCode_should_delete_user_after_failed_attempts() {
        User user = new User();
        user.setStatus(Status.NOT_VALIDATED);
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(codeService.validateCode(user, "wrong")).thenReturn(false);

        CodeService.CodeData codeData = new CodeService.CodeData("wrong", LocalDateTime.now().minusMinutes(1));
        when(codeService.getCodeData(user.getId())).thenReturn(codeData);

        boolean result = userService.validateRegistrationCode("user", "wrong");

        assertFalse(result);
        verify(userDao).delete(user.getId());
    }

    @Test
    void validateRegistrationCode_should_return_false_if_code_invalid_and_not_expired() {
        User user = new User();
        user.setStatus(Status.NOT_VALIDATED);
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(codeService.validateCode(user, "wrong")).thenReturn(false);

        CodeService.CodeData codeData = new CodeService.CodeData("wrong", LocalDateTime.now().plusMinutes(10));
        when(codeService.getCodeData(user.getId())).thenReturn(codeData);

        boolean result = userService.validateRegistrationCode("user", "wrong");

        assertFalse(result);
        verify(userDao, never()).delete(any());
    }

    @Test
    void validateRegistrationCode_should_return_false_if_user_not_found() {

        when(userDao.findByEmailOrUsername("user")).thenReturn(null);

        boolean result = userService.validateRegistrationCode("user", "123456");

        assertFalse(result);
    }

    // ====== VALIDATE EMAIL UPDATE CODE ======

    @Test
    void validateEmailUpdate_should_succeed() {
        User user = new User();
        user.setPendingEmail("new@email.com");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(codeService.validateCode(user, "code")).thenReturn(true);

        boolean result = userService.validateEmailUpdate("user", "code");

        assertTrue(result);
        assertEquals("new@email.com", user.getEmail());
        assertNull(user.getPendingEmail());
        verify(userDao).update(user);
    }

    @Test
    void validateEmailUpdate_should_revert_pending_email_on_expired_code() {
        User user = new User();
        user.setPendingEmail("new@email.com");
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(codeService.validateCode(user, "code")).thenReturn(false);

        CodeService.CodeData codeData = new CodeService.CodeData("wrong", LocalDateTime.now().minusMinutes(1));
        when(codeService.getCodeData(user.getId())).thenReturn(codeData);

        boolean result = userService.validateEmailUpdate("user", "code");

        assertFalse(result);
        assertNull(user.getPendingEmail());
        verify(userDao).update(user);
    }

    @Test
    void validateEmailUpdate_should_handle_missing_pending_email() {
        User user = new User();
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);

        assertFalse(userService.validateEmailUpdate("user", "1234"));
    }
}
