package chipization.controllers;

import chipization.model.Analytics;
import chipization.model.dto.AreaDto;
import chipization.services.AreaService;
import chipization.services.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequestMapping("/areas")
@RequiredArgsConstructor
@Validated
public class AreaController {
    private final AreaService areaService;
    private final AuthorizationService authorizationService;

    @GetMapping("/{areaId}")
    public ResponseEntity<AreaDto> findById(@RequestHeader(value = "Authorization", required = false) String auth,
                                            @PathVariable Long areaId) {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(areaService.findById(areaId));
    }

    @PostMapping
    public ResponseEntity<AreaDto> createArea(@RequestHeader(value = "Authorization", required = false) String auth,
                                              @Valid @RequestBody AreaDto areaDto) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        return new ResponseEntity<>(areaService.createArea(areaDto), HttpStatus.CREATED);
    }

    @PutMapping("/{areaId}")
    public ResponseEntity<AreaDto> update(@RequestHeader(value = "Authorization", required = false) String auth,
                                          @RequestBody @Valid AreaDto areaDto, @PathVariable Long areaId) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        return ResponseEntity.ok(areaService.update(areaId, areaDto));
    }

    @DeleteMapping("/{areaId}")
    public void delete(@RequestHeader(value = "Authorization", required = false) String auth,
                       @PathVariable Long areaId) {
        authorizationService.checkAuthorizationForAdminRights(auth);
        areaService.delete(areaId);
    }

    @GetMapping("/{areaId}/analytics")
    public ResponseEntity<Analytics> analytics(@RequestHeader(value = "Authorization", required = false) String auth,
                                               @PathVariable Long areaId,
                                               @RequestParam(required = false) String startDate,
                                               @RequestParam(required = false) String endDate) throws ParseException {
        authorizationService.checkAuthorization(auth);
        return ResponseEntity.ok(areaService.analytics(areaId, startDate, endDate));
    }
}
