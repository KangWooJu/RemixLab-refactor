package org.woojukang.remixlab.global.security.facade;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.global.security.dto.response.ReissueResponse;
import org.woojukang.remixlab.global.security.service.RefreshService;

@Component
@RequiredArgsConstructor
public class RefreshFacade {

    private final RefreshService refreshService;

    @Transactional
    public ReissueResponse refreshCookies(HttpServletRequest request){

        return refreshService.refreshCookies(request);

    }
}
