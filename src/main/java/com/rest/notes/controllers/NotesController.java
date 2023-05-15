package com.rest.notes.controllers;

import com.rest.notes.models.Note;
import com.rest.notes.models.User;
import com.rest.notes.services.NotesService;
import com.rest.notes.services.UsersService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class NotesController {

    @Autowired
    private NotesService notesService;

    @Autowired
    private UsersService usersService;

    private final Bucket bucket;

    public NotesController() {
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }
    private User getAuthenticated(){
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = usersService.loadUserByUsername(user.getUsername());
        return new User(userDetails.getUsername(), userDetails.getPassword());
    }

    @GetMapping(path = "/notes")
    public ResponseEntity<List<Note>> getAllNotes() {
        List<Note> notes = notesService.getNotes(getAuthenticated());
        return ResponseEntity.ok().body(notes);
    }

    @PostMapping(path = "/notes")
    public ResponseEntity<Note> saveNote(@RequestBody Note note) {
        note.setUser(getAuthenticated());
        note.setShared(false);
        Note newNote = notesService.createNote(note);
        return new ResponseEntity<>(newNote, HttpStatus.CREATED);
    }

    @PutMapping("/notes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note note){
        if(notesService.getNoteById(id).getUser().getEmail() != getAuthenticated().getEmail()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        note.setShared(false);
        Note updatedNote = notesService.updateNoteById(id, note);
        if (updatedNote != null) {
            return new ResponseEntity<>(updatedNote, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "/notes/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        if(notesService.getNoteById(id).getUser().getEmail() != getAuthenticated().getEmail()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(notesService.getNoteById(id));
    }

    @DeleteMapping(path = "/notes/{id}")
    public ResponseEntity<String> deleteNoteById(@PathVariable Long id) {
        if(notesService.getNoteById(id).getUser().getEmail() != getAuthenticated().getEmail()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean deleteOrderById = notesService.deleteNoteById(id);
        if (deleteOrderById) {
            return new ResponseEntity<>(("Note deleted - Note ID:" + id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(("Note deletion failed - Note ID:" + id), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/notes/{id}/share")
    public ResponseEntity<Note> shareNote(@RequestBody Note note, @PathVariable Long id, @RequestParam String userToShare) {
        if(notesService.getNoteById(id).getUser().getEmail() != getAuthenticated().getEmail()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserDetails shareToUser = usersService.loadUserByUsername(userToShare);
        note.setUser(new User(shareToUser.getUsername(), shareToUser.getPassword()));
        note.setShared(true);
        Note newNote = notesService.createNote(note);
        return new ResponseEntity<>(newNote, HttpStatus.CREATED);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<Note>> searchNotes(@RequestParam String query) {
        return ResponseEntity.ok().body(notesService.searchNotes(query));
    }

}
