package ua.com.alevel.nix.hrtool.service.leaverequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.com.alevel.nix.hrtool.exception.employee.EmployeeException;
import ua.com.alevel.nix.hrtool.exception.leaverequest.LeaveRequestException;
import ua.com.alevel.nix.hrtool.model.employee.Employee;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.LeaveRequestStatus;
import ua.com.alevel.nix.hrtool.model.leaverequest.request.SaveLeaveRequest;
import ua.com.alevel.nix.hrtool.model.leaverequest.response.LeaveRequestResponse;
import ua.com.alevel.nix.hrtool.repository.EmployeeRepository;
import ua.com.alevel.nix.hrtool.repository.LeaveRequestRepository;

import java.util.List;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final EmployeeRepository employeeRepository;

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestServiceImpl(EmployeeRepository employeeRepository, LeaveRequestRepository leaveRequestRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }


    @Override
    public Page<LeaveRequestResponse> findAll(Pageable pageable, String employeeEmail) {
        Employee employee = getEmployee(employeeEmail);
        return leaveRequestRepository.findAllByEmployeeOrderByIdDesc(pageable, employee)
                .map(LeaveRequestResponse::fromLeaveRequestWithBasicAttributes);
    }

    @Override
    public LeaveRequestResponse create(SaveLeaveRequest request, String employeeEmail) {
        Employee employee = getEmployee(employeeEmail);
        validateRequestAvailability(request, employee);

        LeaveRequest leaveRequest = new LeaveRequest(request);
        leaveRequest.setEmployee(employee);

        return LeaveRequestResponse.fromLeaveRequestWithBasicAttributes(
                leaveRequestRepository.save(leaveRequest)
        );
    }

    @Override
    public void approve(long id) {
        updateStatus(getLeaveRequest(id), LeaveRequestStatus.APPROVED);
    }

    @Override
    public void decline(long id) {
        updateStatus(getLeaveRequest(id), LeaveRequestStatus.DECLINED);
    }

    private LeaveRequest getLeaveRequest(long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> LeaveRequestException.requestNotFound(id));
    }

    private Employee getEmployee(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> EmployeeException.employeeNotFound(email));
    }

    private void validateRequestAvailability(SaveLeaveRequest request, Employee employee) {
        List<LeaveRequestStatus> statusList = List.of(LeaveRequestStatus.PENDING, LeaveRequestStatus.APPROVED);
        List<LeaveRequest> existingRequests = leaveRequestRepository.requestsForPeriod(
                employee, request.getStart(), request.getEnd(), statusList
        );

        if (!existingRequests.isEmpty()) {
            throw LeaveRequestException.requestAlreadyExists();
        }
    }

    private void updateStatus(LeaveRequest request, LeaveRequestStatus status) {
        if (request.getStatus().equals(LeaveRequestStatus.PENDING)) {
            request.setStatus(status);
            leaveRequestRepository.save(request);
        }
    }
}
