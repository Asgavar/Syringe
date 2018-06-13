package xyz.juraszek.syringe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.juraszek.syringe.examples.AnExampleClass;
import xyz.juraszek.syringe.examples.AnExampleInterface;
import xyz.juraszek.syringe.exceptions.TypeNotRegisteredException;

class TestServiceLocator {

  @Test
  void simpleRegistrationWithOneContainer() throws TypeNotRegisteredException {
    ServiceLocator serviceLocator = new ServiceLocator();
    SyringeContainer container = new SyringeContainer();

    container.registerType(AnExampleInterface.class, AnExampleClass.class, false);
    serviceLocator.attachContainer(container);
    Object resolvedObject = serviceLocator.getInstance(AnExampleInterface.class);

    assertEquals(AnExampleClass.class, resolvedObject.getClass());
  }

  @Test
  void simpleRegistrationWhenOnlySecondContainerKnowsTheImplementation()
      throws TypeNotRegisteredException {
    ServiceLocator serviceLocator = new ServiceLocator();
    SyringeContainer container1 = new SyringeContainer();
    SyringeContainer container2 = new SyringeContainer();

    container2.registerType(AnExampleInterface.class, AnExampleClass.class, false);
    serviceLocator.attachContainer(container1);
    serviceLocator.attachContainer(container2);
    Object resolvedObject = serviceLocator.getInstance(AnExampleInterface.class);

    assertEquals(AnExampleClass.class, resolvedObject.getClass());
  }
}