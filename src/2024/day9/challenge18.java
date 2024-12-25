import static java.lang.Integer.parseInt;
import static java.lang.String.format;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    // Store information on the grid
    String mapping = getData(args[0]);

    // Track the start/end indices for the different types of blocks
    List<BlockSequence> memoryBlocks = new ArrayList<>();
    List<BlockSequence> emptyBlocks = new ArrayList<>();

    // Convert the mapping to the actual memory content
    convertToBlocks(mapping, memoryBlocks, emptyBlocks);

    // Reorder the memory content, this will also strip '.' entries
    reorderMemory(memoryBlocks, emptyBlocks);

    System.out.println(computeChecksum(memoryBlocks));
}

String getData(String filename) throws IOException{
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        return reader.readLine();
    }
}

void convertToBlocks(String mapping, List<BlockSequence> memoryBlocks, List<BlockSequence> emptyBlocks) {
    boolean fileSpecification = true;
    int indexCounter = 0;

    for (int i = 0; i < mapping.length(); i ++) {
        // Get the next digit we need to process
        int digit = parseInt(String.valueOf(mapping.charAt(i)));

        if (fileSpecification) {
            // Register a file block specification
            memoryBlocks.add(new BlockSequence(indexCounter, indexCounter + digit - 1));

            // Update the env for the next block
            indexCounter += memoryBlocks.getLast().size();
            fileSpecification = false;
        } else {
            // Register an empty block specification
            if (digit != 0) {
                emptyBlocks.add(new BlockSequence(indexCounter, indexCounter + digit - 1));
                indexCounter += emptyBlocks.getLast().size();
            }

            // Update the env for the next block
            fileSpecification = true;
        }
    }
}

void reorderMemory(List<BlockSequence> memoryBlocks, List<BlockSequence> emptyBlocks) {
    for (BlockSequence memory: memoryBlocks.reversed()) {
        int size = memory.size();
        for (BlockSequence emptyBlock : emptyBlocks) {
            if (emptyBlock.start > memory.end) {
                continue;
            }
            if (size <= emptyBlock.size()) {
                memory.start = emptyBlock.start;
                memory.end = memory.start + size - 1;
                emptyBlock.start = emptyBlock.start + size;
                break;
            }
        }
    }
}

long computeChecksum(List<BlockSequence> blocks) {
    long result = 0;
    for (int i = 0; i < blocks.size(); i++) {
        for (int index = blocks.get(i).start; index <= blocks.get(i).end; index ++) {
            result += (long) index * i;
        }
    }
    return result;
}

static class BlockSequence {
    int start;
    int end;

    public BlockSequence(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int size() {
        return end - start + 1;
    }

    @Override
    public String toString() {
        return format("%s to %s", start, end);
    }
}