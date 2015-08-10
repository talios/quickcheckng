package com.theoryinpractise.rxcheck;

import com.theoryinpractise.quickcheckng.RxCheck;
import com.theoryinpractise.quickcheckng.observables.StringObservable;
import org.testng.annotations.Test;
import rx.functions.Action1;

import static com.google.common.truth.Truth.assertThat;

public class RxCheckTest {

  @Test
  public void testRxCheck() {

    final Action1<String> nonEmptyStrings = new Action1<String>() {
      @Override
      public void call(String string) {
//        System.out.println(string);
        assertThat(string).isNotEmpty();
      }
    };

    final Action1<String> lessThanThreeHundred = new Action1<String>() {
      @Override
      public void call(String string) {
//        System.out.println(string);
        assertThat(string.length()).isLessThan(301);
      }
    };

//    RxCheck
//        .forAll(StringObservable.observeLetterStrings(1, 300), nonEmptyStrings)
//        .andAll(StringObservable.observeLetterStrings(1, 300), lessThanThreeHundred);

    RxCheck.forAll(StringObservable.observeLetterStrings(1, 300),
                   nonEmptyStrings,
                   lessThanThreeHundred);

  }


}
