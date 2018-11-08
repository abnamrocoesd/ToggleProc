package cielo.toggleproc.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * Created by bsobat on 08/11/2017.
 */

public abstract class ToggleProcessor extends AbstractProcessor {

    protected void printError(String message) {
        Diagnostic.Kind kind = Diagnostic.Kind.ERROR;
        print(kind, message);
    }

    protected void printWarn(String message){
        Diagnostic.Kind kind = Diagnostic.Kind.WARNING;
        print(kind, message);
    }

    protected void printDebug(String message){
        Diagnostic.Kind kind = Diagnostic.Kind.NOTE;
        print(kind, message);
    }

    private void print(Diagnostic.Kind kind, String message) {
        Messager messager = processingEnv.getMessager();
        messager.printMessage(kind, message);
    }
}
