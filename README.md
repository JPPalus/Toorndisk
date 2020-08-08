# Introduction

ToornDisk est un lecteur audio s'inspirant du lecteur Google Play Music. Il permet de parcourir la bibliothèque musicale d'un téléphone Android ou le système de fichier, et de jouer les morceaux et fichiers audios. La bibliothèque peut être explorée par artiste, albums, ou morceaux. Le système de fichier peut être exploré dossier par dossier depuis la racine.

# Interface

Sur l'écran d'arrivé, une barre d'onglets permet de choisir entre l'exploration par morceau, par artiste, par album, et par fichier.

![](https://raw.githubusercontent.com/olvb/toorndisk/master/screenshots/2.png)

A la sélection d'un morceau, la liste de lecture est remplie avec la totalité des morceaux de la bibliothèque du téléphone, et la lecture se lance sur le morceau sélectionné. Une interface minimaliste de lecteur apparaît en bas de l'application, avec bouton lecture/pause, affichage des métadonnées du morceau lue et d'une barre de progression.

![](https://raw.githubusercontent.com/olvb/toorndisk/master/screenshots/3.png)

Par un clic ou en glissant cette interface vers le haut de l'écran, une vue plus détaillée apparaît. L'illustration de l'album du morceau est affichée si elle est disponible, la barre de progression permet de se déplacer dans le morceau, et 2 boutons permettent de passer au morceau suivant. Glisser n'importe quelle partie de l'écran vers le bas permet de revenir vers l'interface d'exploration de la bibliothèque.

![](https://raw.githubusercontent.com/olvb/toorndisk/master/screenshots/4.png)
![](https://raw.githubusercontent.com/olvb/toorndisk/master/screenshots/5.png)

Revenons à l'interface d'exploration. Un clic sur l'onglet Artistes permet d'afficher l'ensemble des artistes de la bibliothèque. A la sélection d'un artiste, une nouvelle interface se superpose a la précédente et permet d'afficher soit l'ensemble des morceaux de l'artiste sélectionné, soit ses albums.

![](https://raw.githubusercontent.com/olvb/toorndisk/master/screenshots/6.png)

Là aussi, la sélection d'un morceau remplit la liste de lecture de l'ensemble des morceaux de l'artiste et lance la lecture sur le morceau sélectionné, en faisant apparaître l'interface réduite du lecteur. A la sélection d'un album, une nouvelle interface se superpose et affiche l'ensemble des morceaux de l'album sélectionné. La sélection d'un morceau remplit la liste de lecture avec tous les morceaux de l'album et lance la lecture du morceau choisi, en faisant apparaître l'interface réduite du lecteur.

![](https://raw.githubusercontent.com/olvb/toorndisk/master/screenshots/7.png)

Ce système de navigation hiérarchique est également utilisé lorsque l'on choisit directement un album depuis l'interface d'accueil, et peut être étendu pour supporter d'autres modes de navigation (par genre, etc). Le bouton retour de la barre d'application (en haut à gauche) ou du téléphone (en bas à gauche) peut à chaque fois être utilisé pour revenir à l'interface précédente.

L'exploration du système de fichier utilise le même système de navigation hiérarchique. La sélection d'un dossier fait se superposer une nouvelle interface affichant les fichiers du dossier sélectionné, la sélection d'un fichier remplit la liste de lecture avec les fichiers du dossier courant et lance la lecture du fichier sélectionné.

![](https://raw.githubusercontent.com/olvb/toorndisk/master/screenshots/8.png)
![](https://raw.githubusercontent.com/olvb/toorndisk/master/screenshots/9.png)

# Organisation générale du code

Ce projet utilise le langage Kotlin plutôt que Java, ainsi que les composants Jetpack de Google. La principale différence de Jetpack réside dans AndroidX, qui vise à remplacer le système de Support Library. Le versioning en V6, V7, V8, etc, est abandonné pour un versioning sémantique (X.Y.Z) et chaque composant peut être mis à jour indépendamment des autres. AndroidX est destiné à remplacer définitivement la Android Support Library.

Pour cette application, de nombreux éléments d'interface sont réutilisés à différents endroits :

* un liste de morceaux peut être affichée dans un onglet de l'Activity d'accueil (*MainActivity*), dans un onglet de l'Activity Artiste ou à la racine de l'Activity Album. Idem pour une liste d'albums ;
* une liste de fichiers peut être affichée dans un onglet de l'Activity d'accueil ou à la racine de l'Activity Fichiers. Pour cela nous avons utilisés des *Fragments*, afin d'inclure un élément d'interface et son code controlleur dans différentes Activities ;
* l'interface de lecture est affichée sur toutes les activités.

Un dossier *model* contient les dataclasses représentent les objets récupérés dans la bibliothèque média du téléphone (*Track*, *Album*, *Artist*) ainsi que la liste de lecture et l'état de la lecture (en lecture ou en pose, morceau joué, position de la tête de lecture, etc)

Pour chacun des modes d'exploration de la bibliothèque ou du système de fichier (tracks, albums, artists, files), un dossier regroupe:
* le *Fragment* affichant la liste des objets (constitué d'un *RecyclerView*[1] placé dans un *SwipeRefreshLayout*[2]) ;
* le *ViewModel* chargé de récupérer la liste des objets à afficher ;
* l'*Adapter* nécessaire pour fournir à la *RecyclerView* les données à afficher ;
* si elle existe, l'*Activity* correspondant au mode d'exploration.

L'Activity contient un CoordinatorLayout[3], contenant lui-même un *ConstraintLayout* positionnant le *Fragment* listant les objets, ainsi que le *Fragment* de l'interface de lecture affichée en bas d'interface et extensible vers le haut. Ce comportement (étendre en plein écran l'interface du lecteur) est implémenté par une *BottomSheetBehavior*[4]. Si des onglets sont présents dans l'Activity, ils sont implémentés avec un *ViewPager*.

La *MainActivity* est similaire aux Activities déjà décrites, avec ceci de particulier qu'en tant qu'Activity d'entrée, elle est responsable de demander les autorisations nécessaires au bon fonctionnement de l'application lors du premier lancement, à savoir *READ_EXTERNAL_STORAGE*. Si l'autorisation est refusée par l'utilisateur, un message est affiché avec un bouton permettant de redemander l'autorisation.

![](https://raw.githubusercontent.com/olvb/toorndisk/master/screenshots/0.png)
![](https://raw.githubusercontent.com/olvb/toorndisk/master/screenshots/1.png)

[1] La classe *RecyclerView* est similaire à la classe *ListView*mais elle permet de limiter le nombre de *ViewHolder* (cellules) instantiées en ne créant des instances que pour les objets affichés et en réutilisant ces instances pour
différents objets

[2] Le *SwipeRefreshLayout* permet d'implémenter le comportement "swipe to refresh" (balayage vers le haut pour recharger le contenu de la liste)

[3] Le *CoordinatorLayout* permet de contrôler l'affichage et le contenu de l'*AppBar* et de barres d'outils supplémentaires

[4] La *BottomSheetBehavior* est un comportement au sein d'un *CoordinatorLayout* initialement destiné à afficher une liste d'action rétractable en bas de l'interface.

# Accès aux données

Pour récupérer les morceaux, albums, artistes de la bibliothèque Android, il faut utiliser le *ContentResolver* de l'application, et construire une requête décrivant le type d'objet que l'on souhaite récupérer et les éventuels filtres que l'on souhaite appliquer (morceaux d'un album ou d'un artiste, etc), à la façon d'une requête SQL. Le *ContentResolver* retourne un curseur pointant sur le premier objet trouvé. On peut alors récupérer chaque colonne (c'est-à-dire chaque champ) nous intéressant et itérer sur le curseur pour récupérer les objets suivants. Pour parcourir les fichiers, nous utilisons en revanche l'API *java.io*.

Dans les deux cas, le temps pris à récupérer la liste des objets peut être suffisant long pour donner la sensation que l'interface est figée. Nous exécutons donc ces tâches de façon asynchrone, grâce à *AsyncTask* [5] et *MutableLiveData* [6].

A sa création, le fragment s'inscrit comme observateur des propriétés *MutableLiveData* du *ViewModel*. Lorsque le Fragment a besoin des données, il interroge le *ViewModel*, qui lance la méthode de récupération des données, mais en l'appelant depuis une *AsyncTask*. Une fois les données chargées, le *ViewModel* publie les données récupérer en appelant la méthode post sur les propriétés MutableLiveData contenant les données. Le Fragment est ainsi notifié que de nouvelles données sont disponibles.

[5] *LiveData* et *MutableLiveData* sont des conteneurs de données qui peuvent être observés afin d'être notifié lorsque les données changent. Ils sont spécialement conçus pour Android afin de prendre en charge la durée de viée (Lifecycle) des observeurs, ceci devant donc implémenter l'interface *LifecycleOwner*.

[6] *AsyncTask* est un helper fourni par Android permettant d'exécuter une tâche courte (quelques secondes au maximum) sur un thread en arrière-plan afin de pas bloquer le thread UI. *AsyncTask* utilise un *ThreadPoolExecutor* afin
d'optimiser la création et réutiliser de threads.

# Lecture des médias

Sous Android, la lecture de médias en arrière-plan doit être implémentée dans un *Service* plutôt que dans une Activity, afin qu'elle ne s'interrompe pas lorsque l'Activity disparait. Un Service est en quelque sorte une Activity sans vue, et à ce titre doit être déclaré dans le Manifest.

Dans notre application, la classe *PlaybackService* est le service chargé d'implémenter la lecteur audio, et elle utilise pour cela l'API *MediaPlayer*. Lors de la lecture d'un nouveau fichier, le *MediaPlayer* doit être préparé de façon asynchrone via la méthode *prepareAsync*. Lorsque le *MediaPlayer* est prêt à démarrer la lecture, la méthode *onPrepared* (définie dans l'interface *MediaPlayer.OnPreparedListener*) est appelée.

Pour la communication entre le *PlaybackFragment* et le *PlaybackService*, nous utilisons également les *MutableLiveData*. L'état de la lecture est modélisé par un objet singleton *Playback* (dans le dossier *model*) qui contient les morceaux de la liste de lecture, l'index morceau actuellement joué, la position de la tête de lecture et durée totale du morceau jouée, etc.
Si le *PlaybackFragment* souhaite passer au morceau suivant suite à un clic sur un bouton, il met a jour l'index du morceau joué dans l'objet *Playback*. Le *PlaybackService* étant observateur de cette propriété, il reçoit une notification et sait ainsi qu'il doit passer au morceau suivant.

Réciproquement, lorsqu'un morceau est terminé et qu'il faut passer au suivant dans la liste de lecture, le *PlaybackService* modifie la propriété *MutableLiveData* contenant l'index du morceau, et le *PlaybackFragment* est ainsi notifié qu'il doit mettre a jour les informations affichés sur le morceau en cours.

# Améliorations possibles

Dans l'état actuel, si l'application est terminée (soit par l'utilisateur, soit par Android), le *PlaybackService* est terminé et la lecture est interrompue. Il est possible d'afficher une notification de type *Notification.MediaStyle*, ce qui permettrait non seulement d'offrir une interface de lecture/pause en dehors de l'application mais aussi de faire persister le *PlaybackService* lorsque l'application est terminée.

Lors de l'exploration du système de fichier, tous les fichiers du dossier courant sont placés dans la liste de lecture. Il faudrait filtrer et n'ajouter que les fichiers audios lisibles par le *MediaPlayer*.

Notre application affiche une option Settings dans le menu qui apparaît lors du clic sur le bouton droit de l'*AppBar*, mais cette option n'est pour l'instant pas implémentée.

Il pourrait être utile de revoir le système de chargement des objets de la bibliothèque (morceaux, etc) afin de ne charger que ceux afficher dans la *RecylerView*. Cela permettrait de mieux gérer les performances pour les bibliothèques contenant un très grande nombre d'objets.
