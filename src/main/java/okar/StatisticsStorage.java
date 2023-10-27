package okar;

import lombok.Setter;
import okar.entity.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatisticsStorage {


    private final Map<Integer, Map<Integer, Map<Integer, DoubleSummaryStatistics>>> stats = new HashMap<>();
    private static StatisticsStorage instance;

    @Setter
    private double totalMaximum;

    public static StatisticsStorage getInstance() {
        if (Objects.isNull(instance)) {
            instance = new StatisticsStorage();
        }
        return instance;
    }

    public void addStatistics(int populationSize,
                              int numberOfCrossoverPoints,
                              Map<Integer, DoubleSummaryStatistics> statistics) {
        stats.computeIfAbsent(populationSize, p -> new HashMap<>()).put(numberOfCrossoverPoints, statistics);
    }


    public void store() {
        System.out.println();
        // write
        List<String> lines = new ArrayList<>();
        stats.forEach((popSize, results) -> {
            StringBuilder sb = new StringBuilder();
            lines.clear();
            for (int i = 0; i < results.values().iterator().next().size(); i++) {
                sb.append(i + 1).append('|')
                        .append(results.get(2).get(i).getMin() * 1000).append('|')
                        .append(results.get(3).get(i).getMin() * 1000).append('|')
                        .append(results.get(2).get(i).getMax() * 1000).append('|')
                        .append(results.get(3).get(i).getMax() * 1000).append('|')
                        .append(results.get(2).get(i).getAverage() * 1000).append('|')
                        .append(results.get(3).get(i).getAverage() * 1000).append('|')
                        .append(totalMaximum * 1000)
                        ;
                lines.add(sb.toString().replaceAll("\\.", ","));
                sb = new StringBuilder();
            }
            try {
                Files.write(Paths.get(".", popSize + "_results.csv"), lines);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });



    }
}
