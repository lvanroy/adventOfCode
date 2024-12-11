import static java.lang.Integer.parseInt;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    String fileName = args[0];

    int result = 0;
    Pattern masPattern = Pattern.compile("MAS|SAM");
    List<String> lines = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
    }

    for (int row = 0; row < lines.size() - 2; row ++) {
        for (int col = 0; col < lines.getFirst().length() - 2; col ++) {
            // Generate the left to right downwards line of the X formation
            StringBuilder builder = new StringBuilder();
            builder.append(lines.get(row).charAt(col));
            builder.append(lines.get(row + 1).charAt(col + 1));
            builder.append(lines.get(row + 2).charAt(col + 2));

            // Check if we have a match here
            Matcher masMatcher = masPattern.matcher(builder);
            if (!masMatcher.find()) {
                continue;
            }

            // We have one match, now let's generate the other diagonal
            builder = new StringBuilder();
            builder.append(lines.get(row).charAt(col + 2));
            builder.append(lines.get(row + 1).charAt(col + 1));
            builder.append(lines.get(row + 2).charAt(col));

            // Check if we have a match here
            masMatcher = masPattern.matcher(builder);
            if (masMatcher.find()) {
                result ++;
            }
        }
    }

    System.out.println(result);
}