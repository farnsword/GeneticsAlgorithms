package okar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CrossoverGenieTest {

    @Test
    void mutateTest() {
        String individual = "00000000";
        String mutated = CrossoverGenie.mutate(individual);
        long changedBits = mutated.chars().filter(c -> c == '1').count();
        assertEquals(1, changedBits, "Number of changed bits is incorrect");
    }
}