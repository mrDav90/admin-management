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

1. Depuis la racine du répertoire du projet, on se positionne dans le dossier src/main/docker 
```
cd src/main/docker
```
2. Ensuite on lance la commande pour créer nos conteneurs docker
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

### Démarrage du frontend
1. A la racine du répertoire, on se positionne dans le dossier medical-app
```
cd medical-app
```
2. Une fois dans le répertoire, on démarre l'application frontend avec la commande
```
ng serve
```
3. On peut accéder à l'application sous l'adresse http://localhost:4203
4. Lorsque c'est lancé, on sera redirigé sur l'interface de connexion de keycloak pour nous authentifier. Voici les crédentials
```
login: admin
password: passer
```

### Quelques captures
