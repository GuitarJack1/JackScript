
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class Environment {

    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();
    private final HashSet<String> vars = new HashSet<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        if (vars.contains(name.lexeme)) {
            throw new RuntimeError(name,
                    "Variable '" + name.lexeme + "' never assigned.");
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    void define(Token token, Object value, boolean isBeingDefined) {
        if (vars.contains(token.lexeme)) {
            throw new RuntimeError(token, "Duplicate variable '" + token.lexeme + "'.");
        }

        vars.add(token.lexeme);

        if (isBeingDefined) {
            values.put(token.lexeme, value);
        }
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    void assign(Token name, Object value) {
        if (vars.contains(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }

}
