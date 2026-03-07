package edu.cit.yungco.expensemini.controller;

import edu.cit.yungco.expensemini.model.User;
import edu.cit.yungco.expensemini.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload/{expenseId}")
    public ResponseEntity<Map<String, String>> uploadReceipt(
            @PathVariable Long expenseId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        String fileUrl = fileStorageService.saveFile(file, expenseId, user);
        return ResponseEntity.ok(Map.of("message", "File uploaded successfully", "url", fileUrl));
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileStorageService.loadFile(filename);

        // Ensure standard images open instead of downloading, force download for others
        // if needed
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.parseMediaType("application/octet-stream")) // Let browser infer or set
                                                                                   // specifically
                .body(file);
    }
}
