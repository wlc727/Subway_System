import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;
// 测试类
public class Test {
    public static void main(String[] args) {
        SubwaySystem subwaySystem = new SubwaySystem();
        subwaySystem.loadFromFile("D:/code/SubwaySystem/out/subway.txt");

        // 1. 识别所有中转站
        Set<Map.Entry<Station, List<String>>> transferStations = subwaySystem.getTransferStations();
        System.out.println("所有中转站：");
        for (Map.Entry<Station, List<String>> entry : transferStations) {
            System.out.println("<" + entry.getKey().getName() + "，<" + String.join("、", entry.getValue()) + ">>");
        }

        // 2. 输入某一站点，输出线路距离小于 n 的所有站点集合
        String stationName = "华中科技大学";
        double n = 5;
        List<Map<String, Object>> stationsWithinDistance = subwaySystem.getStationsWithinDistance(stationName, n);
        System.out.println("\n距离 " + stationName + " 小于 " + n + " 的所有站点：");
        for (Map<String, Object> stationInfo : stationsWithinDistance) {
            System.out.println("<<" + stationInfo.get("station") + "，" + stationInfo.get("line") + "，" + stationInfo.get("distance") + ">>");
        }

        // 3. 输入起点站和终点站的名称，返回所有路径的集合，并给路径编号
        String startName = "华中科技大学";
        String endName = "汉口火车站";
        List<List<Station>> allPaths = subwaySystem.getAllPaths(startName, endName);
        List<Station> shortestPath = subwaySystem.getShortestPath(startName, endName);
        int shortestPathIndex = -1;
        System.out.println("\n从 " + startName + " 到 " + endName + " 的所有路径：");
        for (int i = 0; i < allPaths.size(); i++) {
            List<Station> path = allPaths.get(i);
            if (path.equals(shortestPath)) {
                shortestPathIndex = i + 1;
            }
            System.out.printf("路径编号 %d: %s%n", i + 1, path);
        }
        if (shortestPathIndex != -1) {
            System.out.printf("最短路径编号为: %d%n", shortestPathIndex);
        }

        // 4. 给定起点站和终点站的名称，返回最短路径
        System.out.println("\n从 " + startName + " 到 " + endName + " 的最短路径：");
        System.out.println(shortestPath);

        // 5.打印最短路径
        System.out.println("\n最短路径乘车信息：");
        subwaySystem.printShortestPath(startName, endName);

        // 6.计算指定路径的乘车费用（普通单程票）
        if (!allPaths.isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n请输入要选择的路径编号 (1 - " + allPaths.size() + ")：");
            int pathIndex = scanner.nextInt() - 1;
            if (pathIndex >= 0 && pathIndex < allPaths.size()) {
                List<Station> selectedPath = allPaths.get(pathIndex);
                double normalFare = subwaySystem.calculateFareForPath(selectedPath);
                System.out.println("该路径的普通单程票费用：" + normalFare);
            } else {
                System.out.println("输入的编号无效");
            }
        }

        // 7.计算不同支付方式的票价
        if (!allPaths.isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n请再次输入要选择的路径编号 (1 - " + allPaths.size() + ")：");
            int pathIndex = scanner.nextInt() - 1;
            if (pathIndex >= 0 && pathIndex < allPaths.size()) {
                List<Station> selectedPath = allPaths.get(pathIndex);
                Map<String, Double> fares = subwaySystem.calculateFaresForPath(selectedPath);
                System.out.println("不同支付方式的票价：");
                for (Map.Entry<String, Double> entry : fares.entrySet()) {
                    System.out.println(entry.getKey() + "：" + entry.getValue());
                }
            } else {
                System.out.println("输入的编号无效");
            }
        }
    }
}