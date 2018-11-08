package cielo.toggleproc.processor.model;

import javax.lang.model.type.TypeMirror;

public abstract class ToggleBinding {
    private final TypeMirror type;
    private final String name;

    public TypeMirror getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ToggleBinding(TypeMirror type, String name) {

        this.type = type;
        this.name = name;
    }
}
