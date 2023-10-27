package okar;


import okar.entity.Individual;
import okar.entity.Pair;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * f(x,y,z) = xyz/(2+x)(x+y)(y+z)(z+128)
 * TODO: WHEN x == y == 0 OR y == z == 0 FITNESS FUNCTION RESULT IS NAN (0/0)
 * f(x,y,z) -> max
 * Absolute max -> 0.0023262810559006213
 * 0 <= x, y, z <= 63
 * Initial population - random
 * 5 series of 10 experiments (increase initial population in each series)
 */
public class GeneticsLab {

    private Map<Integer, DoubleSummaryStatistics> singleRunStatistics = new HashMap<>();

    public static void main(String[] args) {
        GeneticsLab geneticsLab = new GeneticsLab();
        double totalMax = geneticsLab.calculateTotalMaxForReference();

        geneticsLab.run(20);
        geneticsLab.run(50);
        geneticsLab.run(100);
        geneticsLab.run(200);
        geneticsLab.run(500);

        StatisticsStorage.getInstance().setTotalMaximum(totalMax);
        StatisticsStorage.getInstance().store();
    }

    private double calculateTotalMaxForReference() {

        double best = 0;

        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                for (int k = 0; k < 64; k++) {

                    double fitnessFunctionValue = calculateFitnessFunction(i, j, k);
                    if (fitnessFunctionValue > best) {
                        best = fitnessFunctionValue;
                    }
                }
            }
        }

        return best;
        // 0.0023262810559006213
    }

    private void run(int populationSize) {
        List<Pair<Individual, Double>> populationScored = generateInitialPopulation(populationSize);

        run(populationScored, 2);
        run(populationScored, 3);
    }

    private void run(List<Pair<Individual, Double>> populationScored, int numberOfCrossoverPoints) {
        int populationSize = populationScored.size();

        singleRunStatistics = new HashMap<>();

        for (int i = 0; i < 100; i++) {

            System.out.println("Iteration " + i);

            populationScored = new Roulette(populationScored).draw(populationSize);

            saveStats(populationScored, i);

            List<Individual> currentGeneration = populationScored.stream()
                    .map(Pair::getLeft)
                    .collect(Collectors.toList());

            List<Individual> newGeneration = CrossoverGenie.crossoverGeneration(
                    currentGeneration, numberOfCrossoverPoints);

            populationScored = scoreGeneration(newGeneration);
        }

        StatisticsStorage.getInstance().addStatistics(populationSize, numberOfCrossoverPoints, singleRunStatistics);
    }


    private void saveStats(List<Pair<Individual, Double>> draw, int iteration) {
        DoubleSummaryStatistics drawStats = draw.stream()
                .mapToDouble(Pair::getRight)
                .summaryStatistics();
        System.out.println("                MIN - " + drawStats.getMin());
        System.out.println("MAX - " + drawStats.getMax());

        singleRunStatistics.put(iteration, drawStats);
    }

    private List<Pair<Individual, Double>> generateInitialPopulation(int populationSize) {
        if (populationSize <= 0) {
            throw new RuntimeException("Population could not be negative or empty!");
        }

        Map<Individual, Double> generation = new HashMap<>();

        while (generation.size() != populationSize) {
            Individual ind = Individual.random();

            while (generation.containsKey(ind)) {
                ind = Individual.random();
            }

            generation.put(ind, calculateFitnessFunction(ind));
        }

        return generation.entrySet().stream()
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<Pair<Individual, Double>> scoreGeneration(List<Individual> generation) {
        return generation.stream()
                .map(i -> new Pair<>(i, calculateFitnessFunction(i)))
                .collect(Collectors.toList());
    }

    private double calculateFitnessFunction(Individual individual) {
        return calculateFitnessFunction(individual.getX(), individual.getY(), individual.getZ());
    }

    private double calculateFitnessFunction(int x, int y, int z) {
        return ((double)(x * y * z))/((2 + x) * (x + y) * (y + z) * (z + 128));
    }

}
