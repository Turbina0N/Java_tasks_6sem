package toObject;

public class Lexem {

    public enum Type {
        Number,
        String,
        Null,
        False,
        True,
        BracketObjectLeft,
        BracketObjectRight,
        BracketArrayLeft,
        BracketArrayRight,
        Comma,
        Colon,
        Space
    }

    public final Type type;
    private final String value;

    public Lexem(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }


}

