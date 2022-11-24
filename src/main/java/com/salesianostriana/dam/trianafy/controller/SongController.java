package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.dto.song.CreateSongDto;
import com.salesianostriana.dam.trianafy.dto.song.GetSongDto;
import com.salesianostriana.dam.trianafy.dto.song.SongDtoConverter;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
import com.salesianostriana.dam.trianafy.repos.SongRepository;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final SongRepository songRepository;
    private final SongDtoConverter dtoConverter;
    private final GetSongDto songDto;
    private final ArtistService artistService;

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

    @PostMapping("/song/")
    public ResponseEntity<Song> crearCancion(@RequestBody CreateSongDto createSongDto) {
        if (createSongDto.getArtistId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Song nuevaCancion = dtoConverter.createSongDtoToSong(createSongDto);
        artistService.setearArtistaCancion(createSongDto.getArtistId(), nuevaCancion);
        return ResponseEntity.status(HttpStatus.CREATED).body(songService.add(nuevaCancion));

        //GetSongDto dtoSong = data.new(GetMonumentoDto::of)
        //return ResponseEntity.status(HttpStatus.CREATED).body(GetSongDto::of(nuevaCancion));
    }

    @PutMapping("/song/{id}")
    public ResponseEntity<Song> actualizarCancion(@PathVariable Long id, @RequestBody CreateSongDto createSongDto) {
        if (createSongDto.getArtistId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Song cancionEditada = dtoConverter.createSongDtoToSong(createSongDto);
        artistService.setearArtistaCancion(createSongDto.getArtistId(), cancionEditada);

        return ResponseEntity.of(
                songService.findById(id).map(song -> {
                    song.setTitle(cancionEditada.getTitle());
                    song.setAlbum(cancionEditada.getAlbum());
                    song.setYear(cancionEditada.getYear());
                    song.setArtist(cancionEditada.getArtist());
                    songService.add(song);
                    return song;
                })
        );
    }

    @DeleteMapping("/song/{id}")
    public ResponseEntity<?> borrarCancion(@PathVariable Long id) {
        if (songRepository.existsById(id)) {
            songService.deleteById(id);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
