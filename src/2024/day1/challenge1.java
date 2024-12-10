import static java.lang.Math.abs;
import static java.util.Arrays.sort;
import static java.util.Arrays.stream;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    String fileName = args[0];
    int[] firstColumn = new int[1000];
    int[] secondColumn = new int[1000];
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            int[] ids = stream(line.split(" "))
                    .filter(e -> !e.trim().isEmpty())
                    .mapToInt(Integer::parseInt)
                    .toArray();
            firstColumn[i] = ids[0];
            secondColumn[i] = ids[1];
            i++;
        }
    }

    sort(firstColumn);
    sort(secondColumn);
    int totalDistance = 0;
    for (int i = 0; i < 1000; i++) {
        totalDistance += abs(firstColumn[i] - secondColumn[i]);
    }
    System.out.println(totalDistance);
}