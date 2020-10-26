package ua.com.alevel.nix.hrtool.model.position.response;

import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;
import ua.com.alevel.nix.hrtool.model.position.Position;

import java.util.Objects;
import java.util.Optional;

public class PositionResponse {

    private long id;

    private String name;

    private DepartmentResponse department;

    public PositionResponse(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static PositionResponse fromPositionWithBasicAttributes(Position position) {
        return new PositionResponse(position.getId(), position.getName());
    }

    public static PositionResponse fromPosition(Position position) {
        PositionResponse positionResponse = fromPositionWithBasicAttributes(position);
        positionResponse.department = Optional.ofNullable(position.getDepartment())
                .map(DepartmentResponse::fromDepartmentWithBasicAttributes)
                .orElse(null);
        return positionResponse;
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

    public DepartmentResponse getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentResponse department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionResponse)) return false;
        PositionResponse that = (PositionResponse) o;
        return id == that.id &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
