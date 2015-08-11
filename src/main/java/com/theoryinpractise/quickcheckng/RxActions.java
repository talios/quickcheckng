package com.theoryinpractise.quickcheckng;

import rx.functions.Action1;

public class RxActions {

  /**
   * Returns an action that filters out expected exceptions, useful for testing.
   */
  public static <T> Action1<T> actionExpectingException(final Action1<T> originalAction, final Class<? extends Throwable>... throwables) {
    return new Action1<T>() {
      @Override
      public void call(T value) {
        try {
          originalAction.call(value);
        } catch (Throwable exception) {
          for (Class<? extends Throwable> throwable : throwables) {
            if (throwable.isAssignableFrom(exception.getClass())) {
              return;
            }
          }
          throw exception;
        }
      }
    };
  }

}
