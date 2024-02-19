package com.github.ffremont.astrotheque.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Belong<T> {
    String owner;
    T data;
}
