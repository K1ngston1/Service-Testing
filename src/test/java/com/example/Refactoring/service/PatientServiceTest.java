package com.example.Refactoring.service;
import com.example.Refactoring.model.Patient;
import com.example.Refactoring.repository.PatientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;



/*
 @author K1ngst0n
 @project Refactoring
 @class PatientServiceTest
 @version 1.0.0
 @since 4/30/2025 - 9:38 PM
*/

@SpringBootTest
class PatientServiceTest {

    @Autowired
    private PatientRepository repository;

    @Autowired
    private PatientService underTest;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }


    // Тест перевіряє, що сервіс коректно створює пацієнта і повертає не-null об'єкт
    @Test
    void createPatient_ShouldReturnNonNullPatient() {
        Patient patient = new Patient("John", "Doe", "1990-01-01", "Male", "john.doe@example.com");
        Patient createdPatient = underTest.createPatient(patient);
        assertNotNull(createdPatient);
    }

    // Тест перевіряє, що при створенні пацієнта генерується ID
    @Test
    void createPatient_ShouldGenerateId() {
        Patient patient = new Patient("John", "Doe", "1990-01-01", "Male", "john.doe@example.com");
        Patient createdPatient = underTest.createPatient(patient);
        assertNotNull(createdPatient.getId());
    }

    // Тест перевіряє коректність збереження імені пацієнта
    @Test
    void createPatient_ShouldSetCorrectFirstName() {
        Patient patient = new Patient("John", "Doe", "1990-01-01", "Male", "john.doe@example.com");
        Patient createdPatient = underTest.createPatient(patient);
        assertEquals("John", createdPatient.getFirstName());
    }

    // Тест перевіряє коректність збереження прізвища пацієнта
    @Test
    void createPatient_ShouldSetCorrectLastName() {
        Patient patient = new Patient("John", "Doe", "1990-01-01", "Male", "john.doe@example.com");
        Patient createdPatient = underTest.createPatient(patient);
        assertEquals("Doe", createdPatient.getLastName());
    }

    // Тест перевіряє коректність збереження дати народження
    @Test
    void createPatient_ShouldSetCorrectDateOfBirth() {
        Patient patient = new Patient("John", "Doe", "1990-01-01", "Male", "john.doe@example.com");
        Patient createdPatient = underTest.createPatient(patient);
        assertEquals("1990-01-01", createdPatient.getDateOfBirth());
    }

    // Тест перевіряє коректність збереження статі пацієнта
    @Test
    void createPatient_ShouldSetCorrectGender() {
        Patient patient = new Patient("John", "Doe", "1990-01-01", "Male", "john.doe@example.com");
        Patient createdPatient = underTest.createPatient(patient);
        assertEquals("Male", createdPatient.getGender());
    }

    // Тест перевіряє коректність збереження email пацієнта
    @Test
    void createPatient_ShouldSetCorrectEmail() {
        Patient patient = new Patient("John", "Doe", "1990-01-01", "Male", "john.doe@example.com");
        Patient createdPatient = underTest.createPatient(patient);
        assertEquals("john.doe@example.com", createdPatient.getEmail());
    }

    // Тест перевіряє, що оновлення пацієнта повертає не-null об'єкт
    @Test
    void updatePatient_ShouldReturnNonNullPatient() {
        Patient createdPatient = createTestPatient();
        Patient updateData = new Patient("Updated", "Patient", "1985-05-15", "Female", "updated@example.com");
        Patient updatedPatient = underTest.updatePatient(createdPatient.getId(), updateData);
        assertNotNull(updatedPatient);
    }

    // Тест перевіряє оновлення імені пацієнта
    @Test
    void updatePatient_ShouldUpdateFirstName() {
        Patient createdPatient = createTestPatient();
        Patient updateData = new Patient("Updated", "Patient", "1985-05-15", "Female", "updated@example.com");
        Patient updatedPatient = underTest.updatePatient(createdPatient.getId(), updateData);
        assertEquals("Updated", updatedPatient.getFirstName());
    }

    // Тест перевіряє оновлення прізвища пацієнта
    @Test
    void updatePatient_ShouldUpdateLastName() {
        Patient createdPatient = createTestPatient();
        Patient updateData = new Patient("Updated", "Patient", "1985-05-15", "Female", "updated@example.com");
        Patient updatedPatient = underTest.updatePatient(createdPatient.getId(), updateData);
        assertEquals("Patient", updatedPatient.getLastName());
    }

    // Тест перевіряє оновлення дати народження пацієнта
    @Test
    void updatePatient_ShouldUpdateDateOfBirth() {
        Patient createdPatient = createTestPatient();
        Patient updateData = new Patient("Updated", "Patient", "1985-05-15", "Female", "updated@example.com");
        Patient updatedPatient = underTest.updatePatient(createdPatient.getId(), updateData);
        assertEquals("1985-05-15", updatedPatient.getDateOfBirth());
    }

    // Тест перевіряє оновлення статі пацієнта
    @Test
    void updatePatient_ShouldUpdateGender() {
        Patient createdPatient = createTestPatient();
        Patient updateData = new Patient("Updated", "Patient", "1985-05-15", "Female", "updated@example.com");
        Patient updatedPatient = underTest.updatePatient(createdPatient.getId(), updateData);
        assertEquals("Female", updatedPatient.getGender());
    }

    // Тест перевіряє оновлення email пацієнта
    @Test
    void updatePatient_ShouldUpdateEmail() {
        Patient createdPatient = createTestPatient();
        Patient updateData = new Patient("Updated", "Patient", "1985-05-15", "Female", "updated@example.com");
        Patient updatedPatient = underTest.updatePatient(createdPatient.getId(), updateData);
        assertEquals("updated@example.com", updatedPatient.getEmail());
    }

    // Тест перевіряє, що ID пацієнта залишається незмінним після оновлення
    @Test
    void updatePatient_ShouldPreserveId() {
        Patient createdPatient = createTestPatient();
        Patient updateData = new Patient("Updated", "Patient", "1985-05-15", "Female", "updated@example.com");
        Patient updatedPatient = underTest.updatePatient(createdPatient.getId(), updateData);
        assertEquals(createdPatient.getId(), updatedPatient.getId());
    }

    // Тест перевіряє, що спроба оновити неіснуючого пацієнта повертає null
    @Test
    void updateNonExistingPatient_ShouldCreateNewPatient() {
        Patient updateData = new Patient("Non", "Existing", "2000-01-01", "Other", "none@example.com");
        Patient result = underTest.updatePatient("non-existing-id", updateData);
        assertNotNull(result);
        assertEquals("non-existing-id", result.getId());
    }

    // Тест перевіряє, що для пустої бази даних повертається пустий список
    @Test
    void getAllPatients_ShouldReturnEmptyListForEmptyRepository() {
        List<Patient> patients = underTest.getAllPatients();
        assertTrue(patients.isEmpty());
    }

    // Тест перевіряє, що повертаються всі створені пацієнти
    @Test
    void getAllPatients_ShouldReturnAllCreatedPatients() {
        Patient patient1 = createTestPatient();
        Patient patient2 = new Patient("Jane", "Smith", "1988-07-20", "Female", "jane.smith@example.com");
        underTest.createPatient(patient2);

        List<Patient> patients = underTest.getAllPatients();
        assertEquals(2, patients.size());
    }

    // Тест перевіряє, що повертаються всі створені пацієнти
    @Test
    void getPatientById_ShouldReturnCorrectPatient() {
        Patient createdPatient = createTestPatient();
        Optional<Patient> foundPatient = underTest.getPatientById(createdPatient.getId());
        assertTrue(foundPatient.isPresent());
        assertEquals(createdPatient.getId(), foundPatient.get().getId());
    }

    // Тест перевіряє пошук пацієнта за ID
    @Test
    void getPatientById_ShouldReturnEmptyForNonExistingId() {
        Optional<Patient> foundPatient = underTest.getPatientById("non-existing-id");
        assertTrue(foundPatient.isEmpty());
    }

    //Тест перевіряє коректність роботи методу видалення пацієнта
    @Test
    void deletePatient_ShouldRemovePatientFromRepository() {
        Patient createdPatient = createTestPatient();
        underTest.deletePatient(createdPatient.getId());
        Optional<Patient> foundPatient = underTest.getPatientById(createdPatient.getId());
        assertTrue(foundPatient.isEmpty());
    }

    private Patient createTestPatient() {
        Patient patient = new Patient("Test", "Patient", "2000-01-01", "Other", "test@example.com");
        return underTest.createPatient(patient);
    }


    //Тест перевіряє, що спроба видалити неіснуючого пацієнта не призводить до виключень і нічого не змінює в базі
    @Test
    void deleteNonExistingPatient_ShouldNotFail() {
        String nonExistingId = "non-existing-id";
        long initialCount = repository.count();

        underTest.deletePatient(nonExistingId);

        assertEquals(initialCount, repository.count(),
                "Кількість пацієнтів не повинна змінитися при видаленні неіснуючого ID");
    }


    //Тест перевіряє створення пацієнта з мінімально допустимими даними
    @Test
    void createPatient_WithMinimumRequiredFields_ShouldSucceed() {
        //
        Patient minimalPatient = new Patient();
        minimalPatient.setFirstName("Minimal");
        minimalPatient.setLastName("Patient");
        minimalPatient.setEmail("min@test.com");


        Patient result = underTest.createPatient(minimalPatient);


        assertNotNull(result.getId(), "Пацієнт з мінімальними даними має отримати ID");
        assertEquals("Minimal", result.getFirstName());
        assertNull(result.getDateOfBirth(), "Дата народження має бути null, якщо не вказана");
    }
    //Тест перевіряє коректність роботи при великій кількості пацієнтів
    @Test
    void getAllPatients_WithLargeDataset_ShouldReturnAll() {
        // Given
        int count = 100;
        for (int i = 0; i < count; i++) {
            Patient p = new Patient("Patient" + i, "Last" + i, null, null, "p" + i + "@test.com");
            underTest.createPatient(p);
        }
        List<Patient> patients = underTest.getAllPatients();
        assertEquals(count, patients.size(),
                "Повинні повертатися всі створені пацієнти");
    }
}