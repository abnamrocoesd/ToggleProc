package com.abnamro.nl.toggle.processor;

import com.abnamro.nl.toggle.annotation.FeatureToggleBind;
import com.abnamro.nl.toggle.annotation.FeatureToggleConfiguration;
import com.abnamro.nl.toggle.processor.exceptions.ToggleExpiredException;
import com.abnamro.nl.toggle.processor.exceptions.ToggleTooFuturisticException;
import com.abnamro.nl.toggle.processor.model.*;
import com.abnamro.nl.toggle.toggler.FeatureToggler;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static javax.lang.model.element.Modifier.*;

@SupportedAnnotationTypes("FeatureToggleProcessor")
public class FeatureToggleProcessor extends ToggleProcessor {
    private static final long FEATURE_FLAG_TTL = 4838400000l;//8 weeks
    public static final String STRICTNESS_POLICY = "STRICTNESS_POLICY";
    public static final String MAX_NUMBER_OF_TOGGLES = "NUMBER_OF_TOGGLES";
    public static final String CLASS_NAME = "FeatureToggleBinder";
    private Set<String> toggleCount = new HashSet<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {
            {
                add(FeatureToggleBind.class.getCanonicalName());
                add(FeatureToggleConfiguration.class.getCanonicalName());
            }

        };
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        printDebug("Start FeatureToggleProcessor");
        Set<? extends Element> configs = roundEnvironment.getElementsAnnotatedWith(FeatureToggleConfiguration.class);
        Set<JavaFile> javaFiles = new HashSet<>();
        for (Element elem : configs) {
            TypeElement classElement = (TypeElement) elem;
            BindingSet bindingSet = buildBindingSet(classElement);
            javaFiles.addAll(generateBinderClasses(classElement, bindingSet));
        }

