package xyz.juraszek.syringe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import xyz.juraszek.syringe.exceptions.TypeNotRegisteredException;

public class SyringeContainer {
  private Map<Class, Class> typeMapping;
  private Set<Class> typesWhichAreRequiredToBeSingletons;
  private Set<Class> alreadySpawnedSingletons;
  private Map<Class, Object> spawnedSingletonInstances;

  public SyringeContainer() {
    this.typeMapping = new HashMap<>();
    this.typesWhichAreRequiredToBeSingletons = new HashSet<>();
    this.alreadySpawnedSingletons = new HashSet<>();
    this.spawnedSingletonInstances = new HashMap<>();
  }

  public void registerType(Class implementationType,
                           boolean requiredToBeSingleton) {
    if (requiredToBeSingleton) {
      this.typesWhichAreRequiredToBeSingletons.add(implementationType);
    } else {
      this.typesWhichAreRequiredToBeSingletons.remove(implementationType);
    }
  }

  public void registerType(Class abstractionType,
                           Class implementationType,
                           boolean requiredToBeSingleton) {
    this.typeMapping.put(abstractionType, implementationType);
    if (requiredToBeSingleton) {
      this.typesWhichAreRequiredToBeSingletons.add(implementationType);
    }
  }

  public <T> T resolve(Class abstractionType) throws TypeNotRegisteredException,
                                                     InstantiationException,
                                                     IllegalAccessException {
    if (! this.typeMapping.containsKey(abstractionType)) {
      throw new TypeNotRegisteredException();
    }
    Class implementationType = this.typeMapping.get(abstractionType);
    if (this.typesWhichAreRequiredToBeSingletons.contains(implementationType)) {
      return (T) resolveSingleton(implementationType);
    }
    return (T) this.typeMapping.get(abstractionType).newInstance();
  }

  private <T> T resolveSingleton(Class singletonImplementationType) throws InstantiationException,
                                                                           IllegalAccessException {
    if (this.alreadySpawnedSingletons.contains(singletonImplementationType)) {
      return (T) this.spawnedSingletonInstances.get(singletonImplementationType);
    }
    Object newInstance = singletonImplementationType.newInstance();
    this.alreadySpawnedSingletons.add(singletonImplementationType);
    this.spawnedSingletonInstances.put(singletonImplementationType, newInstance);
    return (T) newInstance;
  }
}
