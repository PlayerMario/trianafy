package com.salesianostriana.dam.trianafy.dto.song;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSongDto {

    private String title;
    private Long artistId;
    private String album;
    private String year;

}
