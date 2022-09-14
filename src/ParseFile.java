import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class ParseFile {

    public static HashMap<String, Integer> parseOperatorData() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("src/Data/OperatorData.txt"));
        HashMap<String, Integer> o = new HashMap<>();

        while (sc.hasNextLine()) {
            String operator = sc.nextLine();
            Integer i = Integer.parseInt(sc.nextLine());
            o.put(operator, i);
        }

        System.out.println("✔ Operator data read");

        return o;
    }

    public static HashMap<String, Integer> parseFunctionData() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("src/Data/FunctionData.txt"));
        HashMap<String, Integer> o = new HashMap<>();

        while (sc.hasNextLine()) {
            String function = sc.nextLine();
            Integer i = Integer.parseInt(sc.nextLine());
            o.put(function, i);
        }

        System.out.println("✔ Function data read");

        return o;
    }


    public static HashMap<String, Double> parseSymbolData() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("src/Data/SymbolData.txt"));
        HashMap<String, Double> o = new HashMap<>();

        while (sc.hasNextLine()) {
            String symbol = sc.nextLine();
            Double f = Double.parseDouble(sc.nextLine());
            o.put(symbol, f);
        }

        System.out.println("✔ Symbol data read");
        return o;
    }

}
