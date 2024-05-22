import  toObject.Lexem;
import toObject.LexAnalysing;

import java.util.List;

public class LexAnalysingTest {
    public static void main(String[] args) {
        String jsonInput = "{\"name\": \"John\",   \"age\": 30,    \"isStudent\": false, \"scores\": [100, 90.5, 80], \n \"address\": {\"city\": New York, \"zip\": \"0\"}}";

        LexAnalysing tokenizer = new LexAnalysing(jsonInput);
        List<Lexem> tokens = tokenizer.tokenize();

        for (Lexem token : tokens) {
            System.out.println("Type: " + token.getType() + ", Value: '" + token.getValue() + "'");
        }
    }
}