package xyz.juraszek.syringe.examples;

import xyz.juraszek.syringe.annotations.DependencyMethod;

public class BothConstructorAndDependencyMethods {
  public AnExampleInterface first;
  public AnExampleInterface second;
  public AnExampleInterface third;

  public BothConstructorAndDependencyMethods(AnExampleInterface first) {
    this.first = first;
  }

  @DependencyMethod
  public void setSecond(AnExampleInterface newSecond) {
    this.second = newSecond;
  }

  @DependencyMethod
  public void setThird(AnExampleInterface newThird) {
    this.third = newThird;
  }
}
