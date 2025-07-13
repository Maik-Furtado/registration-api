package br.com.mkanton.cadastroapi.service.interfaces;

import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.dto.Request.QuickSearchDto;
import br.com.mkanton.cadastroapi.dto.response.PageResponseDto;

public interface IAdminService {
    PageResponseDto<User> search(QuickSearchDto dto);
    void promoteUserToAdmin(Long userId);
}
