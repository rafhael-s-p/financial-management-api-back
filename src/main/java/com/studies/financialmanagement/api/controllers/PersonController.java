package com.studies.financialmanagement.api.controllers;

import com.studies.financialmanagement.api.event.CreatedResourceEvent;
import com.studies.financialmanagement.api.models.Person;
import com.studies.financialmanagement.api.repositories.PersonRepository;
import com.studies.financialmanagement.api.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private PersonService service;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SEARCH_PERSON')")
    public Page<Person> search(@RequestParam(required = false, defaultValue = "%") String name, Pageable pageable) {
        return repository.findByNameContaining(name, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SEARCH_PERSON') and #oauth2.hasScope('read')")
    public ResponseEntity<Person> findById(@PathVariable Long id) {
        Optional<Person> person = repository.findById(id);
        if (person.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(person.get());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SAVE_PERSON') and #oauth2.hasScope('write')")
    public ResponseEntity<Person> save(@RequestBody @Valid Person person, HttpServletResponse response) {
        Person savedPerson = service.save(person);

        publisher.publishEvent(new CreatedResourceEvent(this, response, savedPerson.getId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SAVE_PERSON') and #oauth2.hasScope('write')")
    public ResponseEntity<Person> update(@PathVariable Long id, @RequestBody @Valid Person person) {
        Person savedPerson = service.update(id, person);

        return ResponseEntity.ok(savedPerson);
    }

    @PutMapping("/{id}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_SAVE_PERSON') and #oauth2.hasScope('write')")
    public void updateActiveProperty(@PathVariable Long id, @RequestBody Boolean active) {
        service.updateActiveProperty(id, active);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_DELETE_PERSON') and #oauth2.hasScope('write')")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

}
