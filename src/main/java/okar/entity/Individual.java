package okar.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@AllArgsConstructor
public class Individual {

    private final int x;
    private final int y;
    private final int z;

    @Override
    public int hashCode() {
        return 100 * x + 10 * y + z;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Individual)) {
            throw new RuntimeException("Can't compare for equality, other is not an Individual");
        }

        Individual indOther = (Individual) obj;
        
        return this.x == indOther.x
                && this.y == indOther.y
                && this.z == indOther.z;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d, %d]", x, y, z);
    }

    public static Individual random() {
        return new Individual(
                ThreadLocalRandom.current().nextInt(64),
                ThreadLocalRandom.current().nextInt(64),
                ThreadLocalRandom.current().nextInt(64));
    }
}
