# SAE 3.01 - Application de Gestion de Projet

## Auteurs
- Nathan SPÄTH
- Léo BOUGNOUX  
- Gabriel MARIQUE

---

## Table des Matières

1. [Lancement de l'Application](#lancement-de-lapplication)
2. [Navigation entre les Vues](#navigation-entre-les-vues)
3. [Vue Kanban](#vue-kanban)
4. [Vue Liste](#vue-liste)
5. [Vue Gantt](#vue-gantt)
6. [Vue Archives](#vue-archives)
7. [Vue Calendrier](#vue-calendrier)

---

## Lancement de l'Application

Au lancement, l'application s'ouvre sur la **Vue Kanban** par défaut.

L'interface principale se compose de :

- **Une barre de menu** en haut contenant 5 icônes cliquables permettant de changer de vue
- **Une barre de recherche** pour rechercher des tâches par leur titre
- **Un filtre de priorité** (ComboBox) permettant de filtrer les tâches selon leur priorité : Toutes les priorités, Basse, Moyenne, Haute, Urgente
- **Un bouton "Tri Durée"** qui bascule à chaque clic entre : Aucun → Croissant → Décroissant → Aucun
- **Un bouton "Ajouter une liste"** situé en dessous de la barre de menu

---

## Navigation entre les Vues

En haut de l'interface se trouve un menu composé de **5 icônes**. Chaque icône est un bouton permettant de changer de vue :

| Icône | Vue |
|-------|-----|
| Listes multicolores | Kanban |
| Liste avec coches | Liste |
| Main avec œil | Gantt |
| Boîte d'archive | Archives |
| Calendrier | Calendrier |

Cliquez sur une icône pour basculer instantanément vers la vue correspondante.

---

## Vue Kanban

C'est la vue par défaut au lancement de l'application. La barre de recherche, le filtre de priorité et le tri par durée sont disponibles dans cette vue.

### Gérer les listes

Pour **créer une liste**, cliquez sur le bouton "Ajouter une liste". Une fenêtre apparaît vous demandant de saisir un nom pour la liste. Validez avec "OK" ou annulez avec "Annuler".

Chaque liste possède un **bouton en haut à droite** (menu) permettant de :
- Renommer la liste
- Archiver la liste *(fonc.1)*

> **fonc.1** : L'archivage d'une liste archive automatiquement toutes les tâches qu'elle contient.

### Gérer les tâches

En bas de chaque liste se trouve un bouton **"Ajouter une tâche"**.

Lors de la **création d'une tâche**, vous devez renseigner :
- Un nom
- Une date de début
- Une date de fin
- Une priorité

Pour **consulter les détails d'une tâche**, effectuez un double-clic sur celle-ci *(fonc.2)*. Une fenêtre s'ouvre affichant toutes les informations de la tâche.

Dans cette fenêtre de détails, vous pouvez :
- Ajouter ou modifier la description
- Cocher la case "Est un projet" pour indiquer qu'il s'agit d'une tâche composite (tâche mère)
- Définir les dépendances avec d'autres tâches

La **durée de la tâche** est calculée automatiquement à partir des dates de début et de fin *(fonc.3)*.

Pour **déplacer une tâche** d'une liste à une autre, glissez-déposez la tâche vers la liste souhaitée.

> **fonc.2** : Le double-clic sur une tâche ouvre sa fenêtre de détails.
> 
> **fonc.3** : Calcul automatique de la durée en jours.

### Tâches composites et dépendances

Une **tâche composite** (ou tâche mère) est une tâche qui possède des sous-tâches (tâches filles) dépendantes d'elle.

Pour **créer une tâche fille** :
1. Créez une nouvelle tâche
2. Double-cliquez dessus pour ouvrir ses détails
3. Cochez la case "Est une tâche composite"
4. Dans le champ "Bloquée par", sélectionnez la tâche dont elle dépend (sa tâche mère) *(fonc.4)*

La **durée totale** d'une tâche mère est calculée en fonction de ses tâches filles *(fonc.5)*.

Lors du **déplacement d'une tâche mère**, ses tâches filles suivent automatiquement si et seulement si vous avez défini le lien de dépendance dans les détails de la tâche mère (champ "Tâches dépendantes") *(fonc.6)*.

Ce lien de mobilité est **transitif** : si une tâche mère devient elle-même tâche fille d'une autre tâche, déplacer la tâche "grand-mère" déplacera toute la chaîne de tâches dépendantes *(fonc.7)*.

> **fonc.4** : Création d'une dépendance en sélectionnant la tâche mère dans "Bloquée par".
> 
> **fonc.5** : Calcul automatique de la durée totale pour les tâches composites.
> 
> **fonc.6** : Déplacement groupé des tâches filles avec leur tâche mère.
> 
> **fonc.7** : Transitivité des dépendances lors des déplacements.

### Étiquettes

Pour **ajouter une étiquette** à une tâche, effectuez un clic droit sur la tâche et sélectionnez "Ajouter une étiquette" *(fonc.8)*.

Une étiquette possède :
- Un nom
- Une couleur

Une tâche peut porter **plusieurs étiquettes** *(fonc.9)*.

> **fonc.8** : Ajout d'étiquettes via clic droit.
> 
> **fonc.9** : Support de multiples étiquettes par tâche.

### Filtres et recherche

La **barre de recherche** filtre les tâches en fonction de leur titre. La recherche prend en compte l'apparition et la fréquence des lettres dans le titre *(fonc.10)*.

Le **filtre de priorité** permet d'afficher uniquement les tâches d'une priorité donnée.

Le **tri par durée** permet d'ordonner les tâches par durée croissante ou décroissante.

> **fonc.10** : Recherche intelligente basée sur la fréquence des caractères dans les titres.

---

## Vue Liste

La barre de recherche, le filtre de priorité et le tri par durée sont disponibles dans cette vue.

### Affichage

Les tâches sont **groupées par jour** sous forme de liste chronologique :

```
Lundi 13 janvier
    Tâche 1
    Tâche 2

Mardi 14 janvier
    Tâche 3
    ...
```

Pour chaque tâche, la **deadline** est affichée à droite, suivie du **nombre de jours de durée** en rouge *(fonc.11)*.

> **fonc.11** : Affichage de la deadline et de la durée en jours pour chaque tâche.

---

## Vue Gantt

La barre de recherche, le filtre de priorité et le tri par durée ne sont **pas disponibles** dans cette vue.

### Affichage

Il s'agit d'un **diagramme de Gantt classique** représentant les tâches sur un axe temporel horizontal.

### Visualisation des dépendances

Les dépendances entre tâches sont représentées par des **flèches** reliant les tâches filles à leurs tâches mères *(fonc.12)*.

Exemple : `Tâche fille ───────▶ Tâche mère`

Conformément au principe du diagramme de Gantt, une tâche mère ne peut commencer que lorsque toutes ses tâches filles sont terminées *(fonc.13)*.

Pour **afficher ou masquer les flèches** de dépendance, cochez ou décochez la case correspondante *(fonc.14)*.

> **fonc.12** : Représentation visuelle des dépendances par des flèches.
> 
> **fonc.13** : Respect des contraintes de dépendance dans la planification.
> 
> **fonc.14** : Option pour afficher/masquer les flèches de dépendance.

---

## Vue Archives

Cette vue affiche toutes les tâches qui ont été archivées, que ce soit :
- Par archivage individuel d'une tâche
- Par archivage d'une liste (toutes les tâches de la liste sont alors archivées)

### Affichage

Les tâches archivées sont présentées sous forme de **liste**.

### Consulter une tâche archivée

Effectuez un **double-clic** sur une tâche archivée pour consulter ses détails.

### Supprimer définitivement

Dans la fenêtre de détails d'une tâche archivée, vous pouvez choisir de la **supprimer définitivement** *(fonc.15)*.

> **fonc.15** : Suppression définitive des tâches depuis les archives.

---

## Vue Calendrier

### Affichage

Un **calendrier mensuel standard** est affiché. Vous pouvez naviguer entre les mois grâce aux boutons de changement de mois *(fonc.16)*.

Chaque tâche apparaît dans la **cellule correspondant à sa deadline** *(fonc.17)*.

Si une cellule contient **beaucoup de tâches**, un système de défilement (scroll) permet de toutes les consulter sans agrandir excessivement la cellule *(fonc.18)*.

> **fonc.16** : Navigation mensuelle dans le calendrier.
> 
> **fonc.17** : Affichage des tâches à leur date de deadline.
> 
> **fonc.18** : Défilement dans les cellules contenant de nombreuses tâches.

---

*IUT Nancy Charlemagne - Université de Lorraine - 2025/2026*
