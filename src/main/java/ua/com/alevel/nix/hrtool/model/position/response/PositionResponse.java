package ua.com.alevel.nix.hrtool.model.position.response;

import ua.com.alevel.nix.hrtool.model.department.response.DepartmentResponse;
import ua.com.alevel.nix.hrtool.model.position.Position;

import java.util.Optional;

public class PositionResponse {

    private long id;

    private String name;

    private DepartmentResponse department;

    public static PositionResponse fromPositionWithBasicAttributes(Position position) {
        PositionResponse positionResponse = new PositionResponse();
        positionResponse.id = position.getId();
        positionResponse.name = position.getName();
        return positionResponse;
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
}
