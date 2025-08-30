package ru.expensive.api.feature.command.datatypes;

import ru.expensive.api.feature.command.exception.CommandException;

import java.util.function.Function;

public interface IDatatypePost<T, O> extends IDatatype {
    T apply(IDatatypeContext datatypeContext, O original) throws CommandException;
}
