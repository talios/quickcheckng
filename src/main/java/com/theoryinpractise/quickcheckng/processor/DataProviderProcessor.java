package com.theoryinpractise.quickcheckng.processor;

import com.squareup.javawriter.JavaWriter;
import com.theoryinpractise.quickcheckng.DataProviders;
import org.testng.annotations.DataProvider;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class DataProviderProcessor extends AbstractProcessor {

  private static final List<Integer> PUBLIC_AND_STATIC_MODIFIERS = Arrays.asList(Modifier.PUBLIC, Modifier.STATIC);

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(DataProviders.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Collection<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(DataProviders.class);

    Collection<? extends TypeElement> types = ElementFilter.typesIn(annotatedElements);
    for (TypeElement type : types) {
      try {
        processType(type);
      } catch (IOException e) {
        reportError(e.getMessage(), type);
      }
    }
    return true;
  }

  private void processType(TypeElement type) throws IOException {

    if (!type.getSimpleName().toString().endsWith("Generators")) {
      reportError("Classes annotated with @DataProviders MUST be named *Generators", type);
    }

    String className = generatedClassName(type);
    Writer srcWriter = processingEnv.getFiler().createSourceFile(className).openWriter();
    JavaWriter writer = new JavaWriter(srcWriter);

    writer.emitPackage(packageOf(type))
          .emitImports(Iterator.class, DataProvider.class)
          .emitStaticImports("com.theoryinpractise.quickcheckng.testng.GeneratorProvider.toObjectArrayIterator")
          .emitEmptyLine()
          .beginType(className, "class", EnumSet.of(PUBLIC, FINAL));

    for (Element element : type.getEnclosedElements()) {
      if (element.getKind() == ElementKind.METHOD && element.getModifiers().containsAll(PUBLIC_AND_STATIC_MODIFIERS)) {
        for (Element element1 : element.getEnclosedElements()) {
          reportNote(element1.toString(), element);
        }

        writer.emitEmptyLine()
              .emitJavadoc("TestNG @DataProvider for the " + element.getSimpleName() + " quick check generator")
              .emitAnnotation(DataProvider.class)
              .beginMethod("Iterator<Object[]>", element.getSimpleName().toString(), EnumSet.of(PUBLIC, STATIC, FINAL))
              .emitStatement("return toObjectArrayIterator(" + type.getSimpleName() + "." + element.toString() + ")")
              .endMethod();
      }
    }

    writer.emitEmptyLine()
          .endType();

    srcWriter.close();

  }

  private String generatedClassName(TypeElement type) {
    return type.getQualifiedName().toString() + "DataProviders";
  }

  private String packageOf(TypeElement type) {
    final String className = type.getQualifiedName().toString();
    return className.substring(0, className.lastIndexOf("."));
  }

  private void reportError(String msg, Element e) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
  }

  private void reportNote(String msg, Element e) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg, e);
  }

}
