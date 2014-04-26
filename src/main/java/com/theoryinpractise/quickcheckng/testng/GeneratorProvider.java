package com.theoryinpractise.quickcheckng.testng;

import net.java.quickcheck.Generator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static net.java.quickcheck.generator.iterable.Iterables.toIterable;

public class GeneratorProvider {

  public static <T> Iterator<Object[]> toObjectArrayIterator(Generator<T> generator) {
    Set<Object[]> dataPoints = new HashSet<>();
    for (T password : toIterable(generator)) {
      dataPoints.add(new Object[] {password});
    }
    return dataPoints.iterator();
  }

}
