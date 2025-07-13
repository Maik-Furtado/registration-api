package br.com.mkanton.cadastroapi.service;

import br.com.mkanton.cadastroapi.dataaccess.IUserDao;
import br.com.mkanton.cadastroapi.domain.PasswordEncoder;
import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.domain.enums.Status;
import br.com.mkanton.cadastroapi.dto.Request.NewPasswordDto;
import br.com.mkanton.cadastroapi.dto.Request.StartUpdatePasswordDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdatePasswordServiceTest {

    @Mock
    private IUserDao userDao;

    @Mock
    private CodeService codeService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UpdatePasswordService service;

    @Test
    void startPasswordUpdate_should_succeed() {
        User user = new User();
        user.setEmail("user@email.com");

        when(userDao.findByEmail("user@email.com")).thenReturn(Arrays.asList(user));
        when(codeService.generateCode(user)).thenReturn("code123");

        StartUpdatePasswordDto dto = new StartUpdatePasswordDto("user@email.com");
        User result = service.startPasswordUpdate(dto);

        assertEquals(Status.NOT_VALIDATED, user.getStatus());
        verify(userDao).update(user);
        verify(emailService).sendEmail(eq(user.getEmail()), eq("code123"));
    }

    @Test
    void updatePassword_should_fail_if_user_not_validated() {
        User user = new User();
        user.setStatus(Status.NOT_VALIDATED);
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);

        NewPasswordDto dto = new NewPasswordDto("newpass", "newpass");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.updatePassword(dto, "user"));
        assertEquals("User not validated.", ex.getMessage());
    }

    @Test
    void updatePassword_should_fail_if_passwords_do_not_match() {
        User user = new User();
        user.setStatus(Status.VALIDATED);
        when(userDao.findByEmailOrUsername("user")).thenReturn(user);

        NewPasswordDto dto = new NewPasswordDto("strongPassword1", "strongPassword2");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.updatePassword(dto, "user"));
        assertEquals("Passwords do not match.", ex.getMessage());
    }

    @Test
    void updatePassword_should_succeed() {
        User user = new User();
        user.setStatus(Status.VALIDATED);
        user.setEmail("user@email.com");

        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.hashPassword("newpass123")).thenReturn("hashed");

        NewPasswordDto dto = new NewPasswordDto("newpass123", "newpass123");;
        User result = service.updatePassword(dto, "user");

        assertEquals("hashed", user.getPasswordHash());
        verify(userDao).update(user);
        verify(emailService).sendEmail(eq(user.getEmail()), any());
    }
}
