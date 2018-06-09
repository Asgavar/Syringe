package xyz.juraszek.syringe;

import java.util.ArrayList;

public class VisitedClassesTracker {
  private ArrayList<Class> visitedClasses;

  public VisitedClassesTracker() {
    this.visitedClasses = new ArrayList<>();
  }

  public void markClassAsVisited(Class classToMark) {
    this.visitedClasses.add(classToMark);
  }

  public boolean wasClassAlreadyVisited(Class classToCheck) {
    return this.visitedClasses.contains(classToCheck);
  }

  public void reset() {
    this.visitedClasses.clear();
  }
}
