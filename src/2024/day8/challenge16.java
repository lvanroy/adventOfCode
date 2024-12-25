import static java.lang.Math.abs;
import static java.lang.String.format;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    // Store the antenna's on a by frequency basis
    Map<Character, List<Antenna>> antennas = new HashMap<>();

    // Store information on the grid
    GridInfo gridInfo = new GridInfo();

    getData(args[0], antennas, gridInfo);

    System.out.println(computeAntiNodes(antennas, gridInfo));
}

void getData(String filename, Map<Character, List<Antenna>> antennas, GridInfo gridInfo) throws IOException{
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        int row = 0;
        while ((line = reader.readLine()) != null) {
            for (int col = 0; col < line.length(); col ++) {
                char c = line.charAt(col);
                if (c == '.') {
                    continue;
                }
                antennas.putIfAbsent(c, new ArrayList<>());
                antennas.get(c).add(new Antenna(row, col));
            }
            row += 1;
            gridInfo.cols = line.length();
        }
        gridInfo.rows = row;
    }
}

int computeAntiNodes(Map<Character, List<Antenna>> antennasPerFrequency, GridInfo gridInfo) {
    // Track the number of interference points
    Set<Antenna> interferencePoints = new HashSet<>();

    // Iterate over each antenna set separately
    for (List<Antenna> antennas: antennasPerFrequency.values()) {
        interferencePoints.addAll(computeAntiNodesForFrequency(antennas, gridInfo));
    }

    return interferencePoints.size();
}

Set<Antenna> computeAntiNodesForFrequency(List<Antenna> antennas, GridInfo gridInfo) {
    // Track the number of interference points
    Set<Antenna> interferencePoints = new HashSet<>();

    // Iterate over all pairs, it is no problem that we will compare the same, they will just not generate results
    for (Antenna antenna1: antennas) {
        for (Antenna antenna2: antennas) {
            if (antenna1 == antenna2) {
                continue;
            }

            int dr = antenna1.row - antenna2.row;
            int dc = antenna1.col - antenna2.col;

            int ir = antenna1.row;
            int ic = antenna1.col;

            while (true) {
                ir = ir + dr;
                ic = ic + dc;

                if (ir >= 0 && ir < gridInfo.rows && ic >= 0 && ic < gridInfo.cols) {
                    interferencePoints.add(new Antenna(ir, ic));
                } else {
                    break;
                }
            }

            ir = antenna2.row;
            ic = antenna2.col;

            while (true) {
                ir = ir + dr;
                ic = ic + dc;

                if (ir >= 0 && ir < gridInfo.rows && ic >= 0 && ic < gridInfo.cols) {
                    interferencePoints.add(new Antenna(ir, ic));
                } else {
                    break;
                }
            }
        }
    }

    return interferencePoints;
}

static class Antenna {
    public int row;
    public int col;

    public Antenna(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return format("%s, %s", row, col);
    }

    @Override
    public boolean equals(Object o) {
        Antenna pos = (Antenna) o;
        return row == pos.row && col == pos.col;
    }

    @Override
    public int hashCode() {
        return row + col;
    }
}

static class GridInfo {
    public int rows;
    public int cols;
}