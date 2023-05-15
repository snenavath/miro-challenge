package com.rest.notes.services;

import com.rest.notes.exceptions.NoteNotFoundException;
import com.rest.notes.models.Note;
import com.rest.notes.models.User;
import com.rest.notes.repositories.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

    public List<Note> getNotes(User user) {

        return notesRepository.findByUser(user.getEmail());

    }

    public Note getNoteById(Long id) {
        return notesRepository.findById(id).orElseThrow(() -> throwException(String.valueOf(id)));
    }

    public boolean deleteNoteById(Long id) {
        Optional<Note> note = notesRepository.findById(id);
        if (note.isPresent()) {
            notesRepository.deleteById(id);
            return true;
        } else {
            throwException(String.valueOf(id));
            return false;
        }
    }

    public Note createNote(Note note) {
        return notesRepository.save(note);
    }

    private NoteNotFoundException throwException(String value) {
        throw new NoteNotFoundException("Note Not Found with ID: " + value);
    }

    public Note updateNoteById(Long id, Note updateNote) {
        Optional<Note> note = notesRepository.findById(id);
        if (note.isPresent()) {
            note.get().setAuthor(updateNote.getAuthor());
            note.get().setTitle(updateNote.getTitle());
            note.get().setDescription(updateNote.getDescription());
            return notesRepository.save(note.get());
        }
        return null;
    }

    public List<Note> searchNotes(String query) {
        return notesRepository.search(query);
    }
}
