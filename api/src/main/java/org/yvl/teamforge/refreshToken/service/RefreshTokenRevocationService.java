package org.yvl.teamforge.refreshToken.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.yvl.teamforge.entity.User;
import org.yvl.teamforge.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class RefreshTokenRevocationService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeAll(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}
