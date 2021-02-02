package com.example.junit5.study;

import com.example.junit5.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StudyController {
    final StudyRepository studyRepository;

    @GetMapping("/study/{id}")
    public Study getStudy(@PathVariable Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Study no found for '" + id + "'"));
    }

    @PostMapping("/study")
    public Study getStudy(@RequestBody Study study) {
        return studyRepository.save(study);
    }
}
