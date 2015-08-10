package com.theoryinpractise.quickcheckng;

import org.testng.ITest;
import org.testng.annotations.Test;
import rx.Observable;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;

public abstract class RxCheckFactory {

  /**
   * For all elements in the observable, apply the supplied action.
   *
   * @param observable An observable for test data
   * @param action An action containing test assertions
   * @param <T>
   */
  public static <T> RxCheckFactory forAll(Observable<T> observable, Action1<T>... action) {
    return new RxCheckInternal().andAll(observable, action);
  }

  public abstract <T> RxCheckFactory andAll(Observable<T> observable, Action1<T>... action);

  public abstract <T> Object[] test();

  public static class RxCheckInternal extends RxCheckFactory {
    private RxCheckInternal() {
    }

    private List<ITest> tests = new ArrayList<>();

    /**
     * For all elements in the observable, apply the supplied action.
     *
     * @param observable An observable for test data
     * @param actions An list of actions containing test assertions
     * @param <T>
     */
    public <T> RxCheckInternal andAll(Observable<T> observable, final Action1<T>... actions) {

      final Action1<T> combinedAction = combineActions(actions);

      for (T testValue : observable.toBlocking().toIterable()) {
        final T value1 = testValue;
        final Action1<T> action1 = combinedAction;
        tests.add(new RxTest<>(value1, action1));
      }

      return this;
    }

    @Override
    public <T> Object[] test() {
      return tests.toArray(new Object[tests.size()]);
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

  public static class RxTest<T>
      implements ITest {

    private T value;

    private Action1<T> action;

    public RxTest(T value1, Action1<T> action1) {
      value = value1;
      action = action1;
    }

    @Override
    public String getTestName() {
      return String.valueOf(value);
    }

    @Test
    public void testAction() {
      action.call(value);
    }
  }

}
