package okar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okar.entity.Individual;
import okar.entity.Pair;

@Getter
@AllArgsConstructor
public class RouletteSector {

    private double start;
    private double end;

    private Pair<Individual, Double> individualWithScore;

    public boolean matchesIndex(double index) {
        return start <= index && end > index;
    }
}
