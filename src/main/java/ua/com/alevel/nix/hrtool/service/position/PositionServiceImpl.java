package ua.com.alevel.nix.hrtool.service.position;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.com.alevel.nix.hrtool.exception.department.DepartmentException;
import ua.com.alevel.nix.hrtool.exception.position.PositionException;
import ua.com.alevel.nix.hrtool.model.department.Department;
import ua.com.alevel.nix.hrtool.model.position.Position;
import ua.com.alevel.nix.hrtool.model.position.request.SavePositionRequest;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;
import ua.com.alevel.nix.hrtool.repository.DepartmentRepository;
import ua.com.alevel.nix.hrtool.repository.PositionRepository;

import java.util.Optional;

@Service
public class PositionServiceImpl implements PositionService {

    PositionRepository positionRepository;
    DepartmentRepository departmentRepository;

    public PositionServiceImpl(PositionRepository positionRepository, DepartmentRepository departmentRepository) {
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Page<PositionResponse> findAll(Pageable pageable) {
        return positionRepository.findAll(pageable).map(PositionResponse::fromPosition);
    }

    @Override
    public PositionResponse create(SavePositionRequest request) {
        validateUnique(request);
        Optional<Department> department = validateDepartment(request);

        Position position = new Position(request.getName());
        department.ifPresent(position::setDepartment);

        return PositionResponse.fromPosition(positionRepository.save(position));
    }

    @Override
    public void update(long id, SavePositionRequest request) {
        Position position = getPosition(id);
        if (!position.getName().equals(request.getName())) {
            validateUnique(request);
        }
        Optional<Department> department = validateDepartment(request);

        position.setName(request.getName());
        position.setDepartment(department.orElse(null));

        positionRepository.save(position);
    }

    @Override
    public void delete(long id) {
        getPosition(id);
        positionRepository.deleteById(id);
    }

    @Override
    public PositionResponse getById(Long id) {
        return PositionResponse.fromPosition(getPosition(id));
    }

    private Position getPosition(long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> PositionException.positionNotFound(id));
    }

    private Optional<Department> validateDepartment(SavePositionRequest request) {
        return Optional.ofNullable(request.getDepartmentId())
                .map(departmentId -> departmentRepository.findById(departmentId)
                        .orElseThrow(() -> DepartmentException.departmentNotFound(departmentId)));
    }

    private void validateUnique(SavePositionRequest request) {
        String name = request.getName();
        if (positionRepository.existsByName(name)) {
            throw PositionException.duplicateName(name);
        }
    }
}
