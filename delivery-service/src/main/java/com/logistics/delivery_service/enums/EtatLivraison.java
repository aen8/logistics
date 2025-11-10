package com.logistics.delivery_service.enums;

public enum EtatLivraison {
    EN_ATTENTE,          // Créée, en attente d'assignation
    ASSIGNEE,            // Livreur assigné
    EN_PREPARATION,      // En préparation à l'entrepôt
    EN_COURS,            // En cours de livraison
    LIVREE,              // Livrée avec succès
    ANNULEE,             // Annulée
    RETOURNEE,           // Retournée à l'expéditeur
    ECHEC                // Échec de livraison
}