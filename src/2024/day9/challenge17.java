import static java.lang.Integer.parseInt;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    // Store information on the grid
    String mapping = getData(args[0]);

    // Convert the mapping to the actual memory content, this needs to be in string format because we still have '.' entries
    List<String> memory = convertToMemoryString(mapping);

    // Reorder the memory content, this will also strip '.' entries
    List<Integer> orderedMemory = reorderMemory(memory);

    System.out.println(computeChecksum(orderedMemory));
}

String getData(String filename) throws IOException{
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        return reader.readLine();
    }
}

List<String> convertToMemoryString(String mapping) {
    boolean fileSpecification = true;
    List<String> result = new ArrayList<>();
    int idCounter = 0;

    for (int i = 0; i < mapping.length(); i ++) {
        // Get the next digit we need to process
        int digit = parseInt(String.valueOf(mapping.charAt(i)));

        if (fileSpecification) {
            // Register a file block specification
            for (int j = 0; j < digit; j ++) {
                result.add(String.valueOf(idCounter));
            }

            // Update the env for the next block
            idCounter ++;
            fileSpecification = false;
        } else {
            // Register an empty block specification
            for (int j = 0; j < digit; j ++) {
                result.add(".");
            }

            // Update the env for the next block
            fileSpecification = true;
        }
    }

    return result;
}

List<Integer> reorderMemory(List<String> memory) {
    int begin = 0;
    int end = memory.size() - 1;
    List<Integer> result = new ArrayList<>();

    while (begin <= end) {
        if (!memory.get(begin).equals(".")) {
            // We have a number at the start of our left to right reader
            result.add(Integer.valueOf(memory.get(begin)));
            begin ++;
            continue;
        }

        // At this point we know our begin pointer is pointing to an empty memory slot, let's check if we have a digit
        //    at the end that we can move to the front
        if (!memory.get(end).equals(".")) {
            // We have a number at our right to left reader
            result.add(Integer.valueOf(memory.get(end)));

            // Update the begin pointer so that it points to the next unread entry
            begin ++;
        }
        // No matter what, move our end reader one space to the left
        end --;
    }

    return result;
}

long computeChecksum(List<Integer> memory) {
    long result = 0;
    for (int i = 0; i < memory.size(); i ++) {
        result += (long) i * memory.get(i);
    }
    return result;
}