package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    private static final int TIMEOUT_SECONDS = 5;

    @BeforeAll
    public static void setUpJavaFxRuntime() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        if (!latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            fail("JavaFX runtime did not start within " + TIMEOUT_SECONDS + " seconds.");
        }
    }

    @Test
    public void constructor_validPerson_displaysPersonDetails() throws Throwable {
        Person person = new PersonBuilder()
                .withName("Bobby Bee")
                .withPatientNo("91234567")
                .withFamilyNo("87654321")
                .withPatientEmail("patient@example.com")
                .withFamilyEmail("family@example.com")
                .withAddress("123 Serangoon Road")
                .withTags("urgent", "family")
                .build();

        runOnJavaFxThread(() -> {
            PersonCard personCard = new PersonCard(person, 3);

            assertSame(person, personCard.person);
            assertEquals("3. ", getLabelText(personCard, "id"));
            assertEquals("Bobby Bee", getLabelText(personCard, "name"));
            assertEquals("Patient HP: 91234567", getLabelText(personCard, "patientNo"));
            assertEquals("Family HP: 87654321", getLabelText(personCard, "familyNo"));
            assertEquals("Patient Email: patient@example.com | Family Email: family@example.com",
                    getLabelText(personCard, "email"));
            assertEquals("123 Serangoon Road", getLabelText(personCard, "address"));
            assertEquals(List.of("family", "urgent"), getTagTexts(personCard));
        });
    }

    @Test
    public void constructor_personWithoutEmail_displaysEmptyEmailText() throws Throwable {
        Person person = new PersonBuilder().withoutEmail().build();

        runOnJavaFxThread(() -> {
            PersonCard personCard = new PersonCard(person, 1);

            assertEquals("", getLabelText(personCard, "email"));
        });
    }

    @Test
    public void constructor_personWithOnlyFamilyEmail_displaysFamilyEmailText() throws Throwable {
        Person person = new PersonBuilder()
                .withoutEmail()
                .withFamilyEmail("caregiver@example.com")
                .build();

        runOnJavaFxThread(() -> {
            PersonCard personCard = new PersonCard(person, 1);

            assertEquals("Family Email: caregiver@example.com", getLabelText(personCard, "email"));
        });
    }

    private static void runOnJavaFxThread(Executable executable) throws Throwable {
        AtomicReference<Throwable> thrown = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                executable.execute();
            } catch (Throwable throwable) {
                thrown.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            fail("JavaFX task did not finish within " + TIMEOUT_SECONDS + " seconds.");
        }
        if (thrown.get() != null) {
            throw thrown.get();
        }
    }

    private static String getLabelText(PersonCard personCard, String fieldName) throws ReflectiveOperationException {
        return getField(personCard, fieldName, Label.class).getText();
    }

    private static List<String> getTagTexts(PersonCard personCard) throws ReflectiveOperationException {
        return getField(personCard, "tags", FlowPane.class).getChildren().stream()
                .map(node -> ((Label) node).getText())
                .toList();
    }

    private static <T> T getField(PersonCard personCard, String fieldName, Class<T> fieldType)
            throws ReflectiveOperationException {
        Field field = PersonCard.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return fieldType.cast(field.get(personCard));
    }
}
