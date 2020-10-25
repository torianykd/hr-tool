package ua.com.alevel.nix.hrtool.model.department.response;

import ua.com.alevel.nix.hrtool.model.department.Department;
import ua.com.alevel.nix.hrtool.model.position.response.PositionResponse;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DepartmentResponse {

    private long id;

    private String name;

    private Set<PositionResponse> positions;

    public static DepartmentResponse fromDepartmentWithBasicAttributes(Department department) {
        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.id = department.getId();
        departmentResponse.name = department.getName();
        return departmentResponse;
    }

    public static DepartmentResponse fromDepartment(Department department) {
        DepartmentResponse departmentResponse = fromDepartmentWithBasicAttributes(department);
        departmentResponse.positions = Optional.ofNullable(department.getPositions())
                .map(positions -> positions.stream()
                        .map(PositionResponse::fromPositionWithBasicAttributes)
                        .collect(Collectors.toSet()))
                .orElse(null);
        return departmentResponse;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PositionResponse> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionResponse> positions) {
        this.positions = positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepartmentResponse)) return false;
        DepartmentResponse that = (DepartmentResponse) o;
        return id == that.id &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
