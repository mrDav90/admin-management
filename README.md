### Gestion des rôles et permissions avec keycloak dans une application spring-boot

### Objectif
L'objectif du projet est de développer un système de gestion des permissions selon le rôle des utilisateurs dans une application. Pour ce faire nous allons utiliser l'outil populaire **keycloak**
un outil de gestion des identités et des accès.

### Prérequis
```
    Java 17 
    Docker 
    Maven
```

### Clone du projet
```
    git clone https://github.com/mrDav90/admin-management.git
```

### Exécution du docker compose

1. On se positionne dans le dossier docker 
```
    cd src/main/docker
```
2. Lancer la commande
```
    docker-compose up -d
```

### Démarrage de l'application

1. Lancement d'un clean install pour reconstruire tout le projet 
```
    mvn clean install
```
2. Démarrage de l'app en mode dev 
```
   mvn spring-boot:run -Pdev
```
3. Accès à l'app 
Si tout se passe bien, on peut accéder à l'application sous l'adresse http://localhost:8085
