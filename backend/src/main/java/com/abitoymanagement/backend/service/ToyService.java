package com.abitoymanagement.backend.service;

import com.abitoymanagement.backend.dto.ToyDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ToyService {

    ToyDTO createToy(ToyDTO toyDTO, MultipartFile image);

    List<ToyDTO> getAllToys();

    ToyDTO getToyById(String toyId);

    ToyDTO updateToy(String toyId, ToyDTO toyDTO, MultipartFile image);

    void deleteToy(String toyId);

    List<ToyDTO> searchToys(String keyword);
}
