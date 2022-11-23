package com.salesianostriana.dam.trianafy.dto.song;

import com.salesianostriana.dam.trianafy.model.Song;
import org.springframework.stereotype.Component;

@Component
public class SongDtoConverter {

    public Song createSongDtoToSong(CreateSongDto songDto) {
        return new Song(
                songDto.getTitle(),
                songDto.getAlbum(),
                songDto.getYear()
        );
    }

}
