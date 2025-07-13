package br.com.mkanton.cadastroapi.dataaccess;

import br.com.mkanton.cadastroapi.domain.User;

import java.util.List;

public interface IUserDao {
    void insert(User user);

    void update(User user);

    void delete(Long id);

    List<User> findAll();

    User findById(Long id);

    List<User> findByEmail(String emailPart);

    List<User> findByUsername(String usernamePart);

    User findByEmailOrUsername(String value);

    User findExactByEmail(String email);


}
