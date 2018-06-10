package xyz.juraszek.syringe.examples;

public class NonTrivialB {
  public NonTrivialC c;
  public NonTrivialD d;

  public NonTrivialB(NonTrivialC c, NonTrivialD d) {
    this.c = c;
    this.d = d;
  }
}
