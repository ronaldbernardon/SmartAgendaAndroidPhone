# 📱 SmartAgenda Android

Application Android native pour SmartAgenda Pro - Affichage des événements quotidiens

## 🎯 Fonctionnalités

- ✅ **Synchronisation automatique à 7h00** chaque matin
- ✅ **Notification quotidienne** avec résumé de la journée
- ✅ **Mode offline** avec cache local
- ✅ **Affichage des événements du jour**
- ✅ **Indice UV et météo**
- ✅ **Jours fériés et vacances scolaires**
- ✅ **Interface Material Design 3**
- ✅ **Thème sombre/clair automatique**

## 🏗️ Architecture

```
SmartAgendaAndroid/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smartagenda/
│   │   │   │   ├── data/          # Modèles et API
│   │   │   │   ├── repository/    # Logique métier
│   │   │   │   ├── ui/            # Interface utilisateur
│   │   │   │   ├── worker/        # Tâches en arrière-plan
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/               # Ressources (layouts, strings)
│   │   │   └── AndroidManifest.xml
│   │   └── androidTest/
│   └── build.gradle.kts
├── .github/
│   └── workflows/
│       └── build.yml              # GitHub Actions pour APK
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🔧 Prérequis

- Android 8.0 (API 26) ou supérieur
- Connexion VPN au serveur Fedora 42
- SmartAgenda Pro en cours d'exécution sur le serveur (port 8086)

## 📦 Installation

### Méthode 1 : APK pré-compilé (Recommandé)

1. Téléchargez le dernier APK depuis [Releases](https://github.com/VOTRE_USERNAME/SmartAgendaAndroid/releases)
2. Activez "Sources inconnues" dans les paramètres Android
3. Installez l'APK

### Méthode 2 : Compilation depuis les sources

```bash
# Cloner le repository
git clone https://github.com/VOTRE_USERNAME/SmartAgendaAndroid.git
cd SmartAgendaAndroid

# Compiler avec Gradle
./gradlew assembleRelease

# L'APK sera dans : app/build/outputs/apk/release/
```

## ⚙️ Configuration

Au premier lancement :

1. **URL du serveur** : Entrez l'URL de votre serveur SmartAgenda
   - Format : `http://IP_SERVER:8086` (via VPN)
   - Exemple : `http://192.168.1.100:8086`

2. **Mot de passe** : Entrez votre mot de passe SmartAgenda
   - Le mot de passe est stocké de manière sécurisée (EncryptedSharedPreferences)

3. **Heure de notification** : Par défaut 7h00 (configurable)

## 🔒 Sécurité

- ✅ **Mot de passe chiffré** avec EncryptedSharedPreferences
- ✅ **Communication HTTPS** recommandée (certificat SSL)
- ✅ **Connexion VPN** requise pour accéder au serveur
- ✅ **Pas de stockage de données sensibles** en clair

## 🛠️ Technologies utilisées

- **Kotlin** - Langage moderne pour Android
- **Jetpack Compose** - UI déclarative
- **Retrofit** - Client HTTP
- **Room Database** - Cache local
- **WorkManager** - Tâches planifiées
- **Hilt** - Injection de dépendances
- **Coroutines** - Programmation asynchrone

## 📱 Captures d'écran

*(À ajouter après développement)*

## 🤝 Contribution

Ce projet est conçu pour un usage personnel. Les pull requests sont les bienvenues pour des améliorations.

## 📄 Licence

MIT License - Voir le fichier LICENSE

## 🐛 Problèmes connus

- Nécessite une connexion VPN active
- La synchronisation en arrière-plan peut être limitée par les optimisations de batterie Android

## 📞 Support

Pour toute question ou problème :
- Ouvrez une issue sur GitHub
- Consultez la documentation de SmartAgenda Pro

---

**Version** : 1.0.0  
**Dernière mise à jour** : Décembre 2025
