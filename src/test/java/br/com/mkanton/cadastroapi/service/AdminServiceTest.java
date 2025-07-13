package br.com.mkanton.cadastroapi.service;

import br.com.mkanton.cadastroapi.dataaccess.IUserDao;
import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.domain.enums.Role;
import br.com.mkanton.cadastroapi.dto.Request.QuickSearchDto;
import br.com.mkanton.cadastroapi.dto.response.PageResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private IUserDao userDao;

    @InjectMocks
    private AdminService adminService;

    @Test
    void search_should_return_all_users_when_value_is_empty() {
        List<User> allUsers = Arrays.asList(new User(), new User(), new User());
        when(userDao.findAll()).thenReturn(allUsers);

        QuickSearchDto dto = new QuickSearchDto();
        dto.setValue("");
        dto.setPage(0);
        dto.setSize(10);

        PageResponseDto<User> result = adminService.search(dto);


        assertEquals(3, result.getContent().size());
    }

    @Test
    void promoteUserToAdmin_should_succeed() {
        User user = new User();
        user.setRole(Role.USER);
        when(userDao.findById(1L)).thenReturn(user);

        adminService.promoteUserToAdmin(1L);

        assertEquals(Role.ADMIN, user.getRole());
        verify(userDao).update(user);
    }

    @Test
    void promoteUserToAdmin_should_fail_if_user_not_found() {
        when(userDao.findById(1L)).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> adminService.promoteUserToAdmin(1L));
        assertEquals("User not found.", ex.getMessage());
    }

    @Test
    void promoteUserToAdmin_should_fail_if_already_admin() {
        User user = new User();
        user.setRole(Role.ADMIN);
        when(userDao.findById(1L)).thenReturn(user);

        Exception ex = assertThrows(IllegalStateException.class, () -> adminService.promoteUserToAdmin(1L));
        assertEquals("User is already an admin", ex.getMessage());
    }
}

