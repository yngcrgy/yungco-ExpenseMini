package edu.cit.yungco.expensemini.repository;

import edu.cit.yungco.expensemini.model.PasswordResetToken;
import edu.cit.yungco.expensemini.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
}
