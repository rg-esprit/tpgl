_Présentation détaillée de l'étude de cas « AutoLoc » — ASI 26-27_ 



UP ASI — Architecture des Systèmes d'Information 

# **PRESENTATION DETAILLE DE L’ETUDE DE CAS** 

_« AutoLoc »_ 



<!-- Start of picture text -->
a E<br>4<br>=>.<br>AutoLoc<br><!-- End of picture text -->

Plateforme de gestion de location de véhicules multi-agences 

## **1. Objectifs pédagogiques** 

Cette étude de cas, filée sur l'ensemble des 13 séances du cours d'Architecture des Systèmes d'Information, s'appuie sur un projet unique « AutoLoc » enrichi progressivement à chaque atelier. Les étudiants observent ainsi la construction incrémentale d'une architecture logicielle complète, depuis la modélisation jusqu'à l'évaluation de la qualité et aux pistes d'amélioration continue. 

**À l'issue du parcours, l'étudiant doit être capable de :** 

- Identifier les styles et patrons d'architecture des systèmes d'information et situer l'écosystème Spring dans le développement d'applications Java d'entreprise (Séance 1) ; 

- Générer un projet Spring Boot / Maven et créer les premières entités JPA d'AutoLoc à l'aide de Lombok (Séance 2) ; 

- Concevoir un modèle de données relationnel avec Spring Data JPA (entités, associations, cascade, fetch), en mappant les associations entre entités JPA (@OneToOne, @OneToMany, @ManyToOne, @ManyToMany) et en justifiant les stratégies de cascade et de fetch retenues pour AutoLoc (Séance 3) ; 

- Identifier et corriger les défauts d'une conception logicielle non maîtrisée, puis y appliquer les patrons de conception (GoF) pertinents (Strategy, Factory, Observer, Singleton) sur une version mal conçue d'AutoLoc (Séance 4) ; 

Page 1 / 8 

_Présentation détaillée de l'étude de cas « AutoLoc » — ASI 26-27_ 

- Structurer une application en couches respectant les principes SOLID et l'injection de dépendances, en mettant en place les couches Repository et Service avec Spring Data JPA (Séances 5 & 6) ; 

- Exposer une API REST documentée, sécurisée par la validation des données (DTO + Bean Validation), avec Spring MVC et SpringDoc/OpenAPI d'une part (Séance 7), puis en introduisant les DTO, la validation et le refactoring Clean Code d'autre part (Séance 9) ; 

- Mettre en œuvre les opérations d'affectation et de désaffectation entre entités liées, en garantissant la cohérence des associations bidirectionnelles et l'atomicité des traitements via @Transactional (Séance 8) ; 

- Interroger les données avec les requêtes dérivées et JPQL, y compris agrégations et pagination, et automatiser des traitements différés avec Spring Scheduler (Séances 10 & 11) ; 

- Instrumenter une application avec la programmation orientée aspect (Spring AOP), pour la journalisation, la mesure de performance et le contrôle de rôle sur les opérations sensibles d'AutoLoc (Séance 12) ; 

- Évaluer la qualité et les limites d'une architecture monolithique et proposer des pistes d'évolution, à travers des métriques quantitatives et qualitatives (SonarQube) et l'analyse de la dette technique (Séance 13) ; 

## **2. Présentation du projet : AutoLoc** 

### **2.1 Contexte métier** 

« AutoLoc » est une entreprise de location de véhicules disposant de plusieurs agences réparties dans différentes villes. Chaque agence gère une flotte de véhicules de catégories variées (citadine, berline, SUV, utilitaire). Les clients réservent un véhicule pour une période donnée ; à la signature, un contrat est établi et des paiements y sont rattachés. L'entreprise souhaite numériser l'ensemble du processus 

: réservation, contractualisation, facturation, suivi de flotte et relances automatiques, au travers d'une application back-end Spring Boot exposant une API REST. 

Page 2 / 8 

_Présentation détaillée de l'étude de cas « AutoLoc » — ASI 26-27_ 

### **2.2 Acteurs du système** 

|**Rôle**|**Description**|**Droits principaux**|
|---|---|---|
|Client|Particulier<br>ou<br>professionnel<br>souhaitant louer un véhicule|Consulter<br>les<br>véhicules<br>disponibles,<br>créer/annuler une réservation, consulter ses<br>contrats|
|Agent d'agence|Employé en charge de la gestion<br>opérationnelle d'une agence|Gérer les véhicules, valider une réservation,<br>établir un contrat, enregistrer un paiement|
|Responsable d'agence<br>(Manager)|Supervise une agence et son<br>personnel|Droits Agent + gestion des employés,<br>consultation des statistiques de l'agence|
|Administrateur|Administre la plateforme|Gestion des agences, des catégories de<br>véhicules,<br>statistiques<br>globales,<br>configuration|



### **2.3 Modules fonctionnels** 

