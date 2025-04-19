package com.example.Refactoring.controller;

/*
 @author K1ngst0n
 @project Refactoring
 @class PatientRestController
 @version 1.0.0
 @since 4/19/2025 - 12:25 PM
*/


import com.example.Refactoring.model.Patient;
import com.example.Refactoring.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(patients);
    }

    // Отримання пацієнта за ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable String id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        if (patient.isPresent()) {
            return ResponseEntity.ok(patient.get());
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        if (patient.getFirstName() == null || patient.getLastName() == null || patient.getEmail() == null) {
            return ResponseEntity.badRequest().build();
        }
        Patient createdPatient = patientService.createPatient(patient);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable String id, @RequestBody Patient patient) {
        Optional<Patient> existingPatient = patientService.getPatientById(id);
        if (existingPatient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Оновлюємо пацієнта
        patient.setId(id);
        Patient updatedPatient = patientService.updatePatient(id, patient);
        return ResponseEntity.ok(updatedPatient);
    }

    // Видалення пацієнта
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        Optional<Patient> existingPatient = patientService.getPatientById(id);
        if (existingPatient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}