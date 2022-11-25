package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.dto.list.create.CreatePlaylistDto;
import com.salesianostriana.dam.trianafy.dto.list.create.CreatePlaylistDtoConverter;
import com.salesianostriana.dam.trianafy.dto.list.get.GetOnePlaylistDto;
import com.salesianostriana.dam.trianafy.dto.list.get.GetOnePlaylistDtoConverter;
import com.salesianostriana.dam.trianafy.dto.list.get.GetPlaylistDto;
import com.salesianostriana.dam.trianafy.dto.list.get.GetPlaylistDtoConverter;
import com.salesianostriana.dam.trianafy.dto.list.update.UpdatePlaylistDto;
import com.salesianostriana.dam.trianafy.dto.list.update.UpdatePlaylistDtoConverter;
import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.PlaylistRepository;
import com.salesianostriana.dam.trianafy.service.PlaylistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Playlist", description = "Controlador para gestionar las playlists.")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final GetPlaylistDtoConverter getPlaylistDtoConverter;
    private final GetOnePlaylistDtoConverter getOnePlaylistDtoConverter;
    private final CreatePlaylistDtoConverter createPlaylistDtoConverter;
    private final UpdatePlaylistDtoConverter updatePlaylistDtoConverter;
    private final PlaylistRepository playlistRepository;
    private final SongService songService;

    // GESTIÓN DE PLAYLIST
    @GetMapping("/list/")
    public ResponseEntity<List<GetPlaylistDto>> listarPlaylists() {
        List<Playlist> listado = playlistService.findAll();

        if (listado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            List<GetPlaylistDto> resultado = listado
                    .stream()
                    .map(getPlaylistDtoConverter::playlistToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(resultado);
        }
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<GetOnePlaylistDto> mostrarPlaylist(@PathVariable Long id) {
        if (playlistService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(getOnePlaylistDtoConverter.onePlaylistToDto(playlistService.findById(id).get()));
        }
    }

    @PostMapping("/list/")
    public ResponseEntity<CreatePlaylistDto> crearPlaylist(@RequestBody CreatePlaylistDto createPlaylistDto) {
        if (createPlaylistDto.getName() == null || createPlaylistDto.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            Playlist nuevaPlaylist = createPlaylistDtoConverter.createPlaylistDtoToPlaylist(createPlaylistDto);
            List<Song> listadoCanciones = new ArrayList<>();
            nuevaPlaylist.setSongs(listadoCanciones);
            playlistService.add(nuevaPlaylist);

            return ResponseEntity.status(HttpStatus.CREATED).body(createPlaylistDto);
        }
    }

    @PutMapping("/list/{id}")
    public ResponseEntity<GetPlaylistDto> actualizarPlaylist(@PathVariable Long id, @RequestBody UpdatePlaylistDto updatePlaylistDto) {
        if (updatePlaylistDto.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            Playlist playlistEditada = updatePlaylistDtoConverter.updatePlaylistDtoToPlaylist(updatePlaylistDto);
            return ResponseEntity.of(
                    playlistService.findById(id).map(playlist -> {
                        playlist.setName(playlistEditada.getName());
                        playlistService.edit(playlist);

                        return getPlaylistDtoConverter.playlistToDto(playlist);
                    }));
        }
    }

    @DeleteMapping("/list/{id}")
    public ResponseEntity<?> borrarPlaylist(@PathVariable Long id) {
        if (playlistRepository.existsById(id)) {
            playlistService.deleteById(id);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // GESTIÓN DE CANCIONES DE UNA PLAYLIST
    @GetMapping("/list/{id}/song/")
    public ResponseEntity<GetOnePlaylistDto> listarCancionesPlaylist(@PathVariable Long id) {
        if (playlistService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(getOnePlaylistDtoConverter.onePlaylistToDto(playlistService.findById(id).get()));
        }
    }

    @GetMapping("/list/{id1}/song/{id2}/")
    public ResponseEntity<Song> mostrarCancion(@PathVariable("id1") Long idList, @PathVariable("id2") Long idSong) {
        if (playlistService.findById(idList).isEmpty() || songService.findById(idSong).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.of(playlistService.findById(idList)
                    .get().getSongs()
                    .stream().filter(song -> song.getId().equals(idSong)).findFirst());
        }
    }

    @PostMapping("/list/{id1}/song/{id2}")
    public ResponseEntity<GetOnePlaylistDto> guardarCancionLista(@PathVariable("id1") Long idList, @PathVariable("id2") Long idSong) {
        if (playlistService.findById(idList).isEmpty() || songService.findById(idSong).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            playlistService.findById(idList).get().getSongs().add(songService.findById(idSong).get());
            return listarCancionesPlaylist(idList);
        }
    }

    @DeleteMapping("/list/{id1}/song/{id2}")
    public ResponseEntity<?> borrarCancionLista(@PathVariable("id1") Long idList, @PathVariable("id2") Long idSong) {
        if (playlistRepository.existsById(idList)) {
            playlistService.borrarCancionesLista(idSong);
            playlistService.deleteById(idList);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
