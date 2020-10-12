package ua.com.alevel.nix.hrtool.controller.position;

import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;
import ua.com.alevel.nix.hrtool.service.position.PositionService;

@RestController
@RequestMapping("positions")
public class PositionController {

    PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    @PageableAsQueryParam
    public Page<PositionResponse> listPositions(@Parameter(hidden = true) Pageable pageable) {
        return positionService.findAll(pageable).map(PositionResponse::fromPosition);
    }
}
