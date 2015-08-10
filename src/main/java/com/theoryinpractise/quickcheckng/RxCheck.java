package com.theoryinpractise.quickcheckng;

import rx.Observable;
import rx.functions.Action1;

public abstract class RxCheck {

  private static final RxCheckInternal internal = new RxCheckInternal();

  /**
   * For all elements in the observable, apply the supplied action.
   *
   * @param observable An observable for test data
   * @param action An action containing test assertions
   * @param <T>
   */
  public static <T> RxCheck forAll(Observable<T> observable, Action1<T>... action) {
    return internal.andAll(observable, action);
  }

  public abstract <T> RxCheck andAll(Observable<T> observable, Action1<T>... action);

  public static class RxCheckInternal extends RxCheck {
    private RxCheckInternal() {
    }

    /**
     * For all elements in the observable, apply the supplied action.
     *
     * @param observable An observable for test data
     * @param actions An list of actions containing test assertions
     * @param <T>
     */
    public <T> RxCheckInternal andAll(Observable<T> observable, final Action1<T>... actions) {
      observable.toBlocking().forEach(combineActions(actions));
      return this;
    }

    /**
     * Reduce the given actions in to a single action.
     *
     * If the given actions array has a length of 1, simply return the first action, otherwise
     * the function returns a new action that iteratively calls each action.
     *
     * @param actions The actions to combine
     * @param <T> The type of the action target
     * @return The combined action.
     */
    private <T> Action1<T> combineActions(final Action1<T>... actions) {
      Action1<T> targetAction;

      if (actions.length == 1) {
        targetAction = actions[0];
      } else {
        targetAction = new Action1<T>() {
          @Override
          public void call(T t) {
            for (Action1<T> nextAction : actions) {
              nextAction.call(t);
            }
          }
        };
      }
      return targetAction;
    }

  }

}
