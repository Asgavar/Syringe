package xyz.juraszek.syringe.examples;

public class CircularlyDependentTwo {
  private CircularlyDependentOne one;

  public CircularlyDependentTwo(CircularlyDependentOne one) {
    this.one = one;
  }
}
