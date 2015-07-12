package nebusers.repository;

import java.util.Optional;

import nebusers.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByLogin(String login);
    
    Optional<User> findOneById(Long id);

    @Override
    void delete(User t);

}
