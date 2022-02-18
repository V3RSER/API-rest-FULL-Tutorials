package com.books.integrate.spring.react.controller;

import com.books.integrate.spring.react.model.Tutorial;
import com.books.integrate.spring.react.repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class TutorialController {

    @Autowired
    TutorialRepository tutorialRepository;

    @GetMapping("/tutorials")
    public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
        try {
            List<Tutorial> tutorials = new ArrayList<Tutorial>();

            if (title == null) tutorialRepository.findAll().forEach(tutorials::add);
            else tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
        Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

        if (tutorialData.isPresent()) {
            return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/tutorials")
    public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
        try {
            Tutorial _tutorial = tutorialRepository.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false));
            return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/tutorials/id/{id}")
    public ResponseEntity<Tutorial> updateTutorialByID(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
        Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

        if (tutorialData.isPresent()) {
            Tutorial _tutorial = tutorialData.get();
            _tutorial.setTitle(tutorial.getTitle());
            _tutorial.setDescription(tutorial.getDescription());
            _tutorial.setPublished(tutorial.isPublished());
            return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/tutorials/title/{title}")
    public ResponseEntity<Tutorial[]> updateTutorialByTitle(@PathVariable("title") String title, @RequestBody Tutorial tutorial) {

        try {
            // Busca los tutoriales que empiecen por el títutlo recibido
            List<Tutorial> tutorialsByTitle = tutorialRepository.findByTitleContaining(title);
            //Si no coincide con exactamente con el título recibido, se descarta
            System.out.println("antes: " + tutorialsByTitle);
            tutorialsByTitle.removeIf(tuto -> !Objects.equals(tuto.getTitle().toLowerCase(), title.toLowerCase()));
            if (tutorialsByTitle.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            System.out.println("después: " + tutorialsByTitle);
            Tutorial[] tutorialsSave = new Tutorial[tutorialsByTitle.size()];

            for (int i = tutorialsByTitle.size() - 1; i >= 0; i--) {
                Tutorial _tutorial = tutorialsByTitle.get(i);
                _tutorial.setTitle(tutorial.getTitle());
                _tutorial.setDescription(tutorial.getDescription());
                _tutorial.setPublished(tutorial.isPublished());
                tutorialsSave[i] = tutorialRepository.save(_tutorial);
            }
            return new ResponseEntity<>(tutorialsSave, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    //HttpStatus
    @DeleteMapping("/tutorials/id/{id}")
    public ResponseEntity<String> deleteTutorialByID(@PathVariable("id") long id) {
        try {
            tutorialRepository.deleteById(id);
            return new ResponseEntity<>("Tutorials DELETE!! ", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("/tutorials/title/{title}")
    public ResponseEntity<String> deleteTutorialByTitle(@PathVariable("title") String title) {
        try {
            // Busca los tutoriales que empiecen por el títutlo recibido
            List<Tutorial> tutorialsByTitle = tutorialRepository.findByTitleContaining(title);
            //Si no coincide con exactamente con el título recibido, se descarta
            System.out.println("antes: " + tutorialsByTitle);
            tutorialsByTitle.removeIf(tuto -> !Objects.equals(tuto.getTitle().toLowerCase(), title.toLowerCase()));
            if (tutorialsByTitle.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            tutorialsByTitle.forEach(tutorial -> tutorialRepository.deleteById(tutorial.getId()));
            return new ResponseEntity<>("Tutorials DELETE!! ", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("/tutorials")
    public ResponseEntity<HttpStatus> deleteAllTutorials() {
        try {
            tutorialRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/tutorials/published")
    public ResponseEntity<List<Tutorial>> findByPublished() {
        try {
            List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

}