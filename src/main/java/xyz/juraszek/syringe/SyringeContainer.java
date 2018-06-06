package xyz.juraszek.syringe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import xyz.juraszek.syringe.exceptions.TypeNotRegisteredException;

public class SyringeContainer {
  private Map<Class, Class> typeMapping;
  private Map<Class, Object> instanceMapping;
  private Map<Class, ResolutionApproach> resolutionApproaches;
  private Set<Class> typesWhichAreRequiredToBeSingletons;
  private Set<Class> alreadySpawnedSingletons;
  private Map<Class, Object> spawnedSingletonInstances;

  public SyringeContainer() {
    this.typeMapping = new HashMap<>();
    this.instanceMapping = new HashMap<>();
    this.resolutionApproaches = new HashMap<>();
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
    this.resolutionApproaches.put(abstractionType, ResolutionApproach.BIND_CLASS);
    if (requiredToBeSingleton) {
      this.typesWhichAreRequiredToBeSingletons.add(implementationType);
    }
  }

  public void registerInstance(Class abstractionType,
                               Object concreteInstance) {
    this.resolutionApproaches.put(abstractionType, ResolutionApproach.BIND_INSTANCE);
    this.instanceMapping.put(abstractionType, concreteInstance);
  }

  public <T> T resolve(Class abstractionType) throws TypeNotRegisteredException,
                                                     InstantiationException,
                                                     IllegalAccessException {
    if (! isAbstractionTypeEligibleToBeResolved(abstractionType))
      throw new TypeNotRegisteredException();
    if (this.resolutionApproaches.get(abstractionType).equals(ResolutionApproach.BIND_CLASS))
      return (T) resolveToClassInstance(abstractionType);
    else if (this.resolutionApproaches.get(abstractionType).equals(ResolutionApproach.BIND_INSTANCE))
      return (T) resolveToConcreteInstance(abstractionType);
    else
      throw new Error("Podobno niemożliwe!");
  }

  private <T> T resolveToClassInstance(Class abstractionType)
      throws IllegalAccessException, InstantiationException {
    Class implementationType = this.typeMapping.get(abstractionType);
    if (this.typesWhichAreRequiredToBeSingletons.contains(implementationType))
      return (T) resolveToSingletonInstance(implementationType);
    else
      return (T) this.typeMapping.get(abstractionType).newInstance();
  }

  private <T> T resolveToSingletonInstance(Class singletonImplementationType)
      throws InstantiationException, IllegalAccessException {
    if (this.alreadySpawnedSingletons.contains(singletonImplementationType)) {
      return (T) this.spawnedSingletonInstances.get(singletonImplementationType);
    }
    Object newInstance = singletonImplementationType.newInstance();
    this.alreadySpawnedSingletons.add(singletonImplementationType);
    this.spawnedSingletonInstances.put(singletonImplementationType, newInstance);
    return (T) newInstance;
  }

  private<T> T resolveToConcreteInstance(Class abstractionType) {
    return (T) this.instanceMapping.get(abstractionType);
  }

  private boolean isAbstractionTypeEligibleToBeResolved(Class abstractionType) {
    return this.typeMapping.containsKey(abstractionType) ||
           this.instanceMapping.containsKey(abstractionType);
  }
}
