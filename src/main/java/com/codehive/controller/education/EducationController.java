package com.codehive.controller.education;

    import com.codehive.dto.education.EducationDto;
    import com.codehive.service.education.EducationService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/educations")
    @RequiredArgsConstructor
    public class EducationController {

        private final EducationService educationService;

        @PostMapping
        public ResponseEntity<EducationDto> createEducation(
                @AuthenticationPrincipal UserDetails userDetails,
                @RequestBody EducationDto educationDto) {
            String username = userDetails.getUsername();
            EducationDto createdEducation = educationService.createEducation(username, educationDto);
            return new ResponseEntity<>(createdEducation, HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        public ResponseEntity<EducationDto> updateEducation(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable Long id,
                @RequestBody EducationDto educationDto) {
            String username = userDetails.getUsername();
            EducationDto updatedEducation = educationService.updateEducation(username, id, educationDto);
            return ResponseEntity.ok(updatedEducation);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteEducation(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable Long id) {
            String username = userDetails.getUsername();
            educationService.deleteEducation(username, id);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/{id}")
        public ResponseEntity<EducationDto> getEducation(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable Long id) {
            String username = userDetails.getUsername();
            EducationDto education = educationService.getEducation(username, id);
            return ResponseEntity.ok(education);
        }

        @GetMapping
        public ResponseEntity<List<EducationDto>> getUserEducations(
                @AuthenticationPrincipal UserDetails userDetails) {
            String username = userDetails.getUsername();
            List<EducationDto> educations = educationService.getUserEducations(username);
            return ResponseEntity.ok(educations);
        }
    }