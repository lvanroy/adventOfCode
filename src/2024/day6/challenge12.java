void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    // Track the specification of the environment
    List<String> grid = new ArrayList<>();

    // Track the direction by storing the delta for each step
    Direction startDirection = new Direction();

    // Track the number of steps before going out of bounds
    int result = 0;

    // Track the current position + grid configuration
    Position startPosition = getData(args[0], grid);

    // Explore the grid and retrieve all potential obstacle locations
    Set<Position> obstacles = explore(new Position(startPosition), new Direction(startDirection), grid);

    for (Position obstacle: obstacles) {
        if (testForLoop(new Position(startPosition), new Direction(startDirection), grid, obstacle)) {
            result += 1;
        }
    }

    System.out.println(result);
}

Position getData(String filename, List<String> grid) throws IOException{
    Position startPosition = null;
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("^")) {
                startPosition = new Position(grid.size(), line.indexOf("^"));
            }
            grid.add(line);
        }
    }
    return startPosition;
}

Set<Position> explore(Position startPosition, Direction dir, List<String> grid) {
    // Track the possible obstacle locations
    Set<Position> obstacles = new HashSet<>();

    // Track the current position
    Position position = new Position(startPosition);

    while (true) {
        int newRow = position.row + dir.dr;
        int newCol = position.col + dir.dc;

        // Verify that we did not move out of bounds
        if (newRow >= grid.size() || newRow < 0 ||
                newCol >= grid.getFirst().length() || newCol < 0) {
            break;
        }

        char nextPosition = grid.get(newRow).charAt(newCol);
        if (nextPosition == '#') {
            // We hit a wall, move 90 degrees right
            dir.turnRight();
        } else {
            // This position is empty, move to the given position
            position.move(newRow, newCol);
            if (position.row != startPosition.row || position.col != startPosition.col) {
                obstacles.add(new Position(position));
            }
        }
    }

    return obstacles;
}

boolean testForLoop(Position position, Direction dir, List<String> grid, Position obstacle) {
    // Track the encountered positions
    Map<Integer, Map<Integer, String>> encountered = new HashMap<>();
    encountered.put(position.row, new HashMap<>());
    encountered.get(position.row).put(position.col, dir.getCode());

    while (true) {
        int newRow = position.row + dir.dr;
        int newCol = position.col + dir.dc;

        // Verify that we did not move out of bounds
        if (newRow >= grid.size() || newRow < 0 ||
                newCol >= grid.getFirst().length() || newCol < 0) {
            break;
        }

        // Verify that we did not encounter a loop yet
        if (encountered.containsKey(newRow) &&
                encountered.get(newRow).containsKey(newCol) &&
                encountered.get(newRow).get(newCol).equals(dir.getCode())) {
            return true;
        }

        char nextPosition = grid.get(newRow).charAt(newCol);
        if (nextPosition == '#' || (newRow == obstacle.row && newCol == obstacle.col)) {
            // We hit a wall, move 90 degrees right
            dir.turnRight();
        } else {
            // This position is empty, move to the given position
            position.move(newRow, newCol);

            // Given that we moved, store the result
            encountered.putIfAbsent(position.row, new HashMap<>());
            encountered.get(position.row).put(position.col, dir.getCode());
        }
    }

    return false;
}

static class Position {
    public int row;
    public int col;

    public Position(int row, int col) {
        move(row, col);
    }

    public Position(Position pos) {
        row = pos.row;
        col = pos.col;
    }

    public void move(int nrow, int ncol) {
        row = nrow;
        col = ncol;
    }

    @Override
    public boolean equals(Object o) {
        Position pos = (Position) o;
        return row == pos.row && col == pos.col;
    }

    @Override
    public int hashCode() {
        return row + col;
    }
}

static class Direction {
    public int dr = -1;
    public int dc = 0;

    public Direction() {}

    public Direction(Direction dir) {
        dr = dir.dr;
        dc = dir.dc;
    }

    public String getCode() {
        if (dr == -1) {
            return "UP";
        } else if (dr == 1) {
            return "DOWN";
        } else if (dc == 1) {
            return "RIGHT";
        } else {
            return "LEFT";
        }
    }

    public void turnRight() {
        if (dr == -1) {
            dr = 0;
            dc = 1;
        } else if (dr == 1) {
            dr = 0;
            dc = -1;
        } else if (dc == 1) {
            dr = 1;
            dc = 0;
        } else {
            dr = -1;
            dc = 0;
        }
    }
}