package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.SongRepository;
import com.salesianostriana.dam.trianafy.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final SongRepository songRepository;

    @GetMapping("/song/")
    public ResponseEntity<List<Song>> listarCanciones() {
        if (songService.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(songService.findAll());
        }
    }

    @GetMapping("/song/{id}")
    public ResponseEntity<Song> mostrarCancion(@PathVariable Long id) {
        return ResponseEntity.of(songService.findById(id));
    }
}
