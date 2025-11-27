package dk.sdu.imada.teaching.compiler.fs25.vvpl.interpret;

import java.util.HashMap;
import java.util.Map;

public class Enviroment {
    private final Map<String, Object> values = new HashMap<>();
    final Enviroment outer;

    Enviroment() {
        outer = null;
    }

    Enviroment(Enviroment outer) {
        this.outer = outer;
    }

    Object get(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }
        throw new EnviromentException(name, "Undefined variabel '" + name + "'");
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    void assign(String name, Object value) {
        if (values.containsKey(name)) {
            values.put(name, value);
        }

        throw new EnviromentException(name, "undefined variable '" + name + "'");
    }

    class EnviromentException extends RuntimeException {
        final String cause;

        EnviromentException(String cause, String message) {
            super(message);
            this.cause = cause;
        }
    }

}
