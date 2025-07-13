package br.com.mkanton.cadastroapi.service;

import br.com.mkanton.cadastroapi.dataaccess.IUserDao;
import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.domain.enums.Role;
import br.com.mkanton.cadastroapi.dto.Request.QuickSearchDto;
import br.com.mkanton.cadastroapi.dto.response.PageResponseDto;
import br.com.mkanton.cadastroapi.service.interfaces.IAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

//class with methods exclusive to admin
@ApplicationScoped
public class AdminService  implements IAdminService {

    Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Inject
    private IUserDao IUserDao;

    /**
     * Method for searching users (available only for admins)
     * @param dto data to perform the search
     * @return list of users according to the searched values
     */
    public PageResponseDto<User> search(QuickSearchDto dto) {
        String value = dto.getValue();
        int page = Math.max(dto.getPage(), 0);
        int size = (dto.getSize() <= 0 || dto.getSize() > 100) ? 10 : dto.getSize();

        List<User> results = new ArrayList<>();

        // If value is empty, return all users paginated
        if (value == null || value.trim().isEmpty()) {
            List<User> allUsers = IUserDao.findAll();
            int total = allUsers.size();

            int fromIndex = Math.min(page * size, total);
            int toIndex = Math.min(fromIndex + size, total);

            List<User> pagedUsers = allUsers.subList(fromIndex, toIndex);
            return new PageResponseDto<>(pagedUsers, page, size, total);
        }

        // Search by ID if it's a number
        if (value.matches("\\d+")) {
            try {
                Long id = Long.parseLong(value);
                User user = IUserDao.findById(id);
                if (user != null) results.add(user);
            } catch (Exception e) {
                logger.warn("Invalid ID format: {}", value);
            }
        }

        // Search by partial email (case-insensitive)
        try {
            List<User> emailMatches = IUserDao.findByEmail(value); // método deve ser List<User>
            for (User user : emailMatches) {
                if (!results.contains(user)) results.add(user);
            }
        } catch (Exception e) {
            logger.warn("No users found by email containing: {}", value);
        }

        // Search by partial username (case-insensitive)
        try {
            List<User> usernameMatches = IUserDao.findByUsername(value);
            for (User user : usernameMatches) {
                if (!results.contains(user)) results.add(user);
            }
        } catch (Exception e) {
            logger.warn("No users found by username containing: {}", value);
        }

        // Paginate results
        int total = results.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<User> pagedResults = results.subList(fromIndex, toIndex);

        return new PageResponseDto<>(pagedResults, page, size, total);
    }
    /**
     * Method to promote a user to system admin please read the documentation to understand about adminis.
     * @param userId User ID to be promoted to administrator.
     * @Return user with admin role or operation validation error
     */
    public void promoteUserToAdmin(Long userId) {
        User user = IUserDao.findById(userId);
        if (user == null) {
            logger.error("User not found");
            throw new IllegalArgumentException("User not found.");
        }

        if (user.getRole() == Role.ADMIN) {
            logger.error("user is already an admin");
            throw new IllegalStateException("User is already an admin");
        }

        user.setRole(Role.ADMIN);
        IUserDao.update(user);
        logger.info("Admin role is promoted");
    }
}
