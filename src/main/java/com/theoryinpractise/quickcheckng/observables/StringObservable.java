package com.theoryinpractise.quickcheckng.observables;

import rx.Observable;

import static com.theoryinpractise.quickcheckng.observables.Observables.fromGenerator;
import static net.java.quickcheck.generator.PrimitiveGenerators.letterStrings;
import static net.java.quickcheck.generator.PrimitiveGenerators.nonEmptyStrings;
import static net.java.quickcheck.generator.PrimitiveGenerators.printableStrings;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

/**
 * An observer that emits a set of random strings
 */
public class StringObservable  {

  public static Observable<String> observeStrings() {
    return fromGenerator(strings());
  }

  public static Observable<String> observeStrings(char first, char last) {
    return fromGenerator(strings(first, last));
  }

  public static Observable<String> observeStrings(String allowedCharacters) {
    return fromGenerator(strings(allowedCharacters));
  }

  public static Observable<String> observeStrings(int max) {
    return fromGenerator(strings(max));
  }

  public static Observable<String> observeStrings(int min, int max) {
    return fromGenerator(strings(min, max));
  }

  public static Observable<String> observeLetterStrings() {
    return fromGenerator(letterStrings());
  }

  public static Observable<String> observeLetterStrings(int min, int max) {
    return fromGenerator(letterStrings(min, max));
  }

  public static Observable<String> observePrintableStrings() {
    return fromGenerator(printableStrings());
  }

  public static Observable<String> observeNonEmptyStrings() {
    return fromGenerator(nonEmptyStrings());
  }

}
