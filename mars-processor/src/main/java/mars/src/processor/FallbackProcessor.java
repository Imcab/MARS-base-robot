package mars.src.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("mars.src.processor.Fallback")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class FallbackProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        
        for (Element element : roundEnv.getElementsAnnotatedWith(Fallback.class)) {
            
            if (element.getKind() == ElementKind.INTERFACE) {
                TypeElement typeElement = (TypeElement) element;
                generate(typeElement);
            }
        }
        return true;
    }

    private void generate(TypeElement interfaceElement) {
        String interfaceName = interfaceElement.getSimpleName().toString();
        String packageName = processingEnv.getElementUtils().getPackageOf(interfaceElement).getQualifiedName().toString();
        
        TypeSpec.Builder dummyClassBuilder = TypeSpec.classBuilder(interfaceName + "Fallback")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(interfaceElement.asType());

        Elements elementUtils = processingEnv.getElementUtils();
        Types typeUtils = processingEnv.getTypeUtils();
        DeclaredType enclosingType = (DeclaredType) interfaceElement.asType();

        for (Element enclosed : elementUtils.getAllMembers(interfaceElement)) {
            
            if (enclosed.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) enclosed;
                String methodName = method.getSimpleName().toString();
                
                if (method.getModifiers().contains(Modifier.STATIC)) {
                    continue;
                }
                
                if (method.getModifiers().contains(Modifier.DEFAULT)) {

                    if (methodName.equals("isFallback")) {
                        MethodSpec.Builder fallbackMethod = MethodSpec.overriding(method, enclosingType, typeUtils);
                        fallbackMethod.addStatement("return true");
                        dummyClassBuilder.addMethod(fallbackMethod.build());
                    }
                    continue;
                }

                if (methodName.equals("equals") || methodName.equals("hashCode") || methodName.equals("toString") || 
                    methodName.equals("getClass") || methodName.equals("notify") || 
                    methodName.equals("notifyAll") || methodName.equals("wait")) {
                    continue;
                }

                MethodSpec.Builder methodBuilder = MethodSpec.overriding(method, enclosingType, typeUtils);
                
                TypeName returnType = TypeName.get(method.getReturnType());
                
                if (returnType == TypeName.BOOLEAN) {
                    methodBuilder.addStatement("return false");
                } else if (returnType == TypeName.DOUBLE || returnType == TypeName.FLOAT) {
                    methodBuilder.addStatement("return 0.0");
                } else if (returnType == TypeName.INT || returnType == TypeName.SHORT || returnType == TypeName.LONG) {
                    methodBuilder.addStatement("return 0");
                } else if (returnType != TypeName.VOID) {
                    methodBuilder.addStatement("return null");
                }

                dummyClassBuilder.addMethod(methodBuilder.build());
            }
        }

        JavaFile javaFile = JavaFile.builder(packageName, dummyClassBuilder.build())
                .indent("    ")
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}