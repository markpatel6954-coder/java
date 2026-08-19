import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TemplateFiller {

    public static String fillTemplate(
            String template, String[] names, String[] values) {

        
        Pattern pattern = Pattern.compile("\\{(\\w+)\\}");
        Matcher matcher = pattern.matcher(template);

        StringBuilder result = new StringBuilder();

        int lastEnd = 0;

        while (matcher.find()) {

        
            result.append(template, lastEnd, matcher.start());

            
            String placeholder = matcher.group(1);

            String replacement = "[?]";

            
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(placeholder)) {
                    replacement = values[i];
                    break;
                }
            }

            
            result.append(replacement);

            lastEnd = matcher.end();
        }

        result.append(template, lastEnd, template.length());

        return result.toString();
    }
}

public class Driver {

    public static void main(String[] args) {

        String template = "Dear {name}, order {id} ships {date}.";

        String[] names = {
            "name",
            "id"
        };

        String[] values = {
            "Riya",
            "A07"
        };

        String result = TemplateFiller.fillTemplate(
                template, names, values);

        System.out.println(result);
    }
}