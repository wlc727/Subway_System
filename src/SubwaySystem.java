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

    // 输入起点站和终点站的名称，返回所有路径的集合
    public List<List<Station>> getAllPaths(String startName, String endName) {
        Station start = stationMap.get(startName);
        Station end = stationMap.get(endName);
        if (start == null || end == null) {
            throw new IllegalArgumentException("站点名称不存在");
        }

        List<List<Station>> paths = new ArrayList<>();
        List<Station> path = new ArrayList<>();
        path.add(start);
        dfs(start, end, path, paths);
        return paths;
    }

    private void dfs(Station current, Station end, List<Station> path, List<List<Station>> paths) {
        if (current.equals(end)) {
            paths.add(new ArrayList<>(path));
            return;
        }

        for (Station neighbor : graph.get(current).keySet()) {
            if (!path.contains(neighbor)) {
                path.add(neighbor);
                dfs(neighbor, end, path, paths);
                path.remove(path.size() - 1);
            }
        }
    }

    // 给定起点站和终点站的名称，返回最短路径
    public List<Station> getShortestPath(String startName, String endName) {
        Station start = stationMap.get(startName);
        Station end = stationMap.get(endName);
        if (start == null || end == null) {
            throw new IllegalArgumentException("站点名称不存在");
        }

        Map<Station, Double> distances = new HashMap<>();
        Map<Station, Station> previous = new HashMap<>();
        PriorityQueue<Station> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (Station station : graph.keySet()) {
            distances.put(station, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        queue.offer(start);

        while (!queue.isEmpty()) {
            Station current = queue.poll();
            if (current.equals(end)) {
                break;
            }

            for (Map.Entry<Station, Double> neighborEntry : graph.get(current).entrySet()) {
                Station neighbor = neighborEntry.getKey();
                double distance = neighborEntry.getValue();
                double newDistance = distances.get(current) + distance;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    queue.offer(neighbor);
                }
            }
        }

        return buildPath(previous, end);
    }

    private List<Station> buildPath(Map<Station, Station> previous, Station end) {
        List<Station> path = new ArrayList<>();
        for (Station at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public void printShortestPath(String startName, String endName) {
        List<Station> path = getShortestPath(startName, endName);
        if (path.isEmpty()) {
            System.out.println("未找到路径");
            return;
        }

        String currentLine = "";
        Station segmentStart = path.get(0);
        for (int i = 1; i < path.size(); i++) {
            Station current = path.get(i);
            Station prev = path.get(i - 1);
            String commonLine = getCommonLine(prev, current, currentLine);
            if (currentLine.isEmpty()) {
                currentLine = commonLine;
            } else if (!currentLine.equals(commonLine)) {
                System.out.printf("先坐 %s 从 %s 站到 %s 站，", currentLine, segmentStart.getName(), prev.getName());
                currentLine = commonLine;
                segmentStart = prev;
            }
        }
        System.out.printf("再坐 %s 从 %s 站到 %s 站%n", currentLine, segmentStart.getName(), path.get(path.size() - 1).getName());
    }

    // 6) 计算指定路径的乘车费用（普通单程票）
    public double calculateFareForPath(List<Station> path) {
        double totalDistance = 0;
        for (int i = 1; i < path.size(); i++) {
            Station prev = path.get(i - 1);
            Station current = path.get(i);
            totalDistance += graph.get(prev).get(current);
        }
        return calculateFare(totalDistance, "普通单程票");
    }

    // 7) 计算不同支付方式的票价
    public Map<String, Double> calculateFaresForPath(List<Station> path) {
        double totalDistance = 0;
        for (int i = 1; i < path.size(); i++) {
            Station prev = path.get(i - 1);
            Station current = path.get(i);
            totalDistance += graph.get(prev).get(current);
        }
        double normalFare = calculateFare(totalDistance, "普通单程票");
        double wuhanTongFare = calculateFare(totalDistance, "武汉通");
        double dayTicketFare = 0;

        Map<String, Double> fares = new HashMap<>();
        fares.put("普通单程票", normalFare);
        fares.put("武汉通", wuhanTongFare);
        fares.put("日票", dayTicketFare);
        return fares;
    }

    // 计算票价
    public double calculateFare(double distance, String paymentMethod) {
        double fare = 0;
        if (distance <= 4) {
            fare = 2;
        } else if (distance <= 12) {
            fare = 2 + Math.ceil((distance - 4) / 4);
        } else if (distance <= 24) {
            fare = 4 + Math.ceil((distance - 12) / 6);
        } else if (distance <= 40) {
            fare = 6 + Math.ceil((distance - 24) / 8);
        } else if (distance <= 50) {
            fare = 8 + Math.ceil((distance - 40) / 10);
        } else {
            fare = 9 + Math.ceil((distance - 50) / 20);
        }

        if ("武汉通".equals(paymentMethod)) {
            fare *= 0.9;
        }
        return fare;
    }
}