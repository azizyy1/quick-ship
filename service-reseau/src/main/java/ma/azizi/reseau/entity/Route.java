package ma.azizi.reseau.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String villeDepart;

    @Column(nullable = false)
    private String villeArrivee;

    @Column(nullable = false)
    private LocalDateTime dateDepart;

    @Column(nullable = false)
    private Integer dureeEstimee;

    @Column(nullable = false)
    private Integer capaciteColis;

    protected Route() {
    }

    public Route(String nom, String villeDepart, String villeArrivee,
                 LocalDateTime dateDepart, Integer dureeEstimee,
                 Integer capaciteColis) {
        this.nom = nom;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.dateDepart = dateDepart;
        this.dureeEstimee = dureeEstimee;
        this.capaciteColis = capaciteColis;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getVilleDepart() { return villeDepart; }
    public String getVilleArrivee() { return villeArrivee; }
    public LocalDateTime getDateDepart() { return dateDepart; }
    public Integer getDureeEstimee() { return dureeEstimee; }
    public Integer getCapaciteColis() { return capaciteColis; }
    public void modifier(String nom, String villeDepart, String villeArrivee,
                         LocalDateTime dateDepart, Integer dureeEstimee,
                         Integer capaciteColis) {
        this.nom = nom;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.dateDepart = dateDepart;
        this.dureeEstimee = dureeEstimee;
        this.capaciteColis = capaciteColis;
    }
}