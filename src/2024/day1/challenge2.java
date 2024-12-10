import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    String fileName = args[0];
    String[] firstColumn = new String[1000];
    String[] secondColumn = new String[1000];
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            String[] ids = stream(line.split(" ")).filter(e -> !e.trim().isEmpty()).toArray(String[]::new);
            firstColumn[i] = ids[0];
            secondColumn[i] = ids[1];
            i++;
        }
    }

    int similarity = 0;
    List<String> secondList = Arrays.asList(secondColumn);
    for (int i = 0; i < 1000; i++) {
        similarity += parseInt(firstColumn[i]) * Collections.frequency(secondList, firstColumn[i]);
    }
    System.out.println(similarity);
}