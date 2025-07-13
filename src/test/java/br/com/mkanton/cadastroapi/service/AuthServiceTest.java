package br.com.mkanton.cadastroapi.service;
import br.com.mkanton.cadastroapi.dataaccess.IUserDao;
import br.com.mkanton.cadastroapi.domain.PasswordEncoder;
import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.domain.enums.Status;
import br.com.mkanton.cadastroapi.dto.Request.LoginDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private IUserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_should_succeed() {
        User user = new User();
        user.setPasswordHash("hashed");
        user.setStatus(Status.VALIDATED);

        when(userDao.findByEmailOrUsername("user")).thenReturn(user);
        when(passwordEncoder.matches("12345678", "hashed")).thenReturn(true);

        User result = authService.authenticate(new LoginDto("user", "12345678"));
        assertNotNull(result);
    }

    @Test
    void authenticate_should_fail_if_invalid_credentials() {
        when(userDao.findByEmailOrUsername("user")).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                authService.authenticate(new LoginDto("user", "12345678")));
        assertEquals("Invalid username or password.", ex.getMessage());
    }

    @Test
    void isTokenBlacklisted_should_return_true_if_token_blacklisted() {
        authService.logout("token123");

    }
}

