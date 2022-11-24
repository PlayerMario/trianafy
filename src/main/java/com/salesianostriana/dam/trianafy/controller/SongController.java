package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.dto.song.create.CreateSongDto;
import com.salesianostriana.dam.trianafy.dto.song.get.GetSongDto;
import com.salesianostriana.dam.trianafy.dto.song.create.CreateSongDtoConverter;
import com.salesianostriana.dam.trianafy.dto.song.get.GetSongDtoConverter;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.SongRepository;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Canción", description = "Controlador para gestionar las canciones.")
public class SongController {

    private final SongService songService;
    private final SongRepository songRepository;
    private final CreateSongDtoConverter dtoConverter;
    private final GetSongDtoConverter getSongDto;
    private final ArtistService artistService;

    @Operation(summary = "Obtener el listado de canciones")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de canciones encontrado",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Song.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                                {"id": 7, "title": "Don't Start Now", "artist": "Dua Lipa", "album": "Future Nostalgia", "year": "2019"},
                                                {"id": 9, "title": "Enter Sandman", "artist": "Metallica", "album": "Metallica", "year": "1991"}
                                            ]
                                            """
                            )}
                    )}),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existen canciones",
                    content = @Content)})
    @GetMapping("/song/")
    public ResponseEntity<List<GetSongDto>> listarCanciones() {
        List<Song> listado = songService.findAll();

        if (listado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            List<GetSongDto> resultado = listado
                    .stream()
                    .map(getSongDto::songToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(resultado);
        }
    }

    @Operation(summary = "Obtener canción por su ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Canción encontrada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {"id": 9, "title": "Enter Sandman", "artist": {"id": "3", "nombre": "Metallica"}, "album": "Metallica", "year": "1991"}
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404", description = "Canción no encontrado",
                    content = @Content)})
    @GetMapping("/song/{id}")
    public ResponseEntity<Song> mostrarCancion(@PathVariable Long id) {
        return ResponseEntity.of(songService.findById(id));
    }

    @Operation(summary = "Crear nueva canción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Canción creada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {"id": 14, "title": "Levitating", "artist": "Dua Lipa", "album": "Future Nostalgia", "year": "2020"}
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400", description = "Cuerpo para la creación aportado inválido",
                    content = @Content)})
    @PostMapping("/song/")
    public ResponseEntity<GetSongDto> crearCancion(@RequestBody CreateSongDto createSongDto) {
        if (createSongDto.getArtistId() == null || createSongDto.getYear() == null || createSongDto.getTitle() == null || createSongDto.getAlbum() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            Song nuevaCancion = dtoConverter.createSongDtoToSong(createSongDto);
            artistService.setearArtistaCancion(createSongDto.getArtistId(), nuevaCancion);
            songService.add(nuevaCancion);

            return ResponseEntity.status(HttpStatus.CREATED).body(getSongDto.songToDto(nuevaCancion));
        }
    }

    @Operation(summary = "Modificar una canción, buscada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canción modificada correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {"id": 14, "title": "Cold Heart", "artist": "Dua Lipa", "album": "The Lockdown Sessions", "year": "2021"}
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400", description = "Cuerpo para la modificación aportado inválido",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encuentra la canción",
                    content = @Content)
    })
    @PutMapping("/song/{id}")
    public ResponseEntity<GetSongDto> actualizarCancion(@PathVariable Long id, @RequestBody CreateSongDto createSongDto) {
        if (createSongDto.getArtistId() == null || createSongDto.getYear() == null || createSongDto.getTitle() == null || createSongDto.getAlbum() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            Song cancionEditada = dtoConverter.createSongDtoToSong(createSongDto);
            artistService.setearArtistaCancion(createSongDto.getArtistId(), cancionEditada);
            return ResponseEntity.of(
                    songService.findById(id).map(song -> {
                        song.setTitle(cancionEditada.getTitle());
                        song.setAlbum(cancionEditada.getAlbum());
                        song.setYear(cancionEditada.getYear());
                        song.setArtist(cancionEditada.getArtist());
                        songService.edit(song);

                        return getSongDto.songToDto(song);
                    }));
        }
    }

    @Operation(summary = "Eliminar una canción, buscada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Canción eliminada correctamente, sin contenido",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Song.class),
                            examples = {@ExampleObject(
                                    value = """
                                                {}
                                            """
                            )}
                    )})})
    @DeleteMapping("/song/{id}")
    public ResponseEntity<?> borrarCancion(@PathVariable Long id) {
        if (songRepository.existsById(id)) {
            songService.deleteById(id);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
