package chipization.repositories;

import chipization.model.User;
import chipization.model.dto.GetUsersRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = " select * from users u " +
            "where lower(u. first_name) like lower(concat('%',?1,'%')) and " +
            "lower(u.last_name) like lower(concat('%',?2,'%')) and " +
            "lower(u.email) like lower(concat('%',?3,'%')) " +
            "order by u.user_id asc " +
            "limit ?4 offset ?5", nativeQuery = true)
    List<User> search(String firstName, String lastName, String email, int size, int from);

    @Query(" select u from User u " +
            "where u.email = ?1 and u.password  = ?2")
    Optional<User> authorization(String login, String password);

   // User updateById(Integer accountId, User user);

}