|**Module**|**Description**|
|---|---|
|Gestion des agences & de la flotte|CRUD des agences, des véhicules et de leurs catégories ; suivi de la<br>disponibilité et du statut (disponible, loué, en maintenance).|
|Gestion des clients|Inscription, mise à jour du profil, historique des réservations et des<br>contrats.|
|Réservation|Recherche de véhicules disponibles par ville, catégorie et période ;<br>création, modification et annulation de réservations.|
|Contractualisation & paiement|Génération d'un contrat à la validation d'une réservation,<br>enregistrement des paiements (acompte, solde, pénalités).|
|Tarification|Calcul du tarif selon la catégorie du véhicule, la durée, la période<br>(semaine/week-end) et les équipements optionnels (GPS, siège bébé,<br>etc.).|
|Tâches planifiées|Libération automatique des véhicules en fin de location, alertes de<br>contrats arrivant à échéance, statistiques.|
|Reporting & qualité|Statistiques d'occupation et de chiffre d'affaires par agence,<br>indicateurs de qualité du code et de couverture de tests.|



Page 3 / 8 

_Présentation détaillée de l'étude de cas « AutoLoc » — ASI 26-27_ 

## **3. Modèle de données conceptuel** 

Le modèle présenté ci-dessous constitue le **socle métier du projet** , construit progressivement au cours de la **Séance 2 (Atelier 1)** et de la **Séance 3 (Atelier 2)** . Il a été conçu de manière **approfondie** afin d’illustrer les différents types d’associations et mécanismes de gestion des relations entre entités abordés en cours, notamment **@OneToMany, @ManyToOne, @OneToOne, @ManyToMany** , ainsi que les stratégies de **Cascade** et de **Fetch** . 

|**Entité**|**Attributs principaux**|**Association**|**Type / Cascade-Fetch**|
|---|---|---|---|
|Agence|id, nom, ville, adresse,<br>telephone|1 Agence → N Vehicule ; 1<br>Agence → N Employe|@OneToMany,<br>FetchType.LAZY|
|Vehicule|id, immatriculation, marque,<br>modele, categorie,<br>tarifJournalier, statut|N Vehicule → 1 Agence ; N<br>Vehicule ↔ N Equipement|@ManyToOne /<br>@ManyToMany (table<br>de jointure)|
|Equipement|id, libelle (GPS, siège bébé,<br>coffre de toit…)|N Equipement ↔ N<br>Vehicule|@ManyToMany,<br>FetchType.LAZY|
|Client|id, nom, prenom, email,<br>telephone, numPermis,<br>dateInscription|1 Client → N Reservation|@OneToMany, cascade<br>= PERSIST|
|Employe|id, nom, prenom, role<br>(AGENT/MANAGER)|N Employe → 1 Agence|@ManyToOne,<br>FetchType.LAZY|
|Reservation|id, dateDebut, dateFin, statut|N Reservation → 1 Client ;<br>N Reservation → 1 Vehicule<br>; 1 Reservation → 1 Contrat|@ManyToOne ×2,<br>@OneToOne|
|Contrat|id, dateSignature,<br>montantTotal, valide|1 Contrat → 1 Reservation ;<br>1 Contrat → N Paiement|@OneToOne,<br>@OneToMany cascade =<br>ALL|
|Paiement|id, montant, datePaiement,<br>modePaiement|N Paiement → 1 Contrat|@ManyToOne,<br>FetchType.LAZY|
|Maintenance|id, dateDebut, dateFin,<br>description|N Maintenance → 1<br>Vehicule|@ManyToOne, cascade<br>= PERSIST|



##### **<u>Remarque</u>** 

La relation **Contrat → Paiement** en **cascade = ALL** permet d'illustrer la suppression en cascade (suppression d'un contrat ⇒ suppression de ses paiements), tandis que **Agence → Vehicule** reste volontairement sans cascade de suppression (on ne supprime pas un véhicule en supprimant une agence). 

Page 4 / 8 

_Présentation détaillée de l'étude de cas « AutoLoc » — ASI 26-27_ 



<!-- Start of picture text -->
- nom String<br>> (Agence tong - prenom — : String 7<br>> nom ‘String | 4 - role RoleEmploye |<br>- ville : String . fo ploy<br>- adresse: String Lo I.<br>es- telephone : String ~“ a<br>1 <<enumeration>> |<br>RoleEmploye |<br>+ AGENT<br>+ MANAGER<br>TTavenicule Long<br>CategorieVehicule [_Vehieute<br>++ CITADINEBERLINE =<*¥82>>__|_ immatriculation : String - idMaintenance :Long<br>+ SUV ~ marque String 1 - dateDebut —_: LocalDate<br>+ UTILITAIRE -- modelecategorie :StringCategorieVehicule >—]-= dateFindescription: StringLocalDate<br>[- — —]- tarifJournalier : BigDecimal ns<br>a<br><<enumeration>> <cofers ~_statut : StatutVehicule<br>StatutVehicule | 4 . Equipement<br>+ DISPONIBLE a |= idEquipement =Long<br>+ LOUE - libelle String<br>+ MAINTENANCE |<br>-<br>.<br>1 ~ IdClient Sone <<enumeration>><br>Jvenapom stingng ModePaiement<br>- email String + CARTE<br>- idReservation :Long - telephone String + ESPECES<br>- dateDebut — = LocalDate = numPermis : String + VIREMENT<br>~ dateFin LocalDate 4 - datelnscription : LocalDate<br>— statu StatutReservation Co <aujer><br>° <<ufo>><br>StatutReservation - idPaiement Long<br>_StatutResen eee mal<br>+ CONFIRMEE - idContrat : Long 1 - datePaiement : LocalDate<br>* ANNULEE - dateSignature : LocalDate = |- modePaiement :ModePaiement<br>+ TERMINEE - montantTotal : BigDecimal<br>- valide boolean<br>a<br><!-- End of picture text -->

_Figure 1.  Modèle Entités/Associations - Etude de cas « AutoLoc »_ 

Ce modèle sera implémenté progressivement : les entités seront développées lors de l’Atelier 1, puis leurs associations lors de l’Atelier 2 (Séance 3), avec une justification des choix de cascade et de fetch pour chacune d’entre elles. 

## **4. Architecture retenue** 

L’application cible adopte un **style architectural monolithique** , structuré selon le **patron d’architecture en couches (Layered Architecture)** . Cette organisation repose sur une séparation 

Page 5 / 8 

_Présentation détaillée de l'étude de cas « AutoLoc » — ASI 26-27_ 

claire des responsabilités entre les différentes couches de l’application et s’appuie sur plusieurs **patterns de conception.** 

Le tableau ci-dessous décrit, pour chaque couche, son **rôle** , le **contenu attendu** , le **package cible** ainsi que des **exemples de classes du projet « AutoLoc »** . Il constitue un guide de référence permettant aux apprenants d’identifier précisément l’emplacement et la responsabilité du code développé au cours de chaque atelier. 

|**Couche**|**Package**|**Contenu de la solution**|
|---|---|---|
|**Presentation (API)**|web.controller|VehiculeController, ClientController,<br>ReservationController,<br>ContratController, StatistiqueController,<br>OpenApiConfig|
|**Transfert (DTO)**|web.dto|VehiculeDTO,<br>ReservationRequestDTO/ResponseDTO,<br>VehiculeMapper|
|**Service (metier)**|service|IReservationService/Impl,<br>ITarificationService + strategies|
|**Persistance**|repository|IVehiculeRepository, IClientRepository,<br>IReservationRepository|
|**Domaine**|domain|Vehicule, Client, Reservation,<br>CategorieVehicule, StatutVehicule|
|**Transverse**|aspect / scheduler|LoggingAspect, PerformanceAspect,<br>ContratEcheanceScheduler|



Une application Spring Boot est souvent organisée en couches. Chaque couche a un rôle bien précis 

et ne parle qu'à ses voisines directes. Ça rend le code plus facile à comprendre, à tester et à faire évoluer. 

#### **1. Couche présentation (les contrôleurs)** 

C'est la porte d'entrée de l'application. Elle reçoit les requêtes des utilisateurs (via HTTP) et leur répond. Elle ne manipule pas directement les données internes de l'application, mais des objets simplifiés appelés DTO, pour ne rien exposer d'inutile ou de sensible. 

**2. Couche métier (les services)** 

C'est le cerveau de l'application. Elle contient les règles et la logique : que faire, dans quel ordre, sous quelles conditions. C'est ici qu'on gère par exemple une réservation ou un calcul de prix. 

**3. Couche transversale (AOP)** 

Page 6 / 8 

_Présentation détaillée de l'étude de cas « AutoLoc » — ASI 26-27_ 

Certaines tâches concernent toute l'application, comme garder une trace des actions (logs) ou gérer les erreurs. Plutôt que de répéter ce code partout, on le centralise à part. C'est un peu comme un service de sécurité qui surveille tout le bâtiment sans faire partie d'un étage en particulier. 

#### **4. Couche domaine (les objets métier)** 

Elle représente les "choses" que manipule l'application : un véhicule, une réservation, un client. Ce sont les briques de base du métier, avec leurs propres règles. 

**5. Couche persistance (l'accès aux données)** 

Elle s'occupe de sauvegarder et de récupérer les données dans la base de données, sans que le reste de l'application ait besoin de savoir comment. 

**6. Base de données** 

C'est là que les informations sont stockées de façon durable, avec des règles pour garantir qu'elles restent correctes et cohérentes. 

Page 7 / 8 

_Présentation détaillée de l'étude de cas « AutoLoc » — ASI 26-27_ 



_Figure 2. Architecture retenue_ 

## **5. Stack technique recommandée** 

|**Catégorie**|**Outils / Bibliothèques**|
|---|---|
|**Langage / Build**|Java 17+, Maven|
|**Framework**|Spring Boot, Spring Data JPA, Spring MVC, Spring AOP, Spring<br>Scheduler|
|**Base de données**|MySQL ou PostgreSQL en développement, H2 en mémoire pour les<br>tests|
|**Productivité**|Lombok, SLF4J/Logback, MapStruct (mapping DTO, optionnel)|
|**Documentation API**|springdoc-openapi (Swagger UI)|
|**Qualité**|SonarLint (IDE), SonarQube (optionnel, séance 13)|
|**Outillage**|Git/GitHub, Postman, IntelliJ IDEA|



Page 8 / 8 

