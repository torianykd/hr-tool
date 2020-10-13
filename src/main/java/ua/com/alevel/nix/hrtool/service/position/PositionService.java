package ua.com.alevel.nix.hrtool.service.position;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.alevel.nix.hrtool.model.position.Position;
import ua.com.alevel.nix.hrtool.model.position.request.SavePositionRequest;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;

public interface PositionService {

    Page<PositionResponse> findAll(Pageable pageable);

    PositionResponse create(SavePositionRequest request);

    void update(long id, SavePositionRequest request);

    void delete(long id);

    PositionResponse getById(Long id);

}
