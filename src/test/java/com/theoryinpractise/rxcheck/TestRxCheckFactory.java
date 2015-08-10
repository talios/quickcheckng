package com.theoryinpractise.rxcheck;

import org.testng.annotations.Factory;
import rx.functions.Action1;

import static com.google.common.truth.Truth.assertThat;
import static com.theoryinpractise.quickcheckng.RxCheckFactory.forAll;
import static com.theoryinpractise.quickcheckng.observables.StringObservable.observeLetterStrings;

public class TestRxCheckFactory {

  @Factory
  public Object[] rxCheckFactory() {


    return forAll(observeLetterStrings(1, 300),
                  new Action1<String>() {
      @Override
      public void call(String string) {
        assertThat(string).isNotEmpty();
      }
    },
                  new Action1<String>() {
                    @Override
                    public void call(String string) {
                      assertThat(string.length()).isLessThan(301);
                    }
                  })
        .test();

  }

}
