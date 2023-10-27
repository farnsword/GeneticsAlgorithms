package okar;

import okar.entity.Individual;
import okar.entity.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CrossoverGenie {


//    public static void main(String[] args) {
//        String a = "aaaaaaaaaaaaa";
//        String b = "bbbbbbbbbbbbb";
//
//        Pair<String, String> crossover = new CrossoverGenie().crossover(a, b, 3);
//
//
//        System.out.println();
//    }

    public static List<Individual> crossoverGeneration(List<Individual> generation, int numberOfCrossoverPoints) {
        List<Individual> half_1 = new ArrayList<>(generation);
        List<Individual> tempHalf = half_1.subList(generation.size() / 2, generation.size());
        List<Individual> half_2 = new ArrayList<>(tempHalf);

        tempHalf.clear();

        int mutationProbability = calculateMutationProbability(generation);

        List<Individual> newGeneration = new ArrayList<>();

        for (int i = 0; i < generation.size() / 2; i++) {
            newGeneration.addAll(crossover(half_1.get(i), half_2.get(i), numberOfCrossoverPoints, mutationProbability));
        }

        return newGeneration;
    }

    private static int calculateMutationProbability(List<Individual> generation) {
        HashSet<Individual> uniqueIndividuals = new HashSet<>(generation);

        return (int) ((((double) (generation.size() - uniqueIndividuals.size())) / generation.size()) * 100);
    }

    public static List<Individual> crossover(Individual individualA,
                                             Individual individualB,
                                             int numberOfCrossoverPoints,
                                             int mutationProbability) {
        Pair<Integer, Integer> xs = crossover(
                individualA.getX(), individualB.getX(), numberOfCrossoverPoints, mutationProbability);
        Pair<Integer, Integer> ys = crossover(
                individualA.getY(), individualB.getY(), numberOfCrossoverPoints, mutationProbability);
        Pair<Integer, Integer> zs = crossover(
                individualA.getZ(), individualB.getZ(), numberOfCrossoverPoints, mutationProbability);

        return List.of(
                new Individual(xs.getLeft(), ys.getLeft(), zs.getLeft()),
                new Individual(xs.getRight(), ys.getRight(), zs.getRight()));
    }

    private static Pair<Integer, Integer> crossover(int individualA,
                                                    int individualB,
                                                    int numberOfTouchPoints,
                                                    int mutationProbability) {
        Pair<String, String> crossover = crossover(toBinaryString(individualA),
                toBinaryString(individualB),
                numberOfTouchPoints,
                mutationProbability);

        return new Pair<>(Integer.parseInt(crossover.getLeft(), 2), Integer.parseInt(crossover.getRight(), 2));
    }

    private static Pair<String, String> crossover(String individualA,
                                                  String individualB,
                                                  int numberOfTouchPoints,
                                                  int mutationProbability) {
        if (individualA.length() != individualB.length()) {
            throw new RuntimeException("Individuals are not of the same size, can't crossover");
        }

        List<Integer> touchPointIndexes = ThreadLocalRandom.current()
                //  TODO: 22.10.2023 excluding first and last
                .ints(numberOfTouchPoints, 1, individualA.length() - 1)
                .boxed()
                .sorted()
                .collect(Collectors.toList());


        List<String> subsA = splitStringByIndexes(individualA, touchPointIndexes);
        List<String> subsB = splitStringByIndexes(individualB, touchPointIndexes);

        StringBuilder sbA = new StringBuilder();
        StringBuilder sbB = new StringBuilder();

        for (int i = 0; i < numberOfTouchPoints + 1; i++) {
            if (i % 2 == 0) {
                sbA.append(subsA.get(i));
                sbB.append(subsB.get(i));
            } else {
                sbA.append(subsB.get(i));
                sbB.append(subsA.get(i));
            }
        }

        String newA = sbA.toString();
        String newB = sbB.toString();

        if (newA.equals(newB) && ThreadLocalRandom.current().nextInt(100) <= mutationProbability) {
            newA = mutate(newA);
        }

        return new Pair<>(newA, newB);
    }

    public static String mutate(String individual) {
        int index = ThreadLocalRandom.current().nextInt(individual.length());
        char mutation = individual.charAt(index) == '1' ? '0' : '1';
        StringBuilder sb = new StringBuilder(individual);
        sb.setCharAt(index, mutation);
        return sb.toString();
    }

    private static List<String> splitStringByIndexes(String individual, List<Integer> touchPointIndexes) {
        List<String> subs = new ArrayList<>();
        AtomicInteger lastIndex = new AtomicInteger(0);

        for (int i = 0; i < touchPointIndexes.size() + 1; i++) {
            int endIndex = i == touchPointIndexes.size() ? individual.length() : touchPointIndexes.get(i);
            String substring = individual.substring(lastIndex.get(), endIndex);
            subs.add(substring);
            lastIndex.set(endIndex);
        }

        return subs;
    }

    private static String toBinaryString(int number) {
        if (number > 63) {
            throw new RuntimeException("Only number belo 64 are supported at this time");
        }

        String binaryString = Integer.toBinaryString(number);
        if (binaryString.length() < 6) {
            return "0".repeat(6 - binaryString.length()) + binaryString;
        }
        return binaryString;
    }
}
