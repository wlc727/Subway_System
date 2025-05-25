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

