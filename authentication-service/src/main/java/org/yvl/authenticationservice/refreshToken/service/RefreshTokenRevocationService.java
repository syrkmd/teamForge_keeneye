package org.yvl.authenticationservice.refreshToken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.authenticationservice.entity.User;
import org.yvl.authenticationservice.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class RefreshTokenRevocationService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeAll(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}
