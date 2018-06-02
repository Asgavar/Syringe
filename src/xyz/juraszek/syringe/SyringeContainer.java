package xyz.juraszek.syringe;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import xyz.juraszek.syringe.exceptions.TypeNotRegisteredException;

public class SyringeContainer {
  private Map<Type, Type> typeMapping;
  private Set<Type> typesWhichAreRequiredToBeSingletons;
  private Set<Object> alreadySpawnedSingletons;

  public SyringeContainer() {
    this.typeMapping = new HashMap<>();
    this.typesWhichAreRequiredToBeSingletons = new HashSet<>();
    this.alreadySpawnedSingletons = new HashSet<>();
  }

  public void registerType(Type implementationType,
                           boolean requiredToBeSingleton) {
    if (requiredToBeSingleton) {
      this.typesWhichAreRequiredToBeSingletons.add(implementationType);
    } else {
      this.typesWhichAreRequiredToBeSingletons.remove(implementationType);
    }
  }

  public void registerType(Type abstractionType,
                           Type implementationType,
                           boolean requiredToBeSingleton) {
    this.typeMapping.put(abstractionType, implementationType);
    if (requiredToBeSingleton) {
      this.typesWhichAreRequiredToBeSingletons.add(implementationType);
    }
  }

  public <T> T resolve(Type abstractionType) {
    if (! this.typeMapping.containsKey(abstractionType)) {
      throw new TypeNotRegisteredException();
    }
    Type implementationType = this.typeMapping.get(abstractionType);
  }
}
