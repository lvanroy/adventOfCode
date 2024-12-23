void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    // Track the current position
    Position position = null;

    // Track the encountered positions
    Map<Integer, Set<Integer>> encountered = new HashMap<>();

    // Track the specification of the environment
    List<String> grid = new ArrayList<>();

    // Track the direction by storing the delta for each step
    Direction dir = new Direction();

    // Track the number of steps before going out of bounds
    int result = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("^")) {
                position = new Position(grid.size(), line.indexOf("^"));
                encountered.put(position.row, new HashSet<>());
                encountered.get(position.row).add(position.col);
            }
            grid.add(line);
        }
    }

    while (true) {
        int newRow = position.row + dir.dr;
        int newCol = position.col + dir.dc;

        // Verify that we did not move out of bounds
        if (newRow >= grid.size() ||
                newRow < 0 ||
                newCol >= grid.getFirst().length() ||
                newCol < 0) {
            break;
        }

        char nextPosition = grid.get(newRow).charAt(newCol);
        if (nextPosition == '#') {
            // We hit a wall, move 90 degrees right
            turnRight(dir);
        } else {
            // This position is empty, move to the given position
            position.move(newRow, newCol);

            // Given that we moved, store the result
            encountered.putIfAbsent(position.row, new HashSet<>());
            encountered.get(position.row).add(position.col);
        }
    }

    // We exited the map, compute the total number of unique positions
    for (Set<Integer> positionsInRow: encountered.values()) {
        result += positionsInRow.size();
    }
    System.out.println(result);
}

void turnRight(Direction direction) {
    if (direction.dr == -1) {
        direction.dr = 0;
        direction.dc = 1;
    } else if (direction.dr == 1) {
        direction.dr = 0;
        direction.dc = -1;
    } else if (direction.dc == 1) {
        direction.dr = 1;
        direction.dc = 0;
    } else {
        direction.dr = -1;
        direction.dc = 0;
    }
}

static class Position {
    public int row;
    public int col;

    public Position(int row, int col) {
        move(row, col);
    }

    public void move(int nrow, int ncol) {
        row = nrow;
        col = ncol;
    }
}

static class Direction {
    public int dr = -1;
    public int dc = 0;
}