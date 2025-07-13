package br.com.mkanton.cadastroapi.service.interfaces;

import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.dto.Request.*;

public interface IUpdatePasswordService {

    User startPasswordUpdate(StartUpdatePasswordDto dto);

    boolean validateCodePswUpdate(String username,ValidatePasswordUpdateCodeDto dto);

    User updatePassword(NewPasswordDto dto,String username);
}
