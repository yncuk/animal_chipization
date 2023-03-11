package chipization.controllers;

import chipization.exceptions.EntityNotAuthorizedException;
import chipization.mappers.TypeAnimalMapper;
import chipization.model.TypeAnimal;
import chipization.model.dto.TypeAnimalDto;
import chipization.services.AccountService;
import chipization.services.AnimalService;
import chipization.services.TypeAnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/animals/types")
@RequiredArgsConstructor
@Validated
public class TypeAnimalController {

    private final TypeAnimalService typeAnimalService;
    private final AccountService accountService;

    @GetMapping("/{typeId}")
    public ResponseEntity<TypeAnimalDto> findById(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long typeId) {
        accountService.checkAuthorization(auth);
        return ResponseEntity.ok(TypeAnimalMapper.toTypeAnimalDto(typeAnimalService.findById(typeId)));
    }

    @PostMapping
    public ResponseEntity<TypeAnimalDto> create(@RequestHeader(value = "Authorization", required = false) String auth, @Valid @RequestBody TypeAnimal typeAnimal) {
        if (auth == null) {
            throw new EntityNotAuthorizedException("Анонимный пользователь, запрос недоступен");
        }
        accountService.checkAuthorization(auth);
        return new ResponseEntity<>(TypeAnimalMapper.toTypeAnimalDto(typeAnimalService.create(typeAnimal)), HttpStatus.CREATED);
    }

    @PutMapping("/{typeId}")
    public ResponseEntity<TypeAnimalDto> update(@RequestHeader(value = "Authorization", required = false) String auth, @Valid @RequestBody TypeAnimal typeAnimal, @PathVariable Long typeId) {
        if (auth == null) {
            throw new EntityNotAuthorizedException("Анонимный пользователь, запрос недоступен");
        }
        accountService.checkAuthorization(auth);
        return ResponseEntity.ok(TypeAnimalMapper.toTypeAnimalDto(typeAnimalService.update(typeId, typeAnimal)));
    }

    @DeleteMapping("/{typeId}")
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth, @PathVariable Long typeId) {
        if (auth == null) {
            throw new EntityNotAuthorizedException("Анонимный пользователь, запрос недоступен");
        }
        accountService.checkAuthorization(auth);
        typeAnimalService.delete(typeId);
    }
}
