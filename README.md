# J2E-TP4 — Hibernate et persistance des données

Implémentation du **TP 4** du cours *Architecture Microservices : Conception, Déploiement et Orchestration* (MLIAEdu).

Application Java / Maven autonome (sans Spring) qui met en place une couche de persistance complète
avec **Hibernate 5.6** et **MySQL**, en suivant une architecture en couches
(Entités → DAO → Service → Tests).

> Le code, les commentaires et les noms enregistrés en base (tables et colonnes) sont en **anglais**.
> Seule cette documentation est en français.

## Objectifs couverts

| Objectif du TP | Où |
|---|---|
| Comprendre le fonctionnement d'un ORM | `entities/`, `hibernate.cfg.xml` |
| Configurer Hibernate avec Maven et un fichier XML | `pom.xml`, `src/main/resources/hibernate.cfg.xml` |
| Architecture en couches (Entités, DAO, Service) | `src/main/java` |
| Pattern DAO générique `IDao<T>` | `dao/IDao.java` |
| Associations JPA `OneToMany` / `ManyToOne` | `Room` ↔ `Machine` |
| Requêtes HQL personnalisées | `dao/RoomDao.java`, `dao/MachineDao.java` |
| Tests unitaires JUnit 5 | `src/test/java` |

## Structure du projet

```
J2E-TP4-Hibernate/
├── pom.xml
├── src/main/java/
│   ├── entities/
│   │   ├── Room.java            # the "one" side (table: rooms)
│   │   └── Machine.java         # the "many" side (table: machines)
│   ├── dao/
│   │   ├── IDao.java            # generic CRUD contract
│   │   ├── RoomDao.java         # CRUD + HQL queries for rooms
│   │   └── MachineDao.java      # CRUD + HQL queries for machines
│   ├── services/
│   │   ├── RoomService.java
│   │   └── MachineService.java
│   ├── util/
│   │   └── HibernateUtil.java   # singleton SessionFactory
│   └── test/
│       └── MainTest.java        # manual smoke test (mvn exec:java)
├── src/main/resources/
│   └── hibernate.cfg.xml        # MySQL connection + entity mappings
└── src/test/
    ├── java/dao/RoomDaoTest.java
    ├── java/dao/MachineDaoTest.java
    ├── java/services/ServiceLayerTest.java
    └── resources/hibernate-test.cfg.xml   # H2 in-memory for the tests
```

## Modèle de données

```
rooms                          machines
-----                          --------
id       (PK, auto)            id          (PK, auto)
code     VARCHAR(50)           reference   VARCHAR(50)
label    VARCHAR(100)          brand       VARCHAR(50)
                               room_id     (FK -> rooms.id)
```

Un `Room` possède plusieurs `Machine` (`@OneToMany(cascade = ALL, orphanRemoval = true)`),
et chaque `Machine` appartient à un `Room` (`@ManyToOne`).

## Prérequis

- JDK 17 ou supérieur
- Maven 3.8+
- MySQL 8 (ou MariaDB) pour l'exécution « réelle »
- Aucune base requise pour les tests : ils tournent sur **H2 en mémoire**

## Configuration MySQL

1. Créer la base :

```sql
CREATE DATABASE db_tp_hibernate CHARACTER SET utf8mb4;
```

2. Adapter si besoin les identifiants dans `src/main/resources/hibernate.cfg.xml` :

```xml
<property name="connection.url">jdbc:mysql://localhost:3306/db_tp_hibernate?useSSL=false&amp;serverTimezone=UTC</property>
<property name="connection.username">root</property>
<property name="connection.password"></property>
```

> `hbm2ddl.auto=update` laisse Hibernate créer/mettre à jour les tables `rooms` et `machines`
> à partir des entités : aucune migration manuelle n'est nécessaire.

## Exécution

```bash
# Compilation + tests (H2 en mémoire)
mvn clean test

# Exécution du test manuel sur MySQL (insère 1 salle et 2 machines)
mvn exec:java
```

Sortie attendue de `mvn exec:java` :

```
=== Persisted data ===
Rooms:
  Room{id=1, code='IT-01', label='Computer Lab'}
Machines:
  Machine{id=1, reference='M-101', brand='Dell'} -> room IT-01
  Machine{id=2, reference='M-102', brand='HP'} -> room IT-01
Machines of brand Dell: [Machine{id=1, reference='M-101', brand='Dell'}]
Machines in room IT-01: 2
Data inserted successfully.
```

## API des couches

### `IDao<T>`

```java
boolean create(T entity);
boolean update(T entity);
boolean delete(T entity);
T getById(int id);
List<T> getAll();
```

### Requêtes HQL ajoutées

| Méthode | Requête |
|---|---|
| `RoomDao.findByCodePrefix(prefix)` | `from Room r where r.code like :prefix order by r.code` |
| `RoomDao.findRoomsWithMachines()` | `select distinct m.room from Machine m` |
| `RoomDao.countMachines(roomId)` | `select count(m) from Machine m where m.room.id = :roomId` |
| `MachineDao.findByBrand(brand)` | `from Machine m where m.brand = :brand order by m.reference` |
| `MachineDao.findByRoom(room)` | `from Machine m where m.room.id = :roomId order by m.reference` |
| `MachineDao.searchByReference(keyword)` | `from Machine m where upper(m.reference) like :keyword` |

## Tests

| Classe | Contenu |
|---|---|
| `RoomDaoTest` | CRUD, génération d'id, filtre HQL par préfixe, jointure HQL |
| `MachineDaoTest` | CRUD, association avec `Room`, recherche par marque / salle / référence |
| `ServiceLayerTest` | délégation de la couche service vers la couche DAO |

```
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
```

## Gestion des transactions

Chaque opération d'écriture ouvre une `Session` dans un bloc *try-with-resources*,
démarre une transaction, puis *commit*. En cas d'exception, un `rollback` est tenté
(défensivement, car Hibernate peut déjà avoir invalidé la transaction) :

```java
Transaction tx = null;
try (Session session = HibernateUtil.getSessionFactory().openSession()) {
    tx = session.beginTransaction();
    session.persist(entity);
    tx.commit();
    return true;
} catch (Exception ex) {
    rollback(tx);
    return false;
}
```

## Bonnes pratiques appliquées

- `SessionFactory` unique (construit une seule fois, fermé par un *shutdown hook*)
- `openSession()` dans un *try-with-resources* : aucune fuite de connexion
- Colonnes nommées explicitement (`@Column(name = ...)`) pour garder un schéma stable
- Tests isolés de MySQL grâce à un fichier de configuration Hibernate dédié

## Pistes d'amélioration

- Passer à `FetchType.EAGER` ou utiliser un `EntityGraph` sur les collections chargées en *lazy*
- Ajouter un pool de connexions (HikariCP)
- Remplacer `System.out` par un logger (SLF4J)
