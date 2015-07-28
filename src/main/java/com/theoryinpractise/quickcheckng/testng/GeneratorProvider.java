package com.theoryinpractise.quickcheckng.testng;

import net.java.quickcheck.Generator;
import rx.Observable;
import rx.Subscriber;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static net.java.quickcheck.generator.iterable.Iterables.toIterable;

public class GeneratorProvider {

  public static <T> Iterator<Object[]> toObjectArrayIterator(Generator<T> generator) {
    return toObjectArrayIterator(toIterable(generator));
  }

  public static <T> Iterator<Object[]> toObjectArrayIterator(Observable<T> observable) {
    return toObjectArrayIterator(observable.toBlocking().toIterable());
  }

  private static <T> Iterator<Object[]> toObjectArrayIterator(Iterable<T> iterable) {
    Set<Object[]> dataPoints = new HashSet<>();
    for (T value : iterable) {
      dataPoints.add(new Object[] {value});
    }
    return dataPoints.iterator();
  }

  public static <T> Observable<T> fromGenerator(final Generator<T> generator) {
    return Observable.create(new Observable.OnSubscribe<T>() {
      @Override
      public void call(Subscriber<? super T> subscriber) {
        subscriber.onStart();
        for (T value : toIterable(generator)) {
          subscriber.onNext(value);
        }
        subscriber.onCompleted();
      }
    });
  }

}
