void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    String fileName = args[0];

    List<String> first = new ArrayList<>();
    List<String> last = new ArrayList<>();
    List<List<String>> orders = new ArrayList<>();
    int result = 0;
    Pattern numberPattern = Pattern.compile("\\d+");

    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher numberMatcher = numberPattern.matcher(line);
            // Extract an order rule
            if (line.contains("|")) {
                // extract the first number
                numberMatcher.find();
                first.add(numberMatcher.group());

                // Extract the second number
                numberMatcher.find();
                last.add(numberMatcher.group());
            }

            // Extract a proposed order
            else if (line.contains(",")) {
                List<String> order = new ArrayList<>();
                while (numberMatcher.find()) {
                    order.add(numberMatcher.group());
                }
                orders.add(order);
            }
        }
    }

    for (List<String> order: orders) {
        if (orderingIsOk(first, last, order)) {
            result += Integer.parseInt(order.get((order.size() - 1) / 2));
        }
    }
    System.out.println(result);
}

boolean orderingIsOk(List<String> first, List<String> last, List<String> order) {
    // An order is "good" if the entries are in the same order in the ordered list
    for (int i = 0; i < order.size() - 1; i ++) {
        String cur = order.get(i);
        String next = order.get(i + 1);
        int[] indices = IntStream
                .range(0, first.size())
                .filter(index -> next.equals(first.get(index)))
                .toArray();
        for (int index: indices) {
            if (last.get(index).equals(cur)) {
                return false;
            }
        }

    }
    return true;
}