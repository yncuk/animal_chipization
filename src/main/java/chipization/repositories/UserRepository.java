package chipization.repositories;

import chipization.model.User;
import chipization.model.dto.GetUsersRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(" select u from User u " +
            "where lower(u.firstName) like lower(concat('%',?1,'%')) and " +
            "lower(u.lastName) like lower(concat('%',?1,'%')) and " +
            "lower(u.email) like lower(concat('%',?1,'%')) " +
            "order by u.id asc")
    List<User> search(GetUsersRequest request);


   // User updateById(Integer accountId, User user);

}
