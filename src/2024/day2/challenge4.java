import static java.lang.Math.abs;
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
            List<Integer> ids = stream(line.split(" "))
                    .filter(e -> !e.trim().isEmpty())
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());
            // Track the initial size, this can be used to track if an array was modified at some point
            int size = ids.size();

            if (validateIncreasing(ids, size) || validateIncreasing(ids.reversed(), size)) {
                System.out.print("success: ");
                for (int i: ids) {
                    System.out.print(i + ", ");
                }
                System.out.println();
                safeEntries ++;
            } else {
                System.out.print("failed: ");
                for (int i: ids) {
                    System.out.print(i + ", ");
                }
                System.out.println();
            }
        }
    }
    System.out.println(safeEntries);
}

boolean validateIncreasing(List<Integer> ids, int originalLength) {
    Integer prev = ids.getFirst();
    for (int i = 1; i < ids.size(); i ++) {
        Integer next = ids.get(i);
        int dif = abs(prev - next);
        if (prev <= next || dif < 1 || dif > 3) {
            // We have a violation, check if we can still try to remove an element
            if (ids.size() != originalLength) {
                // If we get here we already tried to modify, this path will not lead to a safe configuration
                return false;
            }
            // We are still allowed to modify the configuration

            // Do a test without the first element
            List<Integer> copy = new ArrayList<>(ids);
            copy.remove(i - 1);
            if (validateIncreasing(copy, originalLength)) {
                return true;
            }

            // Do a test without the second element
            copy = new ArrayList<>(ids);
            copy.remove(i);
            if (validateIncreasing(copy, originalLength)) {
                return true;
            }

            // Both tests failed, this means there is no way to make this a valid configuration
            return false;
        }
        prev = next;
    }
    // We found no violations, hence the test passed
    return true;
}