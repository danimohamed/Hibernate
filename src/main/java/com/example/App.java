package com.example;

import com.example.model.Categorie;
import com.example.model.Produit;
import org.h2.tools.Server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;

public class App {

    public static void main(String[] args) {
        // Démarrage de la console Web H2
        demarrerConsoleH2();

        // Création de l'EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-demo");

        // Insertion de produits
        insererProduits(emf);

        // Lecture des produits
        lireProduits(emf);

        // Exercices supplémentaires
        mettreAJourPrix(emf, 1L, new BigDecimal("899.99"));
        rechercherParPrix(emf, new BigDecimal("300.00"), new BigDecimal("600.00"));
        supprimerProduit(emf, 3L);

        // Relecture après les modifications
        lireProduits(emf);

        // Fermeture de l'EntityManagerFactory
        emf.close();
    }

    private static void demarrerConsoleH2() {
        try {
            Server.createWebServer("-web", "-webPort", "8082").start();
            System.out.println("Console H2 disponible sur : http://localhost:8082");
        } catch (Exception e) {
            System.out.println("Erreur lors du démarrage de la console H2");
            e.printStackTrace();
        }
    }

    private static void insererProduits(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Création de quelques catégories
            Categorie electronique = new Categorie("Électronique");
            Categorie telephonie = new Categorie("Téléphonie");
            em.persist(electronique);
            em.persist(telephonie);

            // Création de quelques produits
            Produit p1 = new Produit("Laptop", new BigDecimal("999.99"), electronique);
            Produit p2 = new Produit("Smartphone", new BigDecimal("499.99"), telephonie);
            Produit p3 = new Produit("Tablette", new BigDecimal("299.99"), electronique);

            // Persistance des produits
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);

            em.getTransaction().commit();
            System.out.println("Produits insérés avec succès !");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void lireProduits(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            // Requête JPQL pour récupérer tous les produits
            List<Produit> produits = em.createQuery("SELECT p FROM Produit p", Produit.class)
                    .getResultList();

            System.out.println("\nListe des produits :");
            for (Produit produit : produits) {
                System.out.println(produit);
            }

            // Recherche d'un produit par ID
            System.out.println("\nRecherche du produit avec ID=2 :");
            Produit produit = em.find(Produit.class, 2L);
            if (produit != null) {
                System.out.println(produit);
            } else {
                System.out.println("Produit non trouvé");
            }
        } finally {
            em.close();
        }
    }

    private static void mettreAJourPrix(EntityManagerFactory emf, Long id, BigDecimal nouveauPrix) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Produit produit = em.find(Produit.class, id);
            if (produit != null) {
                produit.setPrix(nouveauPrix);
                System.out.println("\nPrix du produit " + id + " mis à jour à " + nouveauPrix);
            } else {
                System.out.println("\nProduit " + id + " introuvable pour la mise à jour");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void supprimerProduit(EntityManagerFactory emf, Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Produit produit = em.find(Produit.class, id);
            if (produit != null) {
                em.remove(produit);
                System.out.println("\nProduit " + id + " supprimé");
            } else {
                System.out.println("\nProduit " + id + " introuvable pour la suppression");
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void rechercherParPrix(EntityManagerFactory emf, BigDecimal min, BigDecimal max) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Produit> query = em.createQuery(
                    "SELECT p FROM Produit p WHERE p.prix BETWEEN :min AND :max", Produit.class);
            query.setParameter("min", min);
            query.setParameter("max", max);

            List<Produit> produits = query.getResultList();
            System.out.println("\nProduits dont le prix est entre " + min + " et " + max + " :");
            for (Produit produit : produits) {
                System.out.println(produit);
            }
        } finally {
            em.close();
        }
    }
}
