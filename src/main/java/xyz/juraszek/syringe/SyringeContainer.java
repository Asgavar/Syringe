package xyz.juraszek.syringe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
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

  public Object resolve(Class abstractionType) throws TypeNotRegisteredException,
      InstantiationException, IllegalAccessException, InvocationTargetException {
    if (! isAbstractionTypeEligibleToBeResolved(abstractionType))
      throw new TypeNotRegisteredException();
    if (this.resolutionApproaches.get(abstractionType).equals(ResolutionApproach.BIND_CLASS))
      return resolveToClassInstance(abstractionType);
    else if (this.resolutionApproaches.get(abstractionType).equals(ResolutionApproach.BIND_INSTANCE))
      return resolveToConcreteInstance(abstractionType);
    else
      throw new Error("Podobno niemożliwe!");
  }

  private Object resolveToClassInstance(Class abstractionType)
      throws IllegalAccessException, InstantiationException, InvocationTargetException {
    Class implementationType = this.typeMapping.get(abstractionType);
    if (this.typesWhichAreRequiredToBeSingletons.contains(implementationType))
      return resolveToSingletonInstance(implementationType);
    else
      return recursivelyInstantiateClass(implementationType);
  }

  private Object resolveToSingletonInstance(Class singletonImplementationType)
      throws InstantiationException, IllegalAccessException, InvocationTargetException {
    if (this.alreadySpawnedSingletons.contains(singletonImplementationType)) {
      return this.spawnedSingletonInstances.get(singletonImplementationType);
    }
    Object newInstance = recursivelyInstantiateClass(singletonImplementationType);
    this.alreadySpawnedSingletons.add(singletonImplementationType);
    this.spawnedSingletonInstances.put(singletonImplementationType, newInstance);
    return newInstance;
  }

  private Object resolveToConcreteInstance(Class abstractionType) {
    return this.instanceMapping.get(abstractionType);
  }

  private Object recursivelyInstantiateClass(Class classToInstantiate)
      throws IllegalAccessException, InvocationTargetException, InstantiationException {
    Constructor[] constructors = classToInstantiate.getDeclaredConstructors();
    Constructor maxParameterCountConstructor =
        Arrays.stream(constructors).max(Comparator.comparing(c -> c.getParameterCount())).get();
    if (maxParameterCountConstructor.getParameterCount() == 0)
      return maxParameterCountConstructor.newInstance();
    Class[] parameterTypes = maxParameterCountConstructor.getParameterTypes();
    return maxParameterCountConstructor.newInstance(
        Arrays.stream(parameterTypes).map(cls -> {
          try {
            return resolve(cls);
          } catch (Exception e) {
            e.printStackTrace();
            return null;  // let's hope this won't happen
          }
        })
    );
  }

  private boolean isAbstractionTypeEligibleToBeResolved(Class abstractionType) {
    return this.typeMapping.containsKey(abstractionType) ||
           this.instanceMapping.containsKey(abstractionType);
  }
}
