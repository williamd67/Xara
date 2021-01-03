package nl.marayla.Xara.test;

import java.util.Arrays;
import java.util.Random;

import nl.marayla.Xara.ElementCollisions.Bounce;
import nl.marayla.Xara.ElementCollisions.Eat;
import nl.marayla.Xara.ElementCollisions.Eaten;
import nl.marayla.Xara.ElementCollisions.ElementCollision;
import nl.marayla.Xara.ElementCollisions.ElementCollisionResolver;
import nl.marayla.Xara.ElementCollisions.Push;
import nl.marayla.Xara.ElementCollisions.Neutral;
import nl.marayla.Xara.GameElements.GameElement;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class ElementCollisionResolverTest {
    @Before
    public final void setUp() {
        int nrOfElements = LevelElements.values().length;
        collisionResolver = createCollisionResolver(nrOfElements);

        expectedCollisions = new ElementCollision[nrOfElements * nrOfElements];
        Arrays.fill(expectedCollisions, ElementCollisionResolver.DEFAULT);
    }

    public final void addDefaultCollision(final ElementCollision elementCollision) {
        collisionResolver.addDefaultCollision(elementCollision);
        Arrays.fill(expectedCollisions, elementCollision);
    }

    public final void addElementCollision(
        final ElementCollision elementCollision,
        final GameElement element
    ) {
        collisionResolver.addElementCollision(elementCollision, element);
        int length = LevelElements.values().length;
        int start = (element.ordinal() * length);
        int end = start + length;
        for (int i = start; i < end; i++) {
            expectedCollisions[i] = elementCollision;
        }
    }

    public final void addElementElementCollision(
        final ElementCollision elementCollision,
        final GameElement element1,
        final GameElement element2
    ) {
        collisionResolver.addElementElementCollision(elementCollision, element1, element2);
        expectedCollisions[
            (element1.ordinal() * LevelElements.values().length) + element2.ordinal()
        ] = elementCollision;
    }

    @Test
    public final void testNoRegistration() {
        verify();
    }

    @Test
    public final void testDefaultRegistration() {
        addDefaultCollision(determineCollision());
        verify();
    }

    @Test
    public final void testMultipleDefaultRegistrations() {
        ElementCollision collision = determineCollision();
        for (int i = 0; i < REPEATS; i++) {
            addDefaultCollision(collision);
            collision = determineDifferentCollision(collision);
        }
        verify();
    }

    @Test
    public final void testSingleElementRegistrationNoDefault() {
        addElementCollision(determineCollision(), determineElement());
        verify();
    }

    @Test
    public final void testSingleElementRegistrationWithDefault() {
        ElementCollision collision = determineCollision();
        addDefaultCollision(collision);

        addElementCollision(determineDifferentCollision(collision), determineElement());
        verify();
    }

    @Test
    public final void testSingleElementRegistrationWithDefaultWithReset() {
        ElementCollision collision1 = determineCollision();
        addDefaultCollision(collision1);

        LevelElements element = determineElement();
        addElementCollision(determineDifferentCollision(collision1), element);
        addElementCollision(collision1, element);
        verify();
    }

    @Test
    public final void testMultipleEqualElementRegistrations() {
        ElementCollision collision = determineCollision();
        LevelElements element = determineElement();
        for (int i = 0; i < REPEATS; i++) {
            addElementCollision(collision, element);
            collision = determineDifferentCollision(collision);
        }
        verify();
    }

    @Test
    public final void testMultipleElementRegistrations() {
        ElementCollision collision = determineCollision();
        LevelElements element = determineElement();
        for (int i = 0; i < REPEATS; i++) {
            addElementCollision(collision, element);
            collision = determineDifferentCollision(collision);
            element = determineDifferentElement(element);
        }
        verify();
    }

    @Test
    public final void testSingleElementElementRegistrationNoDefault() {
        addElementElementCollision(determineCollision(), determineElement(), determineElement());
        verify();
    }

    @Test
    public final void testSingleElementElementRegistrationWithDefault() {
        ElementCollision collision = determineCollision();
        addDefaultCollision(collision);

        addElementElementCollision(
            determineDifferentCollision(collision),
            determineElement(),
            determineElement()
        );
        verify();
    }

    @Test
    public final void testSingleElementElementRegistrationWithDefaultWithReset() {
        ElementCollision collision1 = determineCollision();
        addDefaultCollision(collision1);

        LevelElements element1 = determineElement();
        LevelElements element2 = determineElement();
        addElementElementCollision(determineDifferentCollision(collision1), element1, element2);
        addElementElementCollision(collision1, element1, element2);
        verify();
    }

    @Test
    public final void testSingleElementElementRegistrationWithElementNoDefault() {
        ElementCollision collision = determineCollision();
        LevelElements element1 = determineElement();
        addElementCollision(collision, element1);

        addElementElementCollision(
            determineDifferentCollision(collision),
            element1,
            determineElement()
        );
        verify();
    }

    @Test
    public final void testSingleElementElementRegistrationWithElementNoDefaultWithReset() {
        ElementCollision collision1 = determineCollision();
        LevelElements element1 = determineElement();
        addElementCollision(collision1, element1);

        LevelElements element2 = determineElement();
        addElementElementCollision(
            determineDifferentCollision(collision1),
            element1,
            element2
        );
        addElementElementCollision(
            collision1,
            element1,
            element2
        );
        verify();
    }

    @Test
    public final void testSingleElementElementRegistrationWithElementWithDefault() {
        ElementCollision collision = determineCollision();
        addDefaultCollision(collision);

        collision = determineDifferentCollision(collision);
        LevelElements element1 = determineElement();
        addElementCollision(collision, element1);

        addElementElementCollision(
            determineDifferentCollision(collision),
            element1,
            determineElement()
        );
        verify();
    }

    @Test
    public final void testSingleElementElementRegistrationWithElementWithDefaultWithResetDefault() {
        ElementCollision collision1 = determineCollision();
        addDefaultCollision(collision1);

        ElementCollision collision = determineDifferentCollision(collision1);
        LevelElements element1 = determineElement();
        addElementCollision(collision, element1);

        LevelElements element2 = determineElement();
        addElementElementCollision(determineDifferentCollision(collision1), element1, element2);
        addElementElementCollision(collision1, element1, element2);
        verify();
    }

    @Test
    public final void testSingleElementElementRegistrationWithElementWithDefaultWithResetElement() {
        ElementCollision collision = determineCollision();
        addDefaultCollision(collision);

        ElementCollision collision1 = determineDifferentCollision(collision);
        LevelElements element1 = determineElement();
        addElementCollision(collision1, element1);

        LevelElements element2 = determineElement();
        addElementElementCollision(determineDifferentCollision(collision1), element1, element2);
        addElementElementCollision(collision1, element1, element2);
        verify();
    }

    @Test
    public final void testMultipleEqualElementElementRegistrations() {
        ElementCollision collision = determineCollision();
        LevelElements element1 = determineElement();
        LevelElements element2 = determineElement();
        for (int i = 0; i < REPEATS; i++) {
            addElementElementCollision(collision, element1, element2);
            collision = determineDifferentCollision(collision);
        }
        verify();
    }

    @Test
    public final void testMultipleEqualElementElementRegistrationsWithElement() {
        ElementCollision collision = determineCollision();
        LevelElements element1 = determineElement();
        addElementCollision(collision, element1);

        LevelElements element2 = determineElement();
        for (int i = 0; i < REPEATS; i++) {
            collision = determineDifferentCollision(collision);
            addElementElementCollision(collision, element1, element2);
        }
        verify();
    }

    @Test
    public final void testMultipleElementElementRegistrations() {
        ElementCollision collision = determineCollision();
        LevelElements element1 = determineElement();
        LevelElements element2 = determineElement();
        for (int i = 0; i < REPEATS; i++) {
            addElementElementCollision(collision, element1, element2);
            collision = determineDifferentCollision(collision);
            element1 = determineDifferentElement(element1);
            element2 = determineDifferentElement(element2);
        }
        verify();
    }

    @Test
    public final void testMultipleElementElementRegistrationsWithElement() {
        ElementCollision collision = determineCollision();
        LevelElements element1 = determineElement();
        addElementCollision(collision, element1);

        collision = determineDifferentCollision(collision);
        element1 = determineDifferentElement(element1);
        LevelElements element2 = determineElement();
        for (int i = 0; i < REPEATS; i++) {
            addElementElementCollision(collision, element1, element2);
            collision = determineDifferentCollision(collision);
            element1 = determineDifferentElement(element1);
            element2 = determineDifferentElement(element2);
        }
        verify();
    }

    @Test
    public final void testMultipleElementElementRegistrationsWithMultipleElements() {
        ElementCollision collision = determineCollision();
        addDefaultCollision(collision);

        LevelElements element1 = determineElement();
        collision = determineDifferentCollision(collision);
        for (int i = 0; i < REPEATS; i++) {
            addElementCollision(collision, element1);
            collision = determineDifferentCollision(collision);
            element1 = determineDifferentElement(element1);
        }

        LevelElements element2 = determineElement();
        for (int i = 0; i < REPEATS; i++) {
            addElementElementCollision(collision, element1, element2);
            collision = determineDifferentCollision(collision);
            element1 = determineDifferentElement(element1);
            element2 = determineDifferentElement(element2);
        }
        verify();
    }

    @Test
    public final void testDefaultAfterElementException() {
        try {
            collisionResolver.addElementCollision(determineCollision(), determineElement());
            collisionResolver.addDefaultCollision(determineCollision());
            // This should never be reached as an exception should have been thrown
            fail();
        }
        catch (ElementCollisionResolver.InvalidOrderException e) {
            // Expected exception ignore
            assertTrue(true); // to suppress warning that catch should have at least one statement
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public final void testDefaultAfterElementElementException() {
        try {
            collisionResolver.addElementElementCollision(
                determineCollision(),
                determineElement(),
                determineElement()
            );
            collisionResolver.addDefaultCollision(determineCollision());
            // This should never be reached as an exception should have been thrown
            fail();
        }
        catch (ElementCollisionResolver.InvalidOrderException e) {
            // Expected exception ignore
            assertTrue(true); // to suppress warning that catch should have at least one statement
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public final void testElementAfterElementElementException() {
        try {
            collisionResolver.addElementElementCollision(
                determineCollision(),
                determineElement(),
                determineElement()
            );
            collisionResolver.addElementCollision(determineCollision(), determineElement());
            // This should never be reached as an exception should have been thrown
            fail();
        }
        catch (ElementCollisionResolver.InvalidOrderException e) {
            // Expected exception ignore
            assertTrue(true); // to suppress warning that catch should have at least one statement
        }
        catch (Exception e) {
            fail();
        }
    }

    protected abstract ElementCollisionResolver createCollisionResolver(int nrOfElements);
    private ElementCollision determineCollision() {
        ElementCollision[] availableCollisions = {
            Bounce.REVERSE,
            Eat.INSTANCE,
            Eaten.INSTANCE,
            Push.INSTANCE,
            Neutral.INSTANCE
        };
        int length = availableCollisions.length;
        ElementCollision collision = availableCollisions[random.nextInt(length)];
        while (collision == ElementCollisionResolver.DEFAULT) {
            collision = availableCollisions[random.nextInt(length)];
        }
        return collision;
    }

    private ElementCollision determineDifferentCollision(final ElementCollision current) {
        ElementCollision result = determineCollision();
        while (result == current) {
            result = determineCollision();
        }
        return result;
    }

    private LevelElements determineElement() {
        LevelElements[] elements = LevelElements.values();
        return elements[random.nextInt(elements.length)];
    }

    private LevelElements determineDifferentElement(final LevelElements current) {
        LevelElements result = determineElement();
        while (result == current) {
            result = determineElement();
        }
        return result;
    }

    private void verify() {
        for (LevelElements element1 : LevelElements.values()) {
            for (LevelElements element2 : LevelElements.values()) {
                assertEquals(
                    expectedCollisions[
                        (element1.ordinal() * LevelElements.values().length) + element2.ordinal()
                    ],
                    collisionResolver.getElementElementCollision(element1, element2)
                );
            }
        }
    }

    private enum LevelElements implements GameElement {
        ELEMENT1, ELEMENT2, ELEMENT3, ELEMENT4, ELEMENT5
    }

    private ElementCollisionResolver collisionResolver;
    private ElementCollision[] expectedCollisions;
    private final Random random = new Random(45);
    private static final int REPEATS = 7;
}