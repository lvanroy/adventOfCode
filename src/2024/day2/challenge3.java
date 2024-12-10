import static java.util.Arrays.sort;
import static java.util.Arrays.stream;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    String fileName = args[0];
    int safeEntries = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
        while ((line = reader.readLine()) != null) {
            int[] ids = stream(line.split(" "))
                    .filter(e -> !e.trim().isEmpty())
                    .mapToInt(Integer::parseInt)
                    .toArray();
            int[] original = Arrays.copyOf(ids, ids.length);

            // Check the abs diff between all subsequent entries
            if (diffAdjacentNotInLimits(original)) {
                continue;
            }

            // Check if all numbers are in ascending order
            sort(ids);
            if (Arrays.equals(original, ids)) {
                safeEntries ++;
                continue;
            }

            // Check if all numbers are in descending order
            reverseOrder(ids);
            if (Arrays.equals(original, ids)) {
                safeEntries ++;
            }
        }
    }
    System.out.println(safeEntries);
}

boolean diffAdjacentNotInLimits(int[] ids) {
    int dif;
    for (int i = 1; i < ids.length; i ++) {
        dif = Math.abs(ids[i-1] - ids[i]);
        if (dif < 1 || dif > 3) {
            return false;
        }
    }
    return true;
}

void reverseOrder(int[] ids) {
    for( int i = 0; i < ids.length/2; i++ ) {
        int temp = ids[i];
        ids[i] = ids[ids.length - i - 1];
        ids[ids.length - i - 1] = temp;
    }
}