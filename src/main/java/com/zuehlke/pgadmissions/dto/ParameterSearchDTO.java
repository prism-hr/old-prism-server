package com.zuehlke.pgadmissions.dto;

public class ParameterSearchDTO<T> {

    private T minimum;

    private T interval;

    private T maximum;

    public ParameterSearchDTO(T minimum, T interval, T maximum) {
        this.minimum = minimum;
        this.interval = interval;
        this.maximum = maximum;
    }

    public T getMinimum() {
        return minimum;
    }

    public T getInterval() {
        return interval;
    }

    public T getMaximum() {
        return maximum;
    }

}