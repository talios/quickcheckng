package com.theoryinpractise.quickcheckng.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.theoryinpractise.quickcheckng.DataProviders;
import com.theoryinpractise.quickcheckng.testng.GeneratorProvider;
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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class DataProviderProcessor
    extends AbstractProcessor {

  private static final ParameterizedTypeName objectArrayIterator = ParameterizedTypeName.get(Iterator.class, Object[].class);

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

    Writer srcWriter = processingEnv.getFiler().createSourceFile(generatedQualifiedClassName(type)).openWriter();

    final TypeSpec.Builder builder = TypeSpec.classBuilder(generatedSimpleClassName(type)).addModifiers(PUBLIC, FINAL);

    for (Element element : type.getEnclosedElements()) {
      if (element.getKind() == ElementKind.METHOD && element.getModifiers().containsAll(EnumSet.of(PUBLIC, STATIC))) {

        for (Element element1 : element.getEnclosedElements()) {
          reportNote(element1.toString(), element);
        }


        MethodSpec methodSpec = MethodSpec.methodBuilder(element.getSimpleName().toString())
                                          .addModifiers(PUBLIC, STATIC)
                                          .addAnnotation(DataProvider.class)
                                          .addJavadoc("TestNG @DataProvider for " + element.getSimpleName() + "\n")
                                          .returns(objectArrayIterator)
                                          .addStatement("return $T.toObjectArrayIterator(" + type.getSimpleName() + "." + element.toString() + ")",
                                                        GeneratorProvider.class)
                                          .build();

        builder.addMethod(methodSpec);

      }
    }


    TypeSpec helloWorld = builder.build();

    JavaFile javaFile = JavaFile.builder(packageOf(type), helloWorld)
                                .skipJavaLangImports(true)
                                .build();

    javaFile.writeTo(srcWriter);

    srcWriter.close();

  }

  private String generatedSimpleClassName(TypeElement type) {
    return type.getSimpleName().toString() + "DataProviders";
  }
  private String generatedQualifiedClassName(TypeElement type) {
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
