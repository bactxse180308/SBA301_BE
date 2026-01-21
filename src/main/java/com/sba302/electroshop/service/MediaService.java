package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Media;
import java.util.List;
import java.util.Optional;

public interface MediaService {
    List<Media> findAll();

    Optional<Media> findById(Integer id);

    Media save(Media media);

    void deleteById(Integer id);
}
