import Classes.Person;
import toObject.*;

import java.util.List;
import java.util.Map;

public class ParserTest {
    public static void main(String[] args) {

        String jsonInput = "{\"name\":\"John Doe\",\"age\":30,\"isStudent\":false,\"scores\":[100, 95.5, 88],\"address\":{\"city\":\"New York\",\"zip\":\"10001\"}}";

        LexAnalysing lexer = new LexAnalysing(jsonInput);
        List<Lexem> tokens = lexer.tokenize();
        if (tokens.isEmpty()) {
            throw new IllegalStateException("Token list is empty. Check the lexer.");
        }
        Parser parser = new Parser(tokens);

        try {
            // To Map<String, Object>
            Map<String, Object> resultMap = parser.parse(Map.class);
            System.out.println("Map Output: " + resultMap);

            // To specified class
            Person person = parser.parse(Person.class);
            System.out.println("Classes.Person Name: " + person.getName() + ", Age: " + person.getAge());

            // To Java Object (Выборочный доступ к значению по ключу)
            Integer age = parser.parseByKey("age", Integer.class);
            System.out.println("Age from JSON: " + age);

            // To Java Object (Выборочный доступ к значению по ключу)
            List<Object>  scores = parser.parseByKey("scores", List.class);
            System.out.println("Scores from JSON: " + scores);

            // To Java Object
            Object genericObject = parser.parse();
            System.out.println("Generic Object Output: " + genericObject);

            System.out.println("All tests passed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Test failed: " + e.getMessage());
        }
    }
}

