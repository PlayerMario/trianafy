package com.salesianostriana.dam.trianafy.dto.song.get;

import com.salesianostriana.dam.trianafy.dto.song.get.GetSongDto;
import com.salesianostriana.dam.trianafy.model.Song;
import org.springframework.stereotype.Component;

@Component
public class GetSongDtoConverter {

    public GetSongDto songToDto(Song s) {
        return GetSongDto
                .builder()
                .id(s.getId())
                .title(s.getTitle())
                .artist(s.getArtist().getName())
                .album(s.getAlbum())
                .year(s.getYear())
                .build();
    }

}
