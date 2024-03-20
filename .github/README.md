<div align="center">

<img src="img/logo.png"/>

<h3 align="center">Stocker facilement vos photos astro</h3>
<a href="https://github.com/javalin/javalin/actions/workflows/main.yml">
<img alt="Static Badge" src="https://img.shields.io/badge/Build-draft-orange">
</a>
  <!--License badge-->
  <a href="https://github.com/javalin/javalin/blob/master/LICENSE">
    <img alt="Static Badge" src="https://img.shields.io/badge/License-MIT-blue">
  </a>
  <!--Maven central stable version badge-->
  <a href="https://central.sonatype.com/artifact/io.javalin/javalin">
    <img alt="Stable Version" src="https://img.shields.io/maven-central/v/io.javalin/javalin?label=stable">
  </a>
</div>

## Fonctionnalités

* **Fichiers**: importation des fichiers au format FIT pour une session d'observation
* **Analyse**: analyse automatique à l'aide de la solution nova astrometry
* **Mise à jour** : édition avancer (constellation, exposition, stacking count,...)
* **Dépôt**: téléversement des fichiers FIT mais aussi des image JPG associées
* **Hébergement**: instaler sur votre poste ou sur un serveur l'application
* **Suivi**: suivi des analyses dans le temps via le menu latéral
* **Lune**: calcule automatique de la lunaison sur la base de la date
* **Recherche**: exploiter la barre recherche pour filtrer par lunaison, type, constellation...

## 👋 Utilisation

L'application Astrothèque est un programme Java compatible avec les ordinateurs personnels et les serveurs.
Une fois l'application installée, il vous faudra y **accéder avec votre navigateur** Internet.

## Installation

### Poste local

L'application est pourvue d'un lançeur pour **mac** et **windows**, permettant une installation et une mise à jour
facilitée.
L'ensemble des données seront stockées dans **votre répertoire utilisateur, dans "astrotheque"**.

🌎 👉**Adresse d'accès** : http://localhost:99999

Pour plus de fiabilité, vous pouvez synchroniser ce répertoire avec le cloud de votre choix.

### Serveur

En mode serveur basé sur Docker est disponible afin de pouvoir disposer d'une instance personnalisée.

* Récupérer l'image [floorent/astrotheque](https://hub.docker.com/r/floorent/astrotheque)
* Personnaliser `SECRET` / `DATA_DIR`
* Déployer là sur votre serveur

🔐👋 Générer une clef `SECRET`"**aes-256-cbc-hmac-sha256**", rendez-vous
sur [generate-random.org](https://generate-random.org/encryption-key-generator?count=1&bytes=32&cipher=aes-256-cbc-hmac-sha256&string=&password=)

### Configuration

Pour configurer l'application, vous pouvez utiliser des variables d'environnement :

- `SECRET`: Clé secrète pour l'application nécessaire au chiffrage de la configuration. Format aes-256-cbc-hmac-sha256.
- `DATA_DIR`: Répertoire de données pour stocker les photographies astronomiques. Par défaut, `~/astrotheque`.
- `WEB_THREAD_POOL`: Taille du pool de thread web. Par défaut `10`.
- `ASTROMETRY_NOVA_BASEURL`: URL de base pour Astrometry Nova. Par défaut, `https://nova.astrometry.net`.
- `PORT`: Port sur lequel l'application écoutera. Par défaut, `8080`.





   