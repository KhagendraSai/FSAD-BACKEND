package com.fsad.backend.controller;

import com.fsad.backend.entity.Mark;
import com.fsad.backend.service.MarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marks")
@Tag(name = "Marks", description = "Marks and grading endpoints")
public class MarkController {

    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @GetMapping
    @Operation(summary = "Get marks list")
    public ResponseEntity<List<Mark>> getAll(@RequestParam(required = false) Long courseId) {
        return ResponseEntity.ok(markService.getAll(courseId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mark by id")
    public ResponseEntity<Mark> getById(@PathVariable Long id) {
        return ResponseEntity.ok(markService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create mark")
    public ResponseEntity<Mark> create(@Valid @RequestBody Mark mark) {
        return new ResponseEntity<>(markService.create(mark), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update mark")
    public ResponseEntity<Mark> update(@PathVariable Long id, @Valid @RequestBody Mark mark) {
        return ResponseEntity.ok(markService.update(id, mark));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete mark")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
