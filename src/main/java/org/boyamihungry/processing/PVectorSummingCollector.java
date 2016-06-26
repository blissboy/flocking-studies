package org.boyamihungry.processing;

import processing.core.PVector;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by patwheaton on 6/18/16.
 */
public class PVectorSummingCollector implements Collector<PVector, PVector, PVector> {

    @Override
    public Supplier supplier() {
        return DrawingUtilities.getOriginVector()::copy;
    }

    @Override
    public BiConsumer<PVector, PVector> accumulator() {
        return (currentSum, e) -> currentSum.add(e);
    }

    @Override
    public BinaryOperator<PVector> combiner() {
        return (currentSum, e) -> {
            currentSum.add(e);
            return currentSum;
        };
    }

    @Override
    public Function<PVector,PVector> finisher() {
        return (a) -> { return a;};
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}


