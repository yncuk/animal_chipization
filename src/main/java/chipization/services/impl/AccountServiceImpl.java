package chipization.services.impl;

import chipization.mappers.UserMapper;
import chipization.model.User;
import chipization.model.dto.GetUsersRequest;
import chipization.model.dto.UserDto;
import chipization.repositories.UserRepository;
import chipization.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;

    @Override
    public UserDto findById(Integer accountId) {
        return UserMapper.toUsetDto(userRepository.findById(accountId).
                orElseThrow(EntityNotFoundException::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserDto> search(GetUsersRequest request) {
        Page<User> usersPageSort = userRepository.findAll(PageRequest.of(1, 2, Sort.by("id")));
        //???
        return UserMapper.allToUserDto(userRepository.search(request));
    }

    @Override
    public UserDto update(Integer accountId, User user) {
        User oldUser = userRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        user.setId(accountId);
        return UserMapper.toUsetDto(userRepository.save(user));
    }

    @Override
    public void delete(Integer accountId) {
        userRepository.deleteById(accountId);
    }


}
