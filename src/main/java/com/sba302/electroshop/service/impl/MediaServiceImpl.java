package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.Media;
import com.sba302.electroshop.repository.MediaRepository;
import com.sba302.electroshop.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    @Override
    public List<Media> findAll() {
        return mediaRepository.findAll();
    }

    @Override
    public Optional<Media> findById(Integer id) {
        return mediaRepository.findById(id);
    }

    @Override
    @Transactional
    public Media save(Media media) {
        return mediaRepository.save(media);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        mediaRepository.deleteById(id);
    }
}
