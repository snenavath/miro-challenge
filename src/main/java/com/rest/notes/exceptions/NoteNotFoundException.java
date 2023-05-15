package com.rest.notes.exceptions;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(String s) {
        super(s);
    }

    public NoteNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
