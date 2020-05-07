package csx.com.lib;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import csx.com.annotation_lib.AptTestAnnotation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Date: 2020/5/6
 *
 * create by cuishuxiang
 *
 * description:
 *
 *  ButterKnife
 * https://github.com/JakeWharton/butterknife/blob/master/butterknife-compiler/src/main/java/butterknife/compiler/ButterKnifeProcessor.java
 */
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {

  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;
  private Messager messager;

  /**
   *
   * @param processingEnvironment
   */
  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);

    elementUtils = processingEnvironment.getElementUtils();
    typeUtils = processingEnvironment.getTypeUtils();
    filer = processingEnvironment.getFiler();
    messager = processingEnvironment.getMessager();
  }

  /**
   * 获取配置
   * @return
   */
  @Override
  public Set<String> getSupportedOptions() {
    return super.getSupportedOptions();
  }

  /**
   * 指定java版本
   * @return
   */
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  /**
   * 注册自定义的注解名称
   * @return
   */
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();

    //
    types.add(AptTestAnnotation.class.getCanonicalName());

    if (types.size()!=0)
      return types;

    return super.getSupportedAnnotationTypes();
  }

  /**
   * 主要的方法也是在这里编写
   * @param set
   * @param roundEnvironment
   * @return
   *
   *
   *  public static void printName(int age) {
   *         System.out.println("name");
   *  }
   *
   */
  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
    Set<? extends Element> elements = roundEnvironment
        .getElementsAnnotatedWith(AptTestAnnotation.class);

    if (elements==null||elements.isEmpty())
      return false;

    for (Element element : elements) {

      AptTestAnnotation annotation = element.getAnnotation(AptTestAnnotation.class);
      //
      String name = annotation.getName();

      //，如：public static
      ExecutableElement methodElement = (ExecutableElement) element;
      Set<Modifier> modifiers = methodElement.getModifiers();
      //
      TypeMirror methodReturnType = methodElement.getReturnType();
      String methodName = methodElement.getSimpleName().toString();
      //
      List<? extends VariableElement> parameters = methodElement.getParameters();
      //params map
      HashMap<String, TypeMirror> parasNameAndClass = new HashMap<>();
      for (VariableElement parameter : parameters) {
        String paramName = parameter.getSimpleName().toString();
        TypeMirror paramType = parameter.asType();
        parasNameAndClass.put(paramName, paramType);
      }

      //class
      Element enclosingElement = element.getEnclosingElement();
      if (enclosingElement.getKind() != ElementKind.CLASS) {
        return false;
      }
      TypeElement classElement = (TypeElement) enclosingElement;
      String className = classElement.getSimpleName().toString();
      String qualifiedName = classElement.getQualifiedName().toString();
      int i = qualifiedName.lastIndexOf(".");
      String packageName = qualifiedName.substring(0, i);
      //param
      ArrayList<ParameterSpec> parameterSpecs = new ArrayList<>();
      Set<String> paramsSet = parasNameAndClass.keySet();
      StringBuffer paramsList = new StringBuffer();
      for (String paramName : paramsSet) {
        ParameterSpec parameterSpec = ParameterSpec
            .builder(TypeName.get(parasNameAndClass.get(paramName)), paramName, Modifier.FINAL).build();
        parameterSpecs.add(parameterSpec);
        if (paramsList.length() != 0) {
          paramsList.append(",");
        }
        paramsList.append(paramName);
      }

      //method void
      Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
          .addModifiers(modifiers)
          .addParameter(ClassName.get(packageName, className), "obj", Modifier.FINAL)
          .returns(TypeName.VOID);

      for (ParameterSpec parameterSpec : parameterSpecs) {
        methodSpecBuilder.addParameter(parameterSpec);
      }

      //method code
//      String threadCountVariable = "getName";

      methodSpecBuilder
          .addStatement("String getName=$S", name)
          .addStatement("$T.out.println($S+$S)", ClassName.get(System.class), "Apt Test log :", name);


      MethodSpec methodSpec = methodSpecBuilder.build();

      //class
      TypeSpec typeSpec = TypeSpec.classBuilder(className + "_AptTest")
          .addModifiers(Modifier.PUBLIC)
          .addMethod(methodSpec)
          .build();
      //generate File
      JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
      try {
        javaFile.writeTo(filer);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

    return false;
  }
}
