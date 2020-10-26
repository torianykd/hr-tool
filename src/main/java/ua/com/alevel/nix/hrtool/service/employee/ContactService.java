package ua.com.alevel.nix.hrtool.service.employee;

import ua.com.alevel.nix.hrtool.model.employee.request.SaveContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.request.UpdateContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.ContactResponse;

public interface ContactService {

    ContactResponse create(SaveContactRequest request);

    void update(long contactId, UpdateContactRequest request);

    void delete(long contactId);
}
