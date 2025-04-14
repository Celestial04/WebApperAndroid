![https://github.com/Celestial04/WebApper/releases/latest/download/WebApper.apk](https://img.shields.io/github/release/Celestial04/WebApper.svg?logo=github)
# WebApper est un navigateur simple a utiliser, sécurisé, privé, et open-source.

Vous avez besoin d'aller chercher un paramètre spécifique sans avoir à naviguer a travers des
menus? WebApper est là pour vous aider.

Navigation :

- [Choses dont permet WebApper](#voici-les-choses-dont-permet-webapper)
- [Vidéo démo](#voici-une-petite-vidéo-montrant-toutes-les-fonctions-de-webapper)
- [Téléchargements](#téléchargements)
- [Traductions](#traduction)
- [Contribuer](#contribution)
- [Compiler WebApper](#compiler-webapper)

## Voici les choses dont permet WebApper

### Acessibilité renforcé

WebApper est un navigateur qui vous facilite l'accès aux paramètres, vous souhaitez ajouter un site
aux favoris ? Un bouton dans le Menu défilant. vous souhaitez supprimer toutes les données de
l'application ? Un bouton est disponible aussi dans le Menu défilant, changer d'URL? C'est aussi
inclus dans le Menu défilant.
Enfin bref, tout est acessible directement en seuelement deux touchés.

### Sécurisé

WebApper est aussi un navigateur sécurisé, toutes vos données peuvent être supprimées rapidement, et
**votre historique de navigation n'est pas enregistré** , cela veut dire que lorsque vous fermer le
navigateur, l'**historique disparaît**. Assez pratique n'es-ce pas?

### Open source

WebApper est Open-Source, cela veut dire que si quelqu'un veut modifier WebApper afin de mettre un
bloqueur de pubs, c'est possible. Entendre un "*miaou*" à chaque fois que vous changez de page ?
C'est aussi possible.
Le plus grand avantage selon moi c'est de **pouvoir proposer ses propres changements pour
l'application**, c'est vrai quoi, voir une fonction qu'on propose à une chose que tout le monde peut
utiliser, ça c'est cool.

### Aussi quelques trucs que je trouve bien:

- Rapidité d'ouverture
- Deux thèmes sont disponibles, le thème Nuit, et le thème Clair.

## Voici une petite vidéo montrant toutes les fonctions de WebApper

![Démo](demo.gif)
*Vidéo démo sur les fonctionnalitées de l'application*

## Téléchargements

### Où télécharger WebApper ?

- [Versions stable++](https://play.google.com/store/apps/details?id=com.hugdev.webapper): Versions
  signées, elles sont publiées BEAUCOUP moins rapidement que les versions de développement et ont
  moins de bugs que les versions de développements, sont des versions plus sûres, les mises a jour
  sont assurés par le Play Store, mais ne peuvent pas être compatible avec les nouvelles versions
  d'Android.
- [Versions stables](https://github.com/hugococa2004/WebApper/releases/latest/download/WebApper.apk):
  Versions signées, elles sont publiées moins rapidement et ont moins de bugs que les versions de
  développements mais sont des versions plus sûres.
- [Versions de développements](https://github.com/hugococa2004/WebApper/actions?query=is%3Asuccess):
  Versions qui sont créées via GitHub Action lorsque un fichier du code source à été modifié, donc
  les builds peuvent contenir des bugs, ou peuvent ne pas se lancer ou avoir un problème lors de
  l'installation, toute fois ces builds peuvent aussi contenir de nouvelles fonctionnalités ou/et
  renforcer la sécurité de l'application.

> [!IMPORTANT]
> Le seul moyen de télécharger l'application pour l'instant est uniquement de télécharger l'APK sur GitHub.
> Pour utiliser les builds stables les plus récentes, **veuillez utiliser les APKs qui sont
uniquement sur GitHub pour l'instant**.

> [!NOTE]
> Si jamais, vous voulez proposer des changements, vous pouvez le faire en créant un PR. (c'est un peu le principe de l'Open Source, mais je le rappelle au cas où.)

## Traduction

L'application peut être **traduite**, toutes traductions sont les bienvenues. Pour commencer rien de
plus simple que d'aller sur [la page Crowdin de WebApper](https://crowdin.com/project/webapper). Les
traductions effectuées seront en attentes de validations, une fois les traductions validées, elles
seront aussitôt mises dans le code source de l'application.

## Contribution

Tout le monde peut proposer des changements, voir des corrections, ce qui m'aide beaucoup, et permet
d'accélerer le développement de WebApper, et j'en suis très reconnaissante et je vous remercie d'avance!

## Compiler WebApper
Vous aurez besoin de:
- [Android Studio](https://developer.android.com/studio)
- [Git](https://git-scm.com/downloads)
- [Eclipse Temurin](https://adoptium.net/fr/temurin/releases/) ou [Java](https://www.oracle.com/fr/java/technologies/downloads/) (java 17 ou plus néssecaire)

1. cloner le repo
```bash
git clone https://github.com/Celestial04/WebApperAndroid.git
cd WebApperAndroid
```

2. Ouvrez le repo cloné
Lancez Android Studio puis ouvrez le dossier contenant le repo cloné

3. Compiler WebApper

  **via Android Studio** > `Build` > `Make Project`
  
  **en ligne de commande**:
  
   - **Linux/macOS**:
     `./gradlew assembleDebug`
   - **Windows**:
     `gradlew.bat assembleDebug`

Pour savoir comment faire un PR, merci de suivre la partie [Contribution](#contribution)
