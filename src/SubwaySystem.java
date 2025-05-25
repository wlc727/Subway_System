import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// 地铁系统类
class SubwaySystem {
    private Map<Station, Map<Station, Double>> graph;
    private Map<String, Station> stationMap;

    public SubwaySystem() {
        this.graph = new HashMap<>();
        this.stationMap = new HashMap<>();
    }

    // 从文件中读取地铁线路信息
    public void loadFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 去除行首尾的空白字符
                line = line.trim();
                // 跳过空行
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                // 检查行是否包含足够的字段
                if (parts.length < 4) {
                    System.err.println("无效的行格式: " + line);
                    continue;
                }
                String lineName = parts[0];
                String station1Name = parts[1];
                String station2Name = parts[2];
                double distance = Double.parseDouble(parts[3]);

                Station station1 = getOrCreateStation(station1Name);
                Station station2 = getOrCreateStation(station2Name);

                station1.addLine(lineName);
                station2.addLine(lineName);

                addEdge(station1, station2, distance);
                addEdge(station2, station1, distance);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("文件中距离格式无效: " + e.getMessage());
        }
    }


    private Station getOrCreateStation(String name) {
        if (!stationMap.containsKey(name)) {
            Station station = new Station(name);
            stationMap.put(name, station);
            graph.put(station, new HashMap<>());
        }
        return stationMap.get(name);
    }

    private void addEdge(Station from, Station to, double distance) {
        graph.get(from).put(to, distance);
    }

    // 识别所有中转站
    public Set<Map.Entry<Station, List<String>>> getTransferStations() {
        Map<Station, List<String>> transferStations = new HashMap<>();
        for (Map.Entry<String, Station> entry : stationMap.entrySet()) {
            Station station = entry.getValue();
            if (station.getLines().size() >= 2) {
                transferStations.put(station, station.getLines());
            }
        }
        return transferStations.entrySet();
    }

    // 输入某一站点，输出线路距离小于 n 的所有站点集合
    public List<Map<String, Object>> getStationsWithinDistance(String stationName, double n) {
        Station start = stationMap.get(stationName);
        if (start == null) {
            throw new IllegalArgumentException("站点名称不存在");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        Queue<Map<String, Object>> queue = new LinkedList<>();
        Set<Station> visited = new HashSet<>();

        queue.offer(Map.of("station", start, "distance", 0.0, "line", ""));
        visited.add(start);

        while (!queue.isEmpty()) {
            Map<String, Object> currentInfo = queue.poll();
            Station current = (Station) currentInfo.get("station");
            double currentDistance = (double) currentInfo.get("distance");
            String currentLine = (String) currentInfo.get("line");

            for (Map.Entry<Station, Double> neighborEntry : graph.get(current).entrySet()) {
                Station neighbor = neighborEntry.getKey();
                double edgeDistance = neighborEntry.getValue();
                double newDistance = currentDistance + edgeDistance;

                if (newDistance < n && !visited.contains(neighbor)) {
                    String line = getCommonLine(current, neighbor, currentLine);
                    result.add(Map.of("station", neighbor.getName(), "line", line, "distance", newDistance));
                    queue.offer(Map.of("station", neighbor, "distance", newDistance, "line", line));
                    visited.add(neighbor);
                }
            }
        }
        return result;
    }

    private String getCommonLine(Station station1, Station station2, String currentLine) {
        for (String line : station1.getLines()) {
            if (station2.getLines().contains(line)) {
                if (currentLine.isEmpty() || currentLine.equals(line)) {
                    return line;
                }
            }
        }
        return "";
    }

