package com.salesianostriana.dam.trianafy.dto.song;

import com.salesianostriana.dam.trianafy.model.Song;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GetSongDto {

    private String title;
    private String artist;
    private String album;
    private String year;

    public static GetSongDto of (Song s) {
        return GetSongDto
                .builder()
                .title(s.getTitle())
                .artist(s.getArtist().getName())
                .album(s.getAlbum())
                .year(s.getYear())
                .build();
    }
}
