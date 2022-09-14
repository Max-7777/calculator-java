import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class InputParser {
    HashMap<String, Integer> operatorData;
    HashMap<String, Double> symbolData;
    HashMap<String, Integer> functionData;
    HashMap<String, Integer> precedence;
    HashMap<String, String> associativity;


    public InputParser(HashMap<String, Integer> operatorData, HashMap<String, Integer> functionData, HashMap<String, Double> symbolData) {
        this.operatorData = operatorData;
        this.functionData = functionData;
        this.symbolData = symbolData;
        this.precedence = new HashMap<>();
        precedence.put("^", 4);
        precedence.put("*", 3);
        precedence.put("/", 3);
        precedence.put("+", 2);
        precedence.put("-", 2);

        this.associativity = new HashMap<>();
        associativity.put("^", "right");
        associativity.put("*", "left");
        associativity.put("/", "left");
        associativity.put("+", "left");
        associativity.put("-", "left");
    }

    public String parseToRPN(String input) {
        input = checkNegation(input);
        //debug
        //System.out.println("INPUT: " + input);

        int index = 0;
        ArrayList<String> outputQueue = new ArrayList<>();
        ArrayList<String> stack = new ArrayList<>();

        while (tokensToBeRead(index, input)) {

            String token = readToken(index, input);
            if (!validToken(token)) throw new ArithmeticException("Unidentified Token: " + "'" + token + "'");
            String tokenClass = classifyToken(token);

            switch (tokenClass) {
                case "number" -> {
                        outputQueue.add(token);
                }
                case "symbol" -> {
                    Double symbolValue = symbolData.get(token);
                    outputQueue.add(symbolValue.toString());
                }
                case "function" -> {
                    stack.add(0, token);
                }
                case "operator" -> {
                    try {
                            while ((operatorData.containsKey(stack.get(0)) || stack.get(0).equals(")")) &&
                                    (precedence.get(stack.get(0)) > precedence.get(token) || (precedence.get(stack.get(0)).equals(precedence.get(token)) && associativity.get(token).equals("left")))) {
                                String token2 = stack.get(0);
                                stack.remove(0);
                                outputQueue.add(token2);
                            }
                    } catch (Exception ignored) {}

                    stack.add(0, token);
                }

                case "(" -> {
                    stack.add(0, token);
                }

                case ")" -> {
                    if (stack.size() == 0) throw new ArithmeticException("Mismatched parenthesis");
                    while (!stack.get(0).equals("(")) {
                        String token2 = stack.get(0);
                        stack.remove(0);
                        outputQueue.add(token2);
                        if (stack.size() == 0) throw new ArithmeticException("Mismatched parenthesis");
                    }
                    if (!stack.get(0).equals("(")) throw new ArithmeticException("Mismatched parenthesis");
                    stack.remove(0);
                    if (stack.size() != 0) {
                        if (functionData.containsKey(stack.get(0))) {
                            String token2 = stack.get(0);
                            outputQueue.add(token2);
                            stack.remove(0);
                        }
                    }
                }

                case "ignore" -> {}

            }

            index = advanceIndex(index, token, input);

            //debug
            //System.out.println("TOKEN: " + token);
        }


        //debug
        /*
        for (int i = 0; i < stack.size(); i++) {
            System.out.println(i + ": " + stack.get(i));
        }
         */


        int stackSize = stack.size();

        for (int i = 0; i < stackSize; i++) {
            String tokenClass = classifyToken(stack.get(0));
            if (tokenClass.equals("(") || tokenClass.equals(")")) throw new ArithmeticException("Mismatched parenthesis");

            outputQueue.add(stack.get(0));
            //debug
            //System.out.println("REMOVED: " + stack.get(0));
            stack.remove(0);
        }

        return formattedOutput(outputQueue);
    }

    private String formattedOutput(ArrayList<String> outputQueue) {
        StringBuilder output = new StringBuilder();

        output.append(outputQueue.get(0));

        for (int i = 1; i < outputQueue.size(); i++) {
            output.append(" ").append(outputQueue.get(i));
        }
        //debug
        //System.out.println("FORMAT: " + output.toString());

        return output.toString();
    }

    private boolean tokensToBeRead(int index, String s) {
        return index <= s.length() - 1;
    }

    private String readToken(int index, String s) {
        StringBuilder token = new StringBuilder();

        for (int i = index; i < s.length(); i++) {
            //reached end of string
            if (index == s.length() - 1) {
                token.append(s.charAt(i));
                return token.toString();
            }
            //reached end of number
            if (numeric(token.toString()) && !numeric(String.valueOf(s.charAt(i))) && !String.valueOf(s.charAt(i)).equals(".")) {
                return token.toString();
            }
            //check if valid operator
            if (operatorData.containsKey(token.toString())) {
                return token.toString();
            }
            //check if valid symbol
            if (symbolData.containsKey(token.toString())) {
                return token.toString();
            }
            //check if valid function
            if (functionData.containsKey(token.toString())) {
                return token.toString();
            }
            //check if parenthesis
            if (token.toString().equals("(") || token.toString().equals(")")) {
                return token.toString();
            }

            token.append(s.charAt(i));
            //trim spaces ' '
            token = new StringBuilder(token.toString().trim().replace(",",""));
        }

        return token.toString().trim();
    }

    private boolean validToken(String token) {
        boolean o = false;

        token = token.replace(" ","");

        if (numeric(token)) o = true;
        if (symbolData.containsKey(token)) o = true;
        if (operatorData.containsKey(token)) o = true;
        if (functionData.containsKey(token)) o = true;
        if (token.equals("(") || token.equals(")")) o = true;

        return o;
    }

    private boolean numeric(String s) {
        try {
            double o = Double.parseDouble(s);
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    private String classifyToken(String token) {
        if (numeric(token)) return "number";
        if (symbolData.containsKey(token)) return "symbol";
        if (operatorData.containsKey(token)) return "operator";
        if (functionData.containsKey(token)) return "function";
        if (token.equals("(")) return "(";
        if (token.equals(")")) return ")";
        return "ignore";
    }

    private int advanceIndex(int index, String currentToken, String s) {

        StringBuilder check = new StringBuilder();
        //debug
        //System.out.println("ADVANCE INPUT: " + index + "TOK: " + currentToken);

        //return index ++ if on last index
        if (index >= s.length() - 1) {
            return index + 1;
        }
        //ignore first ' '
        if (s.charAt(index) == ' ' && index < s.length() - 1) index++;


        while (!check.toString().trim().replace(",","").replace(" ","").equals(currentToken) && index <= s.length() - 1) {
            check.append(s.charAt(index));
            index++;
        }

        return index;
    }

    //converts '-5 + 7' -> '-1*5 + 7'
    private String checkNegation(String input) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            Character c = input.charAt(i);

            if (c.toString().equals("-")) {

                if (i == 0) {
                    output.append("(0-1)*");
                    continue;
                }

                StringBuilder token = new StringBuilder();

                for (int j = i - 1; j >= 0; j--) {

                    token.insert(0, input.charAt(j));
                    //trim spaces ' '
                    token = new StringBuilder(token.toString().trim());

                    //reached number
                    if (numeric(token.toString())) {
                        output.append(c);
                        break;
                    }
                    //check if valid operator
                    if (operatorData.containsKey(token.toString())) {
                        output.append("(0-1)*");
                        break;
                    }
                    //check if valid symbol
                    if (symbolData.containsKey(token.toString())) {
                        output.append(c);
                        break;
                    }
                    //check if valid function
                    if (functionData.containsKey(token.toString())) {
                        output.append("(0-1)*");
                        break;
                    }
                    //check if parenthesis "("
                    if (token.toString().equals("(")) {
                        output.append("(0-1)*");
                        break;
                    }

                    //check if parenthesis ")"
                    if (token.toString().equals(")")) {
                        output.append(c);
                        break;
                    }

                    if (j == 0) {
                        output.append("(0-1)*");
                        break;
                    }
                }
            } else {
                output.append(c);
            }
        }

        return output.toString();
    }
}