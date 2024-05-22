package toObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexAnalysing {
    private final String input;
    private final static Pattern PATTERN = Pattern.compile(
            "\\s*(?:" +
                    "(\\{)|" +                      // 1: {
                    "(\\})|" +                      // 2: }
                    "(\\[)|" +                      // 3: [
                    "(\\])|" +                      // 4: ]
                    "(,)|" +                        // 5: ,
                    "(:)|" +                        // 6: :
                    "(true|false|null)|" +          // 7: true, false, null
                    "(-?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)|" + // 8: numbers
                    "\"([^\"]*)\")" +               // 9: strings
                    "\\s*");

    public LexAnalysing(String input) {
        this.input = input;
    }

    public List<Lexem> tokenize() {
        List<Lexem> tokens = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(input);
        while (matcher.find()) {
            if (matcher.group(1) != null) tokens.add(new Lexem(Lexem.Type.BracketObjectLeft, "{"));
            else if (matcher.group(2) != null) tokens.add(new Lexem(Lexem.Type.BracketObjectRight, "}"));
            else if (matcher.group(3) != null) tokens.add(new Lexem(Lexem.Type.BracketArrayLeft, "["));
            else if (matcher.group(4) != null) tokens.add(new Lexem(Lexem.Type.BracketArrayRight, "]"));
            else if (matcher.group(5) != null) tokens.add(new Lexem(Lexem.Type.Comma, ","));
            else if (matcher.group(6) != null) tokens.add(new Lexem(Lexem.Type.Colon, ":"));
            else if (matcher.group(7) != null) {
                if (matcher.group(7).equalsIgnoreCase("true") || matcher.group(7).equalsIgnoreCase("false")) {
                    tokens.add(new Lexem(matcher.group(7).equalsIgnoreCase("true") ? Lexem.Type.True : Lexem.Type.False, matcher.group(7)));
                } else {
                    tokens.add(new Lexem(Lexem.Type.Null, "null"));
                }
            } else if (matcher.group(8) != null) {
                tokens.add(new Lexem(Lexem.Type.Number, matcher.group(8)));
            } else if (matcher.group(9) != null) {
                tokens.add(new Lexem(Lexem.Type.String, matcher.group(9)));
            }
        }
        return tokens;
    }
}