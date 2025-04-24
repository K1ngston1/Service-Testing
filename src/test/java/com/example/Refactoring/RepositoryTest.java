package com.example.Refactoring;

import com.example.Refactoring.model.Patient;
import com.example.Refactoring.repository.PatientRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
public class RepositoryTest {

    @Autowired
    PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        // Додаємо тестові дані перед кожним тестом
        patientRepository.saveAll(List.of(
                new Patient("John", "Doe", "1990-01-01", "Male", "john.doe@test.com"),
                new Patient("Jane", "Smith", "1985-05-15", "Female", "jane.smith@test.com"),
                new Patient("Mike", "Johnson", "1978-11-20", "Male", "mike.johnson@test.com")
        ));
    }

    @AfterEach
    void tearDown() {
        // Видаляємо всі тестові дані
        patientRepository.deleteAll();
    }

    @Test
    void shouldAssignIdWhenSavingNewPatient() {
        // Тестує, чи присвоюється ID новому пацієнту після збереження
        Patient patient = new Patient("New", "Patient", "2000-01-01", "Other", "new.patient@test.com");
        Patient savedPatient = patientRepository.save(patient);
        assertNotNull(savedPatient.getId());
    }

    @Test
    void shouldFindPatientById() {
        // Тестує пошук пацієнта за ID
        Patient savedPatient = patientRepository.save(new Patient("Find", "Me", "1995-02-02", "Female", "find.me@test.com"));
        Optional<Patient> foundPatient = patientRepository.findById(savedPatient.getId());
        assertTrue(foundPatient.isPresent());
        assertEquals("Find", foundPatient.get().getFirstName());
    }

    @Test
    void shouldUpdatePatientInformation() {
        // Тестує оновлення інформації пацієнта
        Patient savedPatient = patientRepository.save(new Patient("Original", "Name", "1980-03-03", "Male", "original.name@test.com"));
        savedPatient.setFirstName("Updated");
        savedPatient.setLastName("Information");
        patientRepository.save(savedPatient);
        Optional<Patient> updatedPatient = patientRepository.findById(savedPatient.getId());
        assertEquals("Updated", updatedPatient.get().getFirstName());
    }

    @Test
    void shouldDeletePatient() {
        // Тестує видалення пацієнта
        Patient savedPatient = patientRepository.save(new Patient("ToDelete", "Patient", "1999-04-04", "Female", "delete.me@test.com"));
        patientRepository.deleteById(savedPatient.getId());
        assertFalse(patientRepository.findById(savedPatient.getId()).isPresent());
    }

    @Test
    void shouldFindAllPatients() {
        // Тестує отримання всіх пацієнтів
        List<Patient> patients = patientRepository.findAll();
        assertFalse(patients.isEmpty());
    }

    @Test
    void shouldNotFindNonExistentPatient() {
        // Тестує пошук неіснуючого пацієнта
        Optional<Patient> patient = patientRepository.findById("nonexistent-id-123");
        assertFalse(patient.isPresent());
    }

    @Test
    void shouldSaveMultiplePatientsAtOnce() {
        // Тестує масове збереження
        List<Patient> patients = List.of(
                new Patient("Multi1", "Patient", "2001-01-01", "Male", "multi1@test.com"),
                new Patient("Multi2", "Patient", "2002-02-02", "Female", "multi2@test.com")
        );
        patientRepository.saveAll(patients);
        List<Patient> allPatients = patientRepository.findAll();
        assertTrue(allPatients.size() >= 5); // бо +3 в setUp()
    }

    @Test
    void shouldAssignUniqueIdsToPatients() {
        // Тестує унікальність ID
        Patient saved1 = patientRepository.save(new Patient("Unique1", "Id", "1991-01-01", "Male", "unique1@test.com"));
        Patient saved2 = patientRepository.save(new Patient("Unique2", "Id", "1992-02-02", "Female", "unique2@test.com"));
        assertNotEquals(saved1.getId(), saved2.getId());
    }

    @Test
    void shouldHandleEmptyFirstNameOnSave() {
        // Тестує збереження з порожнім ім’ям
        Patient saved = patientRepository.save(new Patient("", "EmptyFirst", "2000-01-01", "Other", "empty.first@test.com"));
        assertEquals("", saved.getFirstName());
    }

    @Test
    void shouldHandleNullFieldsOnSave() {
        // Тестує збереження з null-полями
        Patient saved = patientRepository.save(new Patient(null, "NullFields", null, null, "null.fields@test.com"));
        assertNull(saved.getFirstName());
    }

    @Test
    void shouldUpdateOnlySpecificFields() {
        // Тестує часткове оновлення
        Patient saved = patientRepository.save(new Patient("Original", "Patient", "1980-01-01", "Male", "original@test.com"));
        saved.setFirstName("Updated");
        patientRepository.save(saved);
        Optional<Patient> updated = patientRepository.findById(saved.getId());
        assertEquals("Patient", updated.get().getLastName());
    }

    @Test
    void shouldCountPatients() {
        // Тестує підрахунок кількості пацієнтів
        long countBefore = patientRepository.count();
        patientRepository.save(new Patient("Count", "Test", "2000-01-01", "Male", "count.test@test.com"));
        long countAfter = patientRepository.count();
        assertEquals(countBefore + 1, countAfter);
    }

    @Test
    void shouldDeleteAllPatientsWithoutAffectingOtherTests() {
        // Тестує повне очищення (окремо іменований щоб не заважав іншим)
        patientRepository.save(new Patient("Delete", "All", "1999-01-01", "Male", "delete.all@test.com"));
        patientRepository.deleteAll();
        assertEquals(0, patientRepository.count());
    }

    @Test
    void shouldHandleLargeNumberOfPatients() {
        // Тестує збереження великої кількості пацієнтів
        List<Patient> patients = List.of(
                new Patient("Patient1", "Test", "2001-01-01", "Male", "patient1@test.com"),
                new Patient("Patient2", "Test", "2002-02-02", "Female", "patient2@test.com"),
                new Patient("Patient3", "Test", "2003-03-03", "Male", "patient3@test.com")
        );
        patientRepository.saveAll(patients);
        List<Patient> all = patientRepository.findAll();
        assertTrue(all.size() >= 6); // 3 з setUp + 3 додані тут
    }

    @Test
    void shouldMaintainDataConsistencyAfterUpdate() {
        // Тестує консистентність даних після оновлення
        Patient saved = patientRepository.save(new Patient("Consistency", "Test", "1990-01-01", "Male", "consistency@test.com"));
        saved.setEmail("updated.email@test.com");
        patientRepository.save(saved);
        Optional<Patient> updated = patientRepository.findById(saved.getId());
        assertEquals("updated.email@test.com", updated.get().getEmail());
    }
}
