package ua.com.alevel.nix.hrtool.service.position;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ua.com.alevel.nix.hrtool.model.department.Department;
import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;
import ua.com.alevel.nix.hrtool.model.position.Position;
import ua.com.alevel.nix.hrtool.model.position.request.SavePositionRequest;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;
import ua.com.alevel.nix.hrtool.repository.DepartmentRepository;
import ua.com.alevel.nix.hrtool.repository.PositionRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PositionServiceImplTest {

    private PositionRepository positionRepository;

    private DepartmentRepository departmentRepository;

    private PositionServiceImpl positionService;

    @BeforeEach
    void setUp() {
        positionRepository = mock(PositionRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        positionService = new PositionServiceImpl(positionRepository, departmentRepository);
    }

    @Test
    void findAll() {
        Position pos1 = new Position(1L, "position1");
        Position pos2 = new Position(2L, "position2");
        Department department = new Department(1L, "department");
        pos1.setDepartment(department);
        Page<Position> pageablePositions = new PageImpl<>(List.of(pos1, pos2));
        Pageable pageable = PageRequest.of(0, 20);

        when(positionRepository.findAll(pageable)).thenReturn(pageablePositions);

        Page<PositionResponse> positionResponses = positionService.findAll(pageable);

        assertEquals(pos1.getId(), positionResponses.getContent().get(0).getId());
        assertEquals(pos1.getName(), positionResponses.getContent().get(0).getName());
        assertEquals(DepartmentResponse.fromDepartmentWithBasicAttributes(department),
                positionResponses.getContent().get(0).getDepartment());

        assertEquals(pos2.getId(), positionResponses.getContent().get(1).getId());
        assertEquals(pos2.getName(), positionResponses.getContent().get(1).getName());
        assertNull(positionResponses.getContent().get(1).getDepartment());
        verify(positionRepository).findAll(pageable);

        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    void create() {
        testDuplicatedNameOnCreate();
        testInvalidDepartmentOnCreate();
        testValidDataOnCreate();

        verifyNoMoreInteractions(positionRepository, departmentRepository);
    }

    private void testDuplicatedNameOnCreate() {
        SavePositionRequest duplicateNameRequest = new SavePositionRequest(null, "duplicateName");

        when(positionRepository.existsByName(duplicateNameRequest.getName())).thenReturn(true);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> positionService.create(duplicateNameRequest))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
        verify(positionRepository).existsByName(duplicateNameRequest.getName());
    }

    private void testInvalidDepartmentOnCreate() {
        SavePositionRequest invalidDepartmentRequest = new SavePositionRequest(100L, "position");

        when(positionRepository.existsByName(invalidDepartmentRequest.getName())).thenReturn(false);
        when(departmentRepository.findById(invalidDepartmentRequest.getDepartmentId())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> positionService.create(invalidDepartmentRequest))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(positionRepository).existsByName(invalidDepartmentRequest.getName());
        verify(departmentRepository).findById(invalidDepartmentRequest.getDepartmentId());
    }

    private void testValidDataOnCreate() {
        Department department = new Department(1L, "department");
        SavePositionRequest request = new SavePositionRequest(1L, "validPosition");
        long positionId = 1;

        when(positionRepository.existsByName(request.getName())).thenReturn(false);
        when(departmentRepository.findById(request.getDepartmentId())).thenReturn(Optional.of(department));

        when(positionRepository.save(notNull())).thenAnswer(invocation -> {
            Position position = invocation.getArgument(0);
            assertNull(position.getId());
            assertEquals(request.getName(), position.getName());
            assertEquals(department, position.getDepartment());
            position.setId(positionId);
            return position;
        });

        PositionResponse response = positionService.create(request);

        assertEquals(positionId, response.getId());
        assertEquals(request.getName(), response.getName());
        assertEquals(response.getDepartment(), DepartmentResponse.fromDepartmentWithBasicAttributes(department));
        verify(positionRepository).existsByName(request.getName());
        verify(departmentRepository).findById(request.getDepartmentId());
        verify(positionRepository).save(notNull());
    }

    @Test
    void update() {
        testInvalidPositionOnUpdate();
        testDuplicatedNameOnUpdate();
        testInvalidDepartmentOnUpdate();
        testValidDataOnUpdate();

        verifyNoMoreInteractions(positionRepository, departmentRepository);

    }

    private void testInvalidPositionOnUpdate() {
        long absentId = 10;
        SavePositionRequest request = new SavePositionRequest(null, "invalidPosition");
        when(positionRepository.findById(absentId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> positionService.update(absentId, request))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(positionRepository).findById(absentId);
    }

    private void testDuplicatedNameOnUpdate() {
        long presentId = 11;
        Position position = new Position(presentId, "position");
        SavePositionRequest duplicateNameRequest = new SavePositionRequest(null, "duplicateName");

        when(positionRepository.findById(presentId)).thenReturn(Optional.of(position));
        when(positionRepository.existsByName(duplicateNameRequest.getName())).thenReturn(true);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> positionService.update(presentId, duplicateNameRequest))
                .satisfies(e -> assertEquals(HttpStatus.BAD_REQUEST, e.getStatus()));
        verify(positionRepository).findById(presentId);
        verify(positionRepository).existsByName(duplicateNameRequest.getName());
    }

    private void testInvalidDepartmentOnUpdate() {
        long presentId = 12;
        Position position = new Position(presentId, "position");
        SavePositionRequest invalidDepartmentRequest = new SavePositionRequest(100L, "invalidDepartment");

        when(positionRepository.findById(presentId)).thenReturn(Optional.of(position));
        when(positionRepository.existsByName(invalidDepartmentRequest.getName())).thenReturn(false);
        when(departmentRepository.findById(invalidDepartmentRequest.getDepartmentId())).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> positionService.update(presentId, invalidDepartmentRequest))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(positionRepository).findById(presentId);
        verify(positionRepository).existsByName(invalidDepartmentRequest.getName());
        verify(departmentRepository).findById(invalidDepartmentRequest.getDepartmentId());
    }

    private void testValidDataOnUpdate() {
        long presentId = 1;
        SavePositionRequest request = new SavePositionRequest(2L, "newPositionName");
        Department department = new Department(1L, "department");
        Department otherDepartment = new Department(2L, "otherDepartment");
        Position position = new Position(presentId, "position");
        position.setDepartment(department);

        when(positionRepository.findById(presentId)).thenReturn(Optional.of(position));
        when(positionRepository.existsByName(request.getName())).thenReturn(false);
        when(departmentRepository.findById(request.getDepartmentId())).thenReturn(Optional.of(otherDepartment));

        positionService.update(presentId, request);

        assertEquals(request.getName(), position.getName());
        assertEquals(request.getDepartmentId(), position.getDepartment().getId());

        verify(positionRepository).findById(presentId);
        verify(positionRepository).existsByName(request.getName());
        verify(departmentRepository).findById(request.getDepartmentId());
        verify(positionRepository).save(position);
    }

    @Test
    void delete() {
        long absentId = 10;
        long presentId = 1;
        Position position = new Position(presentId, "position");

        when(positionRepository.findById(absentId)).thenReturn(Optional.empty());
        when(positionRepository.findById(presentId)).thenReturn(Optional.of(position));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> positionService.delete(absentId))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(positionRepository).findById(absentId);

        positionService.delete(presentId);
        verify(positionRepository).findById(presentId);
        verify(positionRepository).deleteById(presentId);

        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    void getById() {
        long absentId = 10;
        long presentId = 1;
        Position position = new Position(presentId, "position");
        Department department = new Department(1L, "department");
        position.setDepartment(department);

        when(positionRepository.findById(absentId)).thenReturn(Optional.empty());
        when(positionRepository.findById(presentId)).thenReturn(Optional.of(position));

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> positionService.getById(absentId))
                .satisfies(e -> assertEquals(HttpStatus.NOT_FOUND, e.getStatus()));
        verify(positionRepository).findById(absentId);

        PositionResponse response = positionService.getById(presentId);

        assertEquals(position.getId(), response.getId());
        assertEquals(position.getName(), response.getName());
        assertEquals(DepartmentResponse.fromDepartmentWithBasicAttributes(department), response.getDepartment());
        verify(positionRepository).findById(presentId);

        verifyNoMoreInteractions(positionRepository);
    }
}