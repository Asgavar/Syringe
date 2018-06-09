package xyz.juraszek.syringe.examples;

import xyz.juraszek.syringe.annotations.DependencyMethod;

public class DependencyMethodExample {
  public AnExampleInterface toBeInjected;
  public AnExampleInterface doesNotMatter;

  @DependencyMethod
  public void setToBeInjected(AnExampleInterface toBeInjected) {
    this.toBeInjected = toBeInjected;
  }

  public void setDoesNotMatter(AnExampleInterface notNecessary) {
    this.doesNotMatter = notNecessary;
  }
}
