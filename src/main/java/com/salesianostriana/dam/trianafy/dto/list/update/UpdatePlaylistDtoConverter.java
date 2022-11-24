package com.salesianostriana.dam.trianafy.dto.list.update;

import com.salesianostriana.dam.trianafy.model.Playlist;
import org.springframework.stereotype.Component;

@Component
public class UpdatePlaylistDtoConverter {

    public Playlist updatePlaylistDtoToPlaylist(UpdatePlaylistDto updatePlaylistDto) {
        return new Playlist(
                updatePlaylistDto.getName()
        );
    }

}
