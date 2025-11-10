package com.logistics.delivery_service.enums;

public enum EtatColis {
    EN_ATTENTE,          // En attente de prise en charge
    EN_PREPARATION,      // En préparation
    EN_TRANSIT,          // En transit
    EN_LIVRAISON,        // En cours de livraison
    LIVRE,               // Livré
    RETOURNE,            // Retourné
    ENDOMMAGE,           // Endommagé
    PERDU                // Perdu
}