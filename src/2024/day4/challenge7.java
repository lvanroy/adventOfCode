import static java.lang.Integer.parseInt;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    String fileName = args[0];

    int result = 0;
    Pattern xmasPattern = Pattern.compile("XMAS|SAMX");
    List<String> lines = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
    }

    for (String line: lines) {
        result += countAllMatches(xmasPattern, line);
    }

    for (int col = 0; col < lines.getFirst().length(); col ++) {
        // Generate the column
        StringBuilder builder = new StringBuilder();
        for (String line: lines) {
            builder.append(line.charAt(col));
        }

        // Search top down and bottom up
        result += countAllMatches(xmasPattern, builder.toString());
    }

    for (int i = 0; i < lines.getFirst().length() - 3; i ++) {
        // Generate the downwards left to right diagonal starting from top row
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < lines.getFirst().length() - i; j ++) {
            if (j < lines.size()) {
                builder.append(lines.get(j).charAt(i + j));
            }
        }
        // Search downwards diagonal starting on top line
        result += countAllMatches(xmasPattern, builder.toString());
    }

    for (int i = 1; i < lines.size() - 3; i ++) {
        // Generate the downwards left to right diagonal starting from left col
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < lines.size() - i; j ++) {
            if (lines.getFirst().length() > j) {
                builder.append(lines.get(i + j).charAt(j));
            }
        }
        // Search downwards diagonal starting on left column
        result += countAllMatches(xmasPattern, builder.toString());
    }

    for (int i = 0; i < lines.getFirst().length() - 3; i ++) {
        // Generate the upwards left to right diagonal starting from the bottom row
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < lines.getFirst().length() - i; j ++) {
            if (j < lines.size()) {
                builder.append(lines.get(lines.size() - 1 - j).charAt(i + j));
            }
        }

        // Search upwards diagonally starting on bottom line
        result += countAllMatches(xmasPattern, builder.toString());
    }

    for (int i = 3; i < lines.size() - 1; i ++) {
        // Generate the upwards left to right diagonal starting from the left col
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j <= i; j ++) {
            builder.append(lines.get(i - j).charAt(j));
        }
        // Search downwards diagonally starting on left column
        result += countAllMatches(xmasPattern, builder.toString());
    }

    System.out.println(result);
}

int countAllMatches(Pattern pattern, String input) {
    int count = 0;

    // Search in left to right order
    Matcher xmasMatcher = pattern.matcher(input);
    if (xmasMatcher.find()) {
        count ++;
        while (xmasMatcher.find(xmasMatcher.start() + 1)) {
            count ++;
        }
    }

    return count;
}