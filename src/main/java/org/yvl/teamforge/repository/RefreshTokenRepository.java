package org.yvl.teamforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yvl.teamforge.entity.RefreshToken;
import org.yvl.teamforge.entity.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByJti(String jti);

    @Modifying(clearAutomatically = true)
    @Query("""
                UPDATE RefreshToken r
                SET r.revoked = true
                WHERE r.user = :user
            """)
    void revokeAllByUser(@Param("user") User user);
}