        writeFiles(javaFiles);
        return true;
    }

    private BindingSet buildBindingSet(TypeElement classElement) {
        BindingSet bindingSet = new BindingSet();
        List<? extends Element> enclosedElements = classElement.getEnclosedElements();

        for (Element enclosedElement : enclosedElements) {
            if (enclosedElement.getAnnotation(FeatureToggleBind.class) != null) {
                addElementBinding(bindingSet, enclosedElement);
            }
        }
        return bindingSet;
    }

    private void addElementBinding(BindingSet bindingSet, Element element) {
        FeatureToggleBinding elementBinding = getToggleBinding(element);
        if (elementBinding == null) {
            return;
        }

        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        PackageElement packageElement = getPackage(classElement);

        bindingSet.addBinding(packageElement, classElement, elementBinding);
        PackageBinding packageBinding = bindingSet.getPackageBinding(packageElement);

        FeatureToggleConfiguration annotation = classElement.getAnnotation(FeatureToggleConfiguration.class);
        int numberOfToggles = annotation.maxNumberOfToggles();
        FeatureToggleConfiguration.StrictnessPolicy policy = annotation.strictnessPolicy();

        packageBinding.getProperties().put(STRICTNESS_POLICY, policy);
        packageBinding.getProperties().put(MAX_NUMBER_OF_TOGGLES, numberOfToggles);
    }

    private PackageElement getPackage(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }

        return (PackageElement) element;
    }

    private FeatureToggleBinding getToggleBinding(Element element) {
        if (!isFieldAccessible(element)) {
            printError("Field not accessible, it cannot be private or static to bind");
            return null;
        }

        FeatureToggleBind annotation = element.getAnnotation(FeatureToggleBind.class);
        String name = annotation.toggleName();
        String date = annotation.expirationDate();

        TypeMirror type = element.asType();
        String elemName = element.getSimpleName().toString();

        return new FeatureToggleBinding(type, elemName, name, date);
    }


    private void checkExpirationDate(String sDate, String name) throws ToggleExpiredException, ToggleTooFuturisticException {
        printDebug("Date: " + sDate);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        try {
            startDate = df.parse(sDate);
            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            long time = c.getTimeInMillis();
            long now = System.currentTimeMillis();
            if (now > time) {
                throw new ToggleExpiredException();
            }

            if (time > now + FEATURE_FLAG_TTL) {
                throw new ToggleTooFuturisticException();
            }

        } catch (ParseException e) {
            printWarn("Unable to parse date " + sDate + " format(yyyy-MM-dd) for toggle " + name);
        }
    }

    private boolean isFieldAccessible(Element element) {
        Set<Modifier> modifiers = element.getModifiers();
        return !modifiers.contains(PRIVATE) && !modifiers.contains(STATIC);
    }

    private Set<JavaFile> generateBinderClasses(TypeElement classElement, BindingSet bindingSet) {
        Set<JavaFile> files = new HashSet<>();

        for (PackageBinding packageBinding : bindingSet.getPackageBindings()) {
            String packageName = packageBinding.getPackageName();
            TypeSpec binderClass = generateBinderClass(classElement, packageBinding);

            JavaFile javaFile = JavaFile.builder(packageName, binderClass).build();
            files.add(javaFile);
        }
        return files;
    }

    private TypeSpec generateBinderClass(TypeElement classElement, PackageBinding packageBinding) {
        printDebug("Generating "+CLASS_NAME);

        ClassName className = ClassName.get(classElement);
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(PRIVATE)
                .build();

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(CLASS_NAME)
                .addModifiers(FINAL)
                .addMethod(constructor);

        int numberOfToggles = (int) packageBinding.getProperties().get(MAX_NUMBER_OF_TOGGLES);
        FeatureToggleConfiguration.StrictnessPolicy policy = (FeatureToggleConfiguration.StrictnessPolicy) packageBinding.getProperties().get(STRICTNESS_POLICY);
        printDebug("STRICTNESS_POLICY "+policy.name());
        for (ClassBinding classBinding : packageBinding.getClassBindings()) {

            MethodSpec methodSpec = generateBindMethod(classBinding, numberOfToggles, policy);
            classBuilder.addMethod(methodSpec);
        }

        return classBuilder.build();
    }


    private MethodSpec generateBindMethod(ClassBinding classBinding, int numberOfToggles, FeatureToggleConfiguration.StrictnessPolicy policy) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(FeatureToggler.class, "toggler")
                .addParameter(ClassName.get(classBinding.getClassElement()), "target");

        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        for (ToggleBinding elementBinding : classBinding.getToggleBindings()) {

            FeatureToggleBinding fElement = (FeatureToggleBinding) elementBinding;

            String name = fElement.getToggleName();
            String date = fElement.getExpirationData();

            String varName = elementBinding.getName();
            toggleCount.add(varName);

            methodBuilder.addStatement("target.$N = ($T) toggler.isEnabled($S)",
                    varName,
                    ClassName.get(elementBinding.getType()),
                    name);
            try {
                checkExpirationDate(date, name);
            } catch (ToggleExpiredException e) {
                String mssg = "Toggle (" + name + " , " + date + ") is expired at: " + classBinding.getClassElement().getQualifiedName().toString();
                if(policy == FeatureToggleConfiguration.StrictnessPolicy.MODERATE || policy == FeatureToggleConfiguration.StrictnessPolicy.HARSH) {
                    errors.add(mssg);
                } else {
                    warnings.add(mssg);
                }
            } catch (ToggleTooFuturisticException e) {
                String mssg = "Toggle (" + name + " , " + date + ") is too futuristic at: " + classBinding.getClassElement().getQualifiedName().toString();
                if(policy == FeatureToggleConfiguration.StrictnessPolicy.HARSH){
                    errors.add(mssg);
                } else {
                    warnings.add(mssg);
                }
            }
        }

        printDebug("Number of toggles: " + toggleCount.size());
        for (String error : errors) {
            printError(error);
        }

        for (String warning : warnings) {
            printWarn(warning);
        }

        if (toggleCount.size() > numberOfToggles) {
            if(policy == FeatureToggleConfiguration.StrictnessPolicy.HARSH){
                printError("Too many toggles");
            } else {
                printWarn("Too many toggles");
            }
        }

        return methodBuilder.build();
    }

    private void writeFiles(Collection<JavaFile> javaFiles) {
        for (JavaFile javaFile : javaFiles) {
            writeFile(javaFile);
        }

    }

    private void writeFile(JavaFile javaFile) {
        Filer filer = processingEnv.getFiler();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            Messager messager = processingEnv.getMessager();
            String message = String.format("Unable to write file: %s", e.getMessage());
            messager.printMessage(Diagnostic.Kind.ERROR, message);
        }
    }
}
