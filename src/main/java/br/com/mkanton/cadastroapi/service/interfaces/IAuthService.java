package br.com.mkanton.cadastroapi.service.interfaces;

import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.dto.Request.LoginDto;

public interface IAuthService {
    User authenticate(LoginDto loginDto);

     public void logout(String token);
}
