package mars.src.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("mars.src.processor.RequestFactory")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class RequestFactoryProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        
        for (Element element : roundEnv.getElementsAnnotatedWith(RequestFactory.class)) {

            if (element.getKind() == ElementKind.INTERFACE || element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                generateFactory(typeElement);
            }
        }
        return true;
    }

    private void generateFactory(TypeElement outerElement) {
        String interfaceName = outerElement.getSimpleName().toString();
 
        String packageName = processingEnv.getElementUtils().getPackageOf(outerElement).getQualifiedName().toString();
        String factoryClassName = interfaceName + "Factory";

        TypeSpec.Builder factoryBuilder = TypeSpec.classBuilder(factoryClassName)
                .addModifiers(Modifier.PUBLIC);

        for (Element enclosed : outerElement.getEnclosedElements()) {
            
            if (enclosed.getKind() == ElementKind.CLASS) {
                TypeElement innerClass = (TypeElement) enclosed;
                String className = innerClass.getSimpleName().toString();
                
                String fieldName = Character.toLowerCase(className.charAt(0)) + className.substring(1);

                ExecutableElement constructor = getBestConstructor(innerClass);
                String args = "";
                
                if (constructor != null) {

                    args = constructor.getParameters().stream()
                            .map(param -> getDefaultValueForType(param.asType()))
                            .collect(Collectors.joining(", "));
                }

                ClassName innerClassName = ClassName.get(innerClass);

                FieldSpec field = FieldSpec.builder(innerClassName, fieldName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new $T($L)", innerClassName, args)
                        .build();

                factoryBuilder.addField(field);
            }
        }

        JavaFile javaFile = JavaFile.builder(packageName, factoryBuilder.build())
                .indent("    ")
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ExecutableElement getBestConstructor(TypeElement classElement) {
        ExecutableElement best = null;
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructor = (ExecutableElement) enclosed;

                if (constructor.getParameters().isEmpty()) {
                    return constructor;
                }
                best = constructor;
            }
        }
        return best;
    }

    private String getDefaultValueForType(TypeMirror type) {
        switch (type.getKind()) {
            case BOOLEAN: return "false";
            case BYTE: case SHORT: case INT: return "0";
            case LONG: return "0L";
            case FLOAT: return "0.0f";
            case DOUBLE: return "0.0";
            case CHAR: return "'\\0'";
            default: return "null";
        }
    }
}
