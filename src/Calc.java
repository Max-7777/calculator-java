import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calc {

    HashMap<String, Integer> operatorData;
    HashMap<String, Integer> functionData;
    HashMap<String, Double> symbolData;
    DecimalFormat df;

    public Calc(HashMap<String, Integer> operatorData,HashMap<String, Integer> functionData, HashMap<String, Double> symbolData) {
        this.operatorData = operatorData;
        this.functionData = functionData;
        this.symbolData = symbolData;
        this.df = new DecimalFormat("########.##");
    }

    //solving function, rpn expression -> solution
    public Double solveRPN(String input) {
        List<Double> stack = new ArrayList<>();
        int index = 0;

        //while there are tokens to be read
        while (tokensToBeRead(index, input)) {
            String token = readToken(index, input);

            //advance the current index in the input
            index  = advanceIndex(index, input);

            //if not valid token
            if (!validToken(token)) throw new ArithmeticException("Unidentified Token: " + "'" + token + "'");

            String tokenClass = classifyToken(token);

            /*
            numeric token: add it to stack
            symbol token: add it to stack (same thing as number)
            operator/function: operate on the appropriate numbers at the top of the stack then add that solution to the top of the stack
             */
            switch (tokenClass) {
                case "number" -> stack.add(0,Double.parseDouble(token));
                case "symbol" -> stack.add(0,symbolData.get(token));
                case "operator", "function" -> {
                    Double operateSol = operate(token, stack);
                    removeFromStack(token, tokenClass, stack);
                    stack.add(0, operateSol);
                }
            }
        }

        if (stack.size() > 1) System.out.println("Resulting stack was greater than 1: " + stack.size());
        //no numbers left :(
        else if (stack.size() < 1) throw new ArithmeticException("Resulting stack size is 0");

        //return solution (round it to zero if it's a tiny decimal close to 0 ex: 4.5E-13)
        return roundToZero(stack.get(0));
    }

    private int advanceIndex(int index, String s) {
        //ignore first ' '
        if (s.charAt(index) == ' ' && index < s.length() - 1) index++;

        while (s.charAt(index) != ' ' && index < s.length() - 1)  {
            index++;
        }

        return index;
    }

    private String classifyToken(String token) {
        if (numeric(token)) return "number";
        if (symbolData.containsKey(token)) return "symbol";
        if (operatorData.containsKey(token)) return "operator";
        if (functionData.containsKey(token)) return "function";
        throw new ArithmeticException("Classify Token Error: " + "'" + token + "'");
    }

    private boolean validToken(String token) {
        boolean o = false;

        token = token.replace(" ","");

        if (numeric(token)
                || symbolData.containsKey(token)
                || operatorData.containsKey(token)
                || functionData.containsKey(token)) {
            o = true;
        }

        return o;
    }

    private String readToken(int index, String s) {
        StringBuilder token = new StringBuilder();

        //ignored beginning ' '
        if (index != s.length() - 1 && s.charAt(index) == ' ') index++;

        for (int i = index; i < s.length(); i++) {
            //if reach subsequent ' ' or end of string return token
            if (s.charAt(i) == ' ' || i == s.length() - 1) {
                if (i == s.length() - 1) token.append(s.charAt(i));

                return token.toString();
            //else continue to append to token
            } else {
                token.append(s.charAt(i));
            }
        }

        return token.toString();
    }

    private boolean tokensToBeRead(int index, String s) {
        return index < s.length() - 1;
    }

    public Double operate(String token, List<Double> stack) {
        switch (token) {
            case "+" -> {
                return stack.get(1) + stack.get(0);
            }
            case "-" -> {
                return stack.get(1) - stack.get(0);
            }
            case "*" -> {
                    return stack.get(1) * stack.get(0);
            }
            case "/" -> {
                return stack.get(1) / stack.get(0);
            }
            case "^" -> {
                return Math.pow(stack.get(1), stack.get(0));
            }
            case "%" -> {
                return stack.get(1) % stack.get(0);
            }
            case "sqrt" -> {
                return Math.sqrt(stack.get(0));
            }
            case "cbrt" -> {
                return Math.cbrt(stack.get(0));
            }
            case "root" -> {
                return Math.pow(stack.get(1), 1/stack.get(0));
            }
            case "sin" -> {
                return Math.sin(stack.get(0));
            }
            case "cos" -> {
                return Math.cos(stack.get(0));
            }
            case "tan" -> {
                return Math.tan(stack.get(0));
            }
            case "cot" -> {
                return 1.0 / Math.tan(stack.get(0));
            }
            case "sec" -> {
                return 1.0 / Math.cos(stack.get(0));
            }
            case "csc" -> {
                return 1.0 / Math.sin(stack.get(0));
            }
            case "asin" -> {
                return Math.asin(stack.get(0));
            }
            case "acos" -> {
                return Math.acos(stack.get(0));
            }
            case "atan" -> {
                return Math.atan(stack.get(0));
            }
            case "acot" -> {
                return 1.0 / Math.atan(stack.get(0));
            }
            case "asec" -> {
                return 1.0 / Math.acos(stack.get(0));
            }
            case "acsc" -> {
                return 1.0 / Math.asin(stack.get(0));
            }
            case "max" -> {
                return Math.max(stack.get(1), stack.get(0));
            }
            case "min" -> {
                return Math.min(stack.get(1), stack.get(0));
            }
            case "abs" -> {
                return Math.abs(stack.get(0));
            }
            default -> throw new ArithmeticException("Unrecognized Token: " + "'" + token + "'");
        }
    }

    private boolean numeric(String s) {
        try {
            double o = Double.parseDouble(s);
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    private void removeFromStack(String token, String tokenClass, List<Double> stack) {
        if (tokenClass.equals("operator")) {
            stack.subList(0, operatorData.get(token)).clear();
        }
        else if (tokenClass.equals("function")) {
            stack.subList(0, functionData.get(token)).clear();
        }
    }

    //if possible converts the input into a (simple decimal * symbol) ex: [1.5pi] [5e]
    public String symbolValue(float input) {
        float error = 0.00001f;

        if (near(input, error, 0)) return "";

        for (Map.Entry<String, Double> symbol : symbolData.entrySet()) {
            float remainder = (float) (input % symbol.getValue());

            for (int i = 0; i < 100; i += 25) {
                if (near(remainder, error, (float) ((i*0.01f)*symbol.getValue()))) {
                    return df.format(input / symbol.getValue()) + " " + symbol.getKey();
                }
            }
        }

        return "";
    }

    private boolean near(float input, float range, float target) {
        return input <= target + range && input >= target - range;
    }

    private boolean near(Double input, Double range, Double target) {
        return input <= target + range && input >= target - range;
    }


    private Double roundToZero(Double input) {
        if (near(input, 0.000000001, 0.0)) {
            return 0.0;
        }

        return input;
    }
}