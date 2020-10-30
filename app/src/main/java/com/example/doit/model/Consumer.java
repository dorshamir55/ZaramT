package com.example.doit.model;

public interface Consumer<T> {
    public void apply(T param);
}
