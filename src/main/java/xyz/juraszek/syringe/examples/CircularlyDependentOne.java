package xyz.juraszek.syringe.examples;

public class CircularlyDependentOne {
  private CircularlyDependentTwo two;

  public CircularlyDependentOne(CircularlyDependentTwo two) {
    this.two = two;
  }
}
