package com.logistics.user_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id_user") // ✅ Shared PK with User
public class Client extends User {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String telephone;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Adresse> adresses = new ArrayList<>();

    public void addAdresse(Adresse adresse) {
        adresses.add(adresse);
        adresse.setClient(this);
    }

    public void removeAdresse(Adresse adresse) {
        adresses.remove(adresse);
        adresse.setClient(null);
    }
}