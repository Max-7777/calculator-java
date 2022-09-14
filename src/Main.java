import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        InputParser iP = new InputParser(ParseFile.parseOperatorData(), ParseFile.parseFunctionData(), ParseFile.parseSymbolData());
        Calc calc = new Calc(ParseFile.parseOperatorData(), ParseFile.parseFunctionData(), ParseFile.parseSymbolData());
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Input: ");
            String input = sc.nextLine();
            if (input.equals("stop")) break;

            //infix notation -> rpn
            String rpn = iP.parseToRPN(input);
            //rpn -> solution
            String output = calc.solveRPN(rpn).toString();
            System.out.println("Output: " + output);

            String symbolValue = calc.symbolValue(Float.parseFloat(output));
            if (!symbolValue.equals("")) System.out.println("[" + symbolValue + "]");
        }
    }
}