package xyz.juraszek.syringe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.juraszek.syringe.examples.AnExampleClass;
import xyz.juraszek.syringe.examples.AnExampleInterface;
import xyz.juraszek.syringe.exceptions.TypeNotRegisteredException;

class TestSyringeContainer {

  @Test
  void simpleRegisterAndResolve()
      throws IllegalAccessException, TypeNotRegisteredException, InstantiationException {
    SyringeContainer container = new SyringeContainer();

    container.registerType(AnExampleInterface.class, AnExampleClass.class, false);
    Object hopefullyAnExampleClassInstance = container.resolve(AnExampleInterface.class);

    assertEquals(AnExampleClass.class, hopefullyAnExampleClassInstance.getClass());
  }

  @Test
  void resolvingUnregisteredTypeThrows() {
    SyringeContainer container = new SyringeContainer();

    assertThrows(
        TypeNotRegisteredException.class,
        () -> container.resolve(AnExampleInterface.class)
    );
  }

  @Test
  void instantiatingSingletonTwiceResultsInTheSameObject()
      throws IllegalAccessException, TypeNotRegisteredException, InstantiationException {
    SyringeContainer container = new SyringeContainer();

    container.registerType(
        AnExampleInterface.class,
        AnExampleClass.class,
        true
    );
    Object firstObject = container.resolve(AnExampleInterface.class);
    Object secondObject = container.resolve(AnExampleInterface.class);

    assertSame(secondObject, firstObject);
  }

  @Test
  void markingTypeAsSingletonAfterItsRegistrationWorks()
      throws IllegalAccessException, TypeNotRegisteredException, InstantiationException {
    SyringeContainer container = new SyringeContainer();

    container.registerType(
        AnExampleInterface.class,
        AnExampleClass.class,
        false
    );
    container.registerType(AnExampleClass.class, true);
    Object firstObject = container.resolve(AnExampleInterface.class);
    Object secondObject = container.resolve(AnExampleInterface.class);

    assertSame(secondObject, firstObject);
  }

  @Test
  void whenTypeIsNotSingletonEachResolveShouldReturnADifferentObject()
      throws IllegalAccessException, TypeNotRegisteredException, InstantiationException {
    SyringeContainer container = new SyringeContainer();

    container.registerType(
        AnExampleInterface.class,
        AnExampleClass.class,
        false
    );
    Object firstObject = container.resolve(AnExampleInterface.class);
    Object secondObject = container.resolve(AnExampleInterface.class);

    assertNotSame(secondObject, firstObject);
  }

  @Test
  void concreteInstanceRegistration()
      throws IllegalAccessException, TypeNotRegisteredException, InstantiationException {
    SyringeContainer container = new SyringeContainer();
    AnExampleClass concreteInstance = new AnExampleClass();

    container.registerInstance(
        AnExampleInterface.class,
        concreteInstance
    );
    Object resolvedInstance = container.resolve(AnExampleInterface.class);

    assertSame(concreteInstance, resolvedInstance);
  }
}
