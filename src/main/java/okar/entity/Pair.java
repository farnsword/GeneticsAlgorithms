package okar.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Pair<T, U> {

    private T left;
    private U right;

    @Override
    public String toString() {
        return String.format("[%s / %s]", left, right);
    }
}