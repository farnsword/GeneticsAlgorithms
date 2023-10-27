package okar;

import okar.entity.Individual;
import okar.entity.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

public class Roulette {

    private final List<RouletteSector> sectors = new ArrayList<>();
    private final double totalRouletteLength;

//    public Roulette(Map<Integer, Double> generation) {
//        this(generation.entrySet()
//                .stream()
//                .map(e -> new Pair<>(e.getKey(), e.getValue()))
//                .collect(Collectors.toList()));
//    }

    public Roulette(List<Pair<Individual, Double>> generation) {

        totalRouletteLength = generation.stream()
                .filter(p -> !Double.isNaN(p.getRight()))
                .mapToDouble(Pair::getRight).sum();

        DoubleAdder currentValue = new DoubleAdder();

        generation.stream()
                .filter(p -> !Double.isNaN(p.getRight()))
                .forEach(individual -> {

                    double start = currentValue.doubleValue();
                    double end = start + individual.getRight();

                    RouletteSector rouletteSector = new RouletteSector(start, end, individual);

                    sectors.add(rouletteSector);
                    currentValue.add(individual.getRight());
                });
    }

    private String drawSections(double coefficient, List<RouletteSector> sectorsToHighlight) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");

        sectors.forEach(s -> {
            char selectionChar = sectorsToHighlight != null && sectorsToHighlight.contains(s) ? '#' : '.';
            double limit = s.getIndividualWithScore().getRight() * coefficient;
            if (limit < 1) {
                sb.append(selectionChar);
            } else {
                for (int i = 0; i < limit; i++) {
                    sb.append(selectionChar);
                }
            }
            sb.append("|");
        });
        return sb.toString();
    }

    public List<Pair<Individual, Double>> draw(int numberOfDraws) {

        double k = 130 / totalRouletteLength;
        String sectorsDrawn = drawSections(k, null);
        List<RouletteSector> selections = new ArrayList<>();

        List<Pair<Individual, Double>> selectedSectors = ThreadLocalRandom.current()
                .doubles(numberOfDraws, 0, totalRouletteLength)
                .mapToObj(d -> sectors.stream()
                        .filter(s -> s.matchesIndex(d))
                        .findFirst()
                        .get()) //  TODO: 22.10.2023 handle absence of the value
                .peek(selections::add)
                .map(RouletteSector::getIndividualWithScore)
                .collect(Collectors.toList());

        System.out.println(sectorsDrawn.replaceAll("\\|", "."));
        String selectionsDrawn = drawSections(k, selections);
        System.out.println(sectorsDrawn);
        System.out.println(selectionsDrawn);
        System.out.println(sectorsDrawn);

        return selectedSectors;
    }
}
