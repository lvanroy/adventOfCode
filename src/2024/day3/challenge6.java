import static java.lang.Integer.parseInt;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    String fileName = args[0];

    boolean isProcessing = true;
    int result = 0;
    Pattern operationPattern = Pattern.compile("mul\\(\\d+,\\d+\\)|do\\(\\)|don't\\(\\)");
    Pattern numberPattern = Pattern.compile("\\d+");

    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher operationMatcher = operationPattern.matcher(line);
            while (operationMatcher.find()) {
                String operation = operationMatcher.group();
                if (operation.contains("mul") && isProcessing) {
                    Matcher numberMatcher = numberPattern.matcher(operationMatcher.group());

                    // extract the first number
                    numberMatcher.find();
                    int first = parseInt(numberMatcher.group());

                    // Extract the second number
                    numberMatcher.find();
                    int second = parseInt(numberMatcher.group());

                    // Register the result
                    result += (first * second);
                } else if (operation.contains("don't")) {
                    isProcessing = false;
                } else if (operation.contains("do")) {
                    isProcessing = true;
                }

            }
        }
    }
    System.out.println(result);
}