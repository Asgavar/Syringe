package xyz.juraszek.syringe;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;
import xyz.juraszek.syringe.annotations.DependencyMethod;
import xyz.juraszek.syringe.examples.AnExampleClass;
import xyz.juraszek.syringe.examples.AnExampleInterface;
import xyz.juraszek.syringe.examples.CircularlyDependentOne;
import xyz.juraszek.syringe.examples.CircularlyDependentTwo;
import xyz.juraszek.syringe.examples.DependencyMethodExample;
import xyz.juraszek.syringe.exceptions.CircularDependenciesError;
import xyz.juraszek.syringe.exceptions.TypeNotRegisteredException;

class TestSyringeContainer {

  @Test
  void simpleRegisterAndResolve()
      throws IllegalAccessException, TypeNotRegisteredException, InstantiationException,
             InvocationTargetException {
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
      throws IllegalAccessException, TypeNotRegisteredException, InstantiationException, InvocationTargetException {
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
      throws IllegalAccessException, TypeNotRegisteredException, InstantiationException, InvocationTargetException {
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
      throws IllegalAccessException, TypeNotRegisteredException, InstantiationException,
             InvocationTargetException {
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
      throws IllegalAccessException, TypeNotRegisteredException, InstantiationException,
             InvocationTargetException {
    SyringeContainer container = new SyringeContainer();
    AnExampleClass concreteInstance = new AnExampleClass();

    container.registerInstance(
        AnExampleInterface.class,
        concreteInstance
    );
    Object resolvedInstance = container.resolve(AnExampleInterface.class);

    assertSame(concreteInstance, resolvedInstance);
  }

  @Test
  void circularDependencyThrows() {
    SyringeContainer container = new SyringeContainer();

    container.registerType(CircularlyDependentOne.class, CircularlyDependentOne.class, false);
    container.registerType(CircularlyDependentTwo.class, CircularlyDependentTwo.class, false);

    assertThrows(
        CircularDependenciesError.class,
        () -> container.resolve(CircularlyDependentOne.class)
    );
  }

  @Test
  void injectingIntoMethod()
      throws InvocationTargetException, InstantiationException, IllegalAccessException,
             TypeNotRegisteredException {
    SyringeContainer container = new SyringeContainer();

    container.registerType(AnExampleInterface.class, AnExampleClass.class, false);
    container.registerType(DependencyMethodExample.class, DependencyMethodExample.class, false);
    DependencyMethodExample instantiatedObject =
        (DependencyMethodExample) container.resolve(DependencyMethodExample.class);

    assert(instantiatedObject.toBeInjected != null);
  }
}
