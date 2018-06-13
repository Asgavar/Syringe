package xyz.juraszek.syringe;

import java.util.ArrayList;
import xyz.juraszek.syringe.exceptions.TypeNotRegisteredException;

public class ServiceLocator {
  private ArrayList<SyringeContainer> containers = new ArrayList<>();

  public void attachContainer(SyringeContainer container) {
    this.containers.add(container);
  }

  public Object getInstance(Class abstractionType) throws TypeNotRegisteredException {
    Object resolvedInstance = null;

    for (SyringeContainer container : this.containers) {
      try {
        resolvedInstance = container.resolve(abstractionType);
      } catch (Exception e) {

      }
    }

    if (resolvedInstance == null)
      throw new TypeNotRegisteredException();

    return resolvedInstance;
  }
}
