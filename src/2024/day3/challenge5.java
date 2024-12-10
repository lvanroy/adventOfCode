import static java.lang.Integer.parseInt;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    String fileName = args[0];

    int result = 0;
    Pattern mulPattern = Pattern.compile("mul\\(\\d+,\\d+\\)");
    Pattern numberPattern = Pattern.compile("\\d+");
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher mulMatcher = mulPattern.matcher(line);
            while (mulMatcher.find()) {
                Matcher numberMatcher = numberPattern.matcher(mulMatcher.group());

                // extract the first number
                numberMatcher.find();
                int first = parseInt(numberMatcher.group());

                // Extract the second number
                numberMatcher.find();
                int second = parseInt(numberMatcher.group());

                // Register the result
                result += (first * second);
            }
        }
    }
    System.out.println(result);
}