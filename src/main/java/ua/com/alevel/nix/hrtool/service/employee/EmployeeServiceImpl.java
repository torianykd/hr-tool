package ua.com.alevel.nix.hrtool.service.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alevel.nix.hrtool.exception.employee.EmployeeException;
import ua.com.alevel.nix.hrtool.model.employee.Contact;
import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.employee.EmployeeName;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveEmployeeRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.EmployeeResponse;
import ua.com.alevel.nix.hrtool.model.position.Position;
import ua.com.alevel.nix.hrtool.repository.ContactRepository;
import ua.com.alevel.nix.hrtool.repository.EmployeeRepository;
import ua.com.alevel.nix.hrtool.repository.PositionRepository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public Page<EmployeeResponse> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(EmployeeResponse::fromEmployee);
    }

    @Override
    @Transactional
    public EmployeeResponse create(SaveEmployeeRequest request) {
        validateUnique(request);
        List<Position> positions = validateAndGetPositions(request);

        Employee employee = new Employee(request);
        employee.setPositions(new HashSet<>(positions));
        Employee savedEmployee = employeeRepository.save(employee);

        return EmployeeResponse.fromEmployee(savedEmployee);
    }

    @Override
    public EmployeeResponse getById(long id) {
        return EmployeeResponse.fromEmployee(getEmployee(id));
    }

    @Override
    public void update(long id, SaveEmployeeRequest request) {
        Employee employee = getEmployee(id);
        if (!request.getEmail().equals(employee.getEmail())) {
            validateUnique(request);
        }
        List<Position> positions = validateAndGetPositions(request);

        employee.setEmail(request.getEmail());
        employee.setEmployeeName(new EmployeeName(request.getFirstName(), request.getLastName()));
        employee.setBirthDate(request.getBirthDate());
        employee.setHiringDate(request.getHiringDate());
        employee.setPositions(new HashSet<>(positions));
        employeeRepository.save(employee);
    }

    @Override
    public void deleteById(long id) {
        getEmployee(id);
        employeeRepository.deleteById(id);
    }

    private Employee getEmployee(long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> EmployeeException.employeeNotFound(id));
    }

    private List<Position> validateAndGetPositions(SaveEmployeeRequest request) {
        List<Long> positionIds = request.getPositionIds();
        List<Position> foundPositions = positionRepository.findAllById(positionIds);
        if (positionIds.size() != foundPositions.size()) {
            throw EmployeeException.positionsNotExist();
        }
        return foundPositions;
    }

    private void validateUnique(SaveEmployeeRequest request) {
        String email = request.getEmail();
        if (employeeRepository.existsByEmail(email)) {
            throw EmployeeException.duplicateEmail(email);
        }
    }

}
