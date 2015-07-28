package com.theoryinpractise.quickcheckng.testng;

import net.java.quickcheck.Generator;
import rx.Observable;
import rx.functions.Action1;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static net.java.quickcheck.generator.iterable.Iterables.toIterable;

public class GeneratorProvider {

  public static <T> Iterator<Object[]> toObjectArrayIterator(Generator<T> generator) {
    Set<Object[]> dataPoints = new HashSet<>();
    for (T value : toIterable(generator)) {
      dataPoints.add(new Object[] {value});
    }
    return dataPoints.iterator();
  }

  public static <T> Iterator<Object[]> toObjectArrayIterator(Observable<T> observable) {
    final Set<Object[]> dataPoints = new HashSet<>();

    observable.toBlocking().forEach(new Action1<T>() {
      @Override
      public void call(T value) {
        dataPoints.add(new Object[] {value});
      }
    });

    return dataPoints.iterator();
  }

}
