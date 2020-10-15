package ua.com.alevel.nix.hrtool.controller.position;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.nix.hrtool.model.position.request.SavePositionRequest;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;
import ua.com.alevel.nix.hrtool.service.position.PositionService;

import javax.validation.Valid;

@RestController
@RequestMapping("positions")
@Tag(name = "Positions Resource")
public class PositionController {

    PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    @PageableAsQueryParam
    public Page<PositionResponse> listPositions(@Parameter(hidden = true) Pageable pageable) {
        return positionService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public PositionResponse get(@PathVariable long id) {
        return positionService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PositionResponse create(@Valid @RequestBody SavePositionRequest request) {
        return positionService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @Valid @RequestBody SavePositionRequest request) {
        positionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        positionService.delete(id);
    }

}
