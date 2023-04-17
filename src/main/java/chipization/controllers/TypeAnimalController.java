package chipization.controllers;

import chipization.mappers.TypeAnimalMapper;
import chipization.model.TypeAnimal;
import chipization.model.dto.TypeAnimalDto;
import chipization.services.AuthorizationService;
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
    private final AuthorizationService authorizationService;


    @GetMapping("/{typeId}")
    public ResponseEntity<TypeAnimalDto> findById(@RequestHeader(value = "Authorization", required = false) String auth,
                                                  @PathVariable Long typeId) {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(TypeAnimalMapper.toTypeAnimalDto(typeAnimalService.findById(typeId)));
    }

    @PostMapping
    public ResponseEntity<TypeAnimalDto> create(@RequestHeader(value = "Authorization", required = false) String auth,
                                                @Valid @RequestBody TypeAnimal typeAnimal) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return new ResponseEntity<>(TypeAnimalMapper.toTypeAnimalDto(typeAnimalService.create(typeAnimal)), HttpStatus.CREATED);
    }

    @PutMapping("/{typeId}")
    public ResponseEntity<TypeAnimalDto> update(@RequestHeader(value = "Authorization", required = false) String auth,
                                                @Valid @RequestBody TypeAnimal typeAnimal, @PathVariable Long typeId) {
        authorizationService.checkAuthorizationForAdminOrChipperRights(auth);
        return ResponseEntity.ok(TypeAnimalMapper.toTypeAnimalDto(typeAnimalService.update(typeId, typeAnimal)));
    }

    @DeleteMapping("/{typeId}")
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth,
                       @PathVariable Long typeId) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        typeAnimalService.delete(typeId);
    }
}
