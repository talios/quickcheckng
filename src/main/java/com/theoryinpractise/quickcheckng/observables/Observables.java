package com.theoryinpractise.quickcheckng.observables;

import net.java.quickcheck.Generator;
import rx.Observable;
import rx.Subscriber;

import static net.java.quickcheck.generator.iterable.Iterables.toIterable;

public class Observables {

  public static <T> Observable<T> fromGenerator(final Generator<T> generator) {
    return fromGenerator(generator, 200);
  }

  public static <T> Observable<T> fromGenerator(final Generator<T> generator, final int numberOfRuns) {
    return Observable.create(new Observable.OnSubscribe<T>() {
      @Override
      public void call(Subscriber<? super T> subscriber) {
        subscriber.onStart();
        for (T value : toIterable(generator, numberOfRuns)) {
          subscriber.onNext(value);
        }
        subscriber.onCompleted();
      }
    });
  }

}
