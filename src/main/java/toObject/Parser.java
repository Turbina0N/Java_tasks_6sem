package toObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private List<Lexem> tokens;
    private int position = 0;

    private JsonMapper jsonMapper = new JsonMapper();
    public Parser(List<Lexem> tokens) {
        this.tokens = tokens;
    }

    public Object parse() throws Exception {
        if (tokens.isEmpty()) {
            throw new IllegalStateException("No tokens available for parsing.");
        }
        try {
            position = 0;
            Lexem current = peek();
            if (current.getType() == Lexem.Type.BracketObjectLeft) {
                return parseObject();
            } else {
                throw new IllegalArgumentException("JSON must start with an object. Arrays are not supported at the top level.");
            }
        } finally {
            position = 0;
        }
    }

    public <T> T parse(Class<T> clazz) throws Exception {
        Object result = parse();
        if (clazz.isInstance(result)) {
            return clazz.cast(result);
        } else if (result instanceof Map) {
            return jsonMapper.mapToObject((Map<String, Object>) result, clazz);
        }
        throw new IllegalArgumentException("Cannot cast parsed object to " + clazz.getName());
    }
    private Object parseObject() throws Exception {
        consume(Lexem.Type.BracketObjectLeft);
        Map<String, Object> obj = new HashMap<>();
        while (peek().getType() != Lexem.Type.BracketObjectRight) {
            Lexem keyLexem = consume(Lexem.Type.String);
            consume(Lexem.Type.Colon);
            Object value = parseValue();
            obj.put(keyLexem.getValue(), value);
            if (peek().getType() == Lexem.Type.Comma) {
                consume(Lexem.Type.Comma);
            }
        }
        consume(Lexem.Type.BracketObjectRight);
        return obj;
    }

    private Object parseArray() throws Exception {
        consume(Lexem.Type.BracketArrayLeft);
        List<Object> array = new ArrayList<>();
        while (peek().getType() != Lexem.Type.BracketArrayRight) {
            array.add(parseValue());
            if (peek().getType() == Lexem.Type.Comma) {
                consume(Lexem.Type.Comma);
            }
        }
        consume(Lexem.Type.BracketArrayRight);
        return array;
    }

    private Object parseValue() throws Exception {
        Lexem lexem = peek();
        switch (lexem.getType()) {
            case Number:
                consume(Lexem.Type.Number);
                return parseNumber(lexem);
            case String:
                consume(Lexem.Type.String);
                return lexem.getValue();
            case True:
            case False:
                consume(lexem.getType());
                return Boolean.parseBoolean(lexem.getValue());
            case Null:
                consume(Lexem.Type.Null);
                return null;
            case BracketObjectLeft:
                return parseObject();
            case BracketArrayLeft:
                return parseArray();
            default:
                throw new IllegalArgumentException("Unexpected value type: " + lexem.getValue());
        }
    }

    public static Number parseNumber(Lexem lexem) {
        String content = lexem.getValue();
        if (content.contains(".") || content.contains("E") || content.contains("e")) {
            return Double.parseDouble(content);
        }
        return Integer.parseInt(content);
    }

    private Lexem consume(Lexem.Type expectedType) throws Exception {
        Lexem lexem = tokens.get(position++);
        if (lexem.getType() != expectedType) {
            throw new IllegalArgumentException("Expected " + expectedType + " but found " + lexem.getType());
        }
        return lexem;
    }

    private Lexem peek() {
        if (position >= tokens.size()) {
            throw new IndexOutOfBoundsException("Attempt to access beyond end of token list");
        }
        return tokens.get(position);
    }

    public <T> T parseByKey(String key, Class<T> type) throws Exception {
        Object result = parse();
        Object value = findValueByKey(result, key);
        if (value == null) {
            throw new IllegalArgumentException("Key not found: " + key);
        }
        return convertToType(value, type);
    }

    private <T> T convertToType(Object value, Class<T> type) throws Exception {
        if (type.isInstance(value)) {
            return type.cast(value);
        } else if (value instanceof Number) {
            return convertNumberToType((Number) value, type);
        } else if (value instanceof String && type == String.class) {
            return type.cast(value);
        } else if (value instanceof Boolean && (type == Boolean.class || type == boolean.class)) {
            return type.cast(value);
        }
        throw new ClassCastException("Cannot cast the object of type " + value.getClass().getSimpleName() + " to " + type.getSimpleName());
    }

    private <T> T convertNumberToType(Number number, Class<T> type) {
        if (type == Integer.class || type == int.class) {
            return type.cast((int) number.doubleValue());
        } else if (type == Double.class || type == double.class) {
            return type.cast(number.doubleValue());
        } else if (type == Float.class || type == float.class) {
            return type.cast(number.floatValue());
        } else if (type == Long.class || type == long.class) {
            return type.cast(number.longValue());
        } else if (type == Short.class || type == short.class) {
            return type.cast((short) number.doubleValue());
        } else if (type == Byte.class || type == byte.class) {
            return type.cast((byte) number.doubleValue());
        }
        throw new IllegalArgumentException("Unsupported number conversion to " + type.getSimpleName());
    }

    private Object findValueByKey(Object current, String key) {
        if (current instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) current;
            if (map.containsKey(key)) {
                return map.get(key);
            }
            for (Object value : map.values()) {
                Object found = findValueByKey(value, key);
                if (found != null) return found;
            }
        } else if (current instanceof List<?>) {
            for (Object item : (List<?>) current) {
                Object found = findValueByKey(item, key);
                if (found != null) return found;
            }
        }
        return null;
    }
}

