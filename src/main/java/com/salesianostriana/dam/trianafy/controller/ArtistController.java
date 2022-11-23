package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;
    private final SongService songService;
    private final ArtistRepository artistRepo;

    @GetMapping("/artist/")
    public ResponseEntity<List<Artist>> listarArtistas() {
        if (artistService.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(artistService.findAll());
        }
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<Artist> mostrarArtista(@PathVariable Long id) {
        return ResponseEntity.of(artistService.findById(id));
    }

    @PostMapping("/artist/")
    public ResponseEntity<Artist> crearArtista(@RequestBody Artist a) {
        if (a.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(artistService.add(a));
        }
    }

    @PutMapping("/artist/{id}")
    public ResponseEntity<Artist> actualizarArtista(@PathVariable Long id, @RequestBody Artist a) {
        if (a.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            return ResponseEntity.of(
                    artistService.findById(id).map(art -> {
                        art.setName(a.getName());
                        artistService.add(art);
                        return art;
                    })
            );
        }
    }

    @DeleteMapping("/artist/{id}")
    public ResponseEntity<Artist> borrarArtista(@PathVariable Long id) {
        if (artistRepo.existsById(id)) {
            String nombreArtista = artistService.findById(id).get().getName();
            songService.findAll().forEach(song -> {
                if (song.getArtist().getName() == nombreArtista) {
                    song.setArtist(null);
                }
            });
            artistService.deleteById(id);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
