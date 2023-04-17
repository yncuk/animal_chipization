package chipization.services.impl;

import chipization.exceptions.EntityBadRequestException;
import chipization.exceptions.EntityForbiddenException;
import chipization.mappers.UserMapper;
import chipization.model.User;
import chipization.model.dto.GetUsersRequest;
import chipization.model.dto.UserDto;
import chipization.model.enums.UserRole;
import chipization.repositories.AnimalRepository;
import chipization.repositories.UserRepository;
import chipization.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Base64;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;

    @Override
    public UserDto findById(Integer accountId) {
        validateAccountId(accountId);
        return UserMapper.toUserDto(userRepository.findById(accountId).
                orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public Collection<UserDto> search(GetUsersRequest request) {
        if (request.getSize() <= 0 || request.getFrom() < 0) {
            throw new EntityBadRequestException("Количество пропущенных элементов и страниц не должно быть меньше 0");
        }
        return UserMapper.allToUserDto(userRepository.search(request.getFirstName(), request.getLastName(), request.getEmail(), request.getSize(), request.getFrom()));
    }

    @Override
    public UserDto createUser(User user) {
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(String auth, Integer accountId, User user) {
        userRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        if (userRepository.authorization(getLogin(auth), getPassword(auth)).isPresent()) {
            User updatingUser = userRepository.authorization(getLogin(auth), getPassword(auth)).get();
            if (updatingUser.getId() != accountId && (updatingUser.getRole() == UserRole.USER
                    ||  updatingUser.getRole() == UserRole.CHIPPER)) {
                throw new EntityForbiddenException("Нельзя обновлять не свой аккаунт");
            }
        }
        user.setId(accountId);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void delete(String auth, Integer accountId) {
        validateAccountId(accountId);
        if (!animalRepository.findByChipperId(accountId).isEmpty()) {
            throw new EntityBadRequestException("Аккаунт связан с животным");
        }
        userRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Не найден пользователь"));
        if (userRepository.authorization(getLogin(auth), getPassword(auth)).isPresent()) {
            User user = userRepository.authorization(getLogin(auth), getPassword(auth)).get();
            if (user.getId() != accountId && (user.getRole() == UserRole.USER
                    ||  user.getRole() == UserRole.CHIPPER)) {
                throw new EntityForbiddenException("Нельзя удалить не свой аккаунт");
            }
        }
        userRepository.deleteById(accountId);
    }

    private String getLogin(String auth) {
        auth = auth.substring(auth.indexOf(' ') + 1);
        byte[] decodedBytes = Base64.getDecoder().decode(auth);
        String result = new String(decodedBytes);
        String[] res = result.split(":");
        return res[0];
    }

    private String getPassword(String auth) {
        auth = auth.substring(auth.indexOf(' ') + 1);
        byte[] decodedBytes = Base64.getDecoder().decode(auth);
        String result = new String(decodedBytes);
        String[] res = result.split(":");
        return res[1];
    }

    private void validateAccountId(Integer accountId) {
        if (accountId <= 0) {
            throw new EntityBadRequestException("ID аккаунта должен быть больше 0");
        }
    }
}