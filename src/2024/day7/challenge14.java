import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    // Store the equations we need to solve
    List<Equation> equations = new ArrayList<>();

    // Capture the result
    long result = 0;

    getData(args[0], equations);

    for (Equation equation: equations) {
        if (solveable(equation)) {
            result += equation.result;
        }
    }

    System.out.println(result);
}

void getData(String filename, List<Equation> equations) throws IOException{
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            long result = parseLong(line.split(":")[0]);
            List<Integer> arguments = new ArrayList<>();
            for (String argument: line.split(":")[1].split(" ")) {
                if (argument.isEmpty()) {
                    continue;
                }
                arguments.add(parseInt(argument));
            }
            equations.add(new Equation(result, arguments));
        }
    }
}

boolean solveable(Equation equation) {
    // Generate permutations
    int desiredLength = equation.arguments.size() - 1;
    List<String> permutations = new ArrayList<>();
    generatePermutations(desiredLength, "", permutations);

    // Evaluate each permutation
    for (String permutation: permutations) {
        long currentResult = equation.arguments.getFirst();
        for (int i = 0; i < permutation.length(); i ++) {
            char operation = permutation.charAt(i);
            if (operation == '+') {
                currentResult += equation.arguments.get(i + 1);
            } else if (operation == '*') {
                currentResult *= equation.arguments.get(i + 1);
            } else if (operation == '|') {
                currentResult = Long.parseLong(Long.toString(currentResult) + equation.arguments.get(i + 1));
            }
        }
        if (currentResult == equation.result) {
            return true;
        }
    }
    return false;
}

void generatePermutations(int desiredLength, String cur, List<String> results) {
    if (cur.length() == desiredLength) {
        results.add(cur);
        return;
    }
    for (String option: List.of("+", "*", "|")) {
        generatePermutations(desiredLength, cur + option, results);
    }
}

static class Equation {
    public long result;
    public List<Integer> arguments;

    public Equation(long result, List<Integer> arguments) {
        this.result = result;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(format("%s = ", result));
        for (int argument: arguments) {
            sb.append(format("%s _ ", argument));
        }
        return sb.toString();
    }
}