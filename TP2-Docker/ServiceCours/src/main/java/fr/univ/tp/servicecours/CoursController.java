package fr.univ.tp.servicecours;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/cours")
public class CoursController {

    private final CoursRepository repo;
    private final EtudiantClient etudiantClient;

    public CoursController(CoursRepository repo, EtudiantClient etudiantClient) {
        this.repo = repo;
        this.etudiantClient = etudiantClient;
    }

    @GetMapping
    public List<Cours> all() {
        return repo.findAll();
    }

    @GetMapping("/enrichis")
    public List<Map<String, Object>> getCoursEnrichis() {
        List<Cours> cours = repo.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Cours c : cours) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("titre", c.getTitre());
            try {
                EtudiantDto et = etudiantClient.getEtudiantById(c.getEtudiantId());
                m.put("etudiantNom", et.getNom());
            } catch (Exception ex) {
                m.put("etudiantNom", "INCONNU");
            }
            result.add(m);
        }
        return result;
    }

    @GetMapping("/{id}")
    public Cours get(@PathVariable int id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours non trouvé"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cours create(@RequestBody Cours c) {
        return repo.save(c);
    }

    @PutMapping("/{id}")
    public Cours update(@PathVariable int id, @RequestBody Cours c) {
        if (repo.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours non trouvé");
        }
        c.setId(id);
        return repo.save(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        if (repo.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours non trouvé");
        }
        repo.delete(id);
    }
}