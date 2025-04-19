package com.example.Refactoring.repository;

import com.example.Refactoring.model.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/*
 @author K1ngst0n
 @project Refactoring
 @class  PatientRepository
 @version 1.0.0
 @since 4/19/2025 - 12:28 PM
*/
@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

}