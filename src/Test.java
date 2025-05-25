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

