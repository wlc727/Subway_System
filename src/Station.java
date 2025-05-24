import java.util.ArrayList;
import java.util.List;

// 站点类
class Station {
    private String name;
    private List<String> lines;

    public Station(String name) {
        this.name = name;
        this.lines = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getLines() {
        return lines;
    }

    public void addLine(String line) {
        if (!lines.contains(line)) {
            lines.add(line);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
