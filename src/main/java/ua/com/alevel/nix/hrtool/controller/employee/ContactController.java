package ua.com.alevel.nix.hrtool.controller.employee;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.nix.hrtool.Routes;
import ua.com.alevel.nix.hrtool.model.employee.request.SaveContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.request.UpdateContactRequest;
import ua.com.alevel.nix.hrtool.model.employee.response.ContactResponse;
import ua.com.alevel.nix.hrtool.service.employee.ContactService;

import javax.validation.Valid;

@RestController
@RequestMapping(Routes.CONTACTS)
@Tag(name = "Employee Contact Resource")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse create(@RequestBody @Valid SaveContactRequest request) {
        return contactService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @RequestBody @Valid UpdateContactRequest request) {
        contactService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        contactService.delete(id);
    }

}
