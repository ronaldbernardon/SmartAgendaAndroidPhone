# 📱 Guide d'Installation SmartAgenda Android

## 🎯 Méthode 1 : Installation directe de l'APK (Recommandé)

### Étape 1 : Télécharger l'APK

1. Allez dans l'onglet **Releases** du repository GitHub
2. Téléchargez le dernier fichier `smartagenda-release.apk`

### Étape 2 : Autoriser les sources inconnues

Sur votre téléphone Android :

1. Ouvrez **Paramètres**
2. Allez dans **Sécurité** ou **Confidentialité**
3. Activez **Sources inconnues** ou **Installer des applications inconnues**
4. Autorisez votre navigateur ou gestionnaire de fichiers à installer des APK

### Étape 3 : Installer l'APK

1. Ouvrez le fichier APK téléchargé
2. Appuyez sur **Installer**
3. Attendez la fin de l'installation
4. Appuyez sur **Ouvrir**

---

## 🔧 Méthode 2 : Compilation depuis les sources

### Prérequis

- **Android Studio** (version Electric Eel ou supérieure)
- **JDK 17** ou supérieur
- **Git**

### Étape 1 : Cloner le repository

```bash
git clone https://github.com/VOTRE_USERNAME/SmartAgendaAndroid.git
cd SmartAgendaAndroid
```

### Étape 2 : Ouvrir dans Android Studio

1. Lancez **Android Studio**
2. Cliquez sur **File > Open**
3. Sélectionnez le dossier `SmartAgendaAndroid`
4. Attendez la synchronisation Gradle (peut prendre quelques minutes)

### Étape 3 : Compiler l'APK

**Option A : Via Android Studio**

1. Allez dans **Build > Build Bundle(s) / APK(s) > Build APK(s)**
2. Attendez la compilation
3. Cliquez sur **locate** dans la notification pour trouver l'APK
4. L'APK sera dans `app/build/outputs/apk/release/`

**Option B : Via ligne de commande**

```bash
# Sur Linux/Mac
./gradlew assembleRelease

# Sur Windows
gradlew.bat assembleRelease
```

L'APK sera généré dans : `app/build/outputs/apk/release/app-release.apk`

---

## ⚙️ Configuration de l'application

### Premier lancement

Au premier démarrage, vous verrez l'écran de configuration :

### 1. URL du serveur

- Format attendu : `http://IP_SERVEUR:8086`
- Exemple : `http://192.168.1.100:8086`
- **Important** : Assurez-vous que votre VPN est actif !

### 2. Mot de passe

- Entrez le mot de passe maître de votre SmartAgenda
- Minimum 8 caractères
- Le mot de passe est stocké de manière sécurisée (chiffré)

### 3. Heure de notification

- Par défaut : **7h00**
- Tapez sur l'heure pour la modifier
- L'application enverra une notification quotidienne à cette heure

### 4. Test de connexion

1. Cliquez sur **Tester la connexion**
2. Vérifiez que vous obtenez ✅ "Connexion réussie"
3. Si erreur, vérifiez votre VPN et l'URL

### 5. Enregistrement

1. Cliquez sur **Enregistrer la configuration**
2. Attendez la validation
3. Vous serez redirigé vers l'écran principal

---

## 🔐 Configuration VPN

### Avec Wireguard (Recommandé)

1. Installez **Wireguard** depuis le Play Store
2. Importez votre configuration `.conf`
3. Activez le VPN
4. Vérifiez la connexion (icône clé en haut de l'écran)

### Avec OpenVPN

1. Installez **OpenVPN Connect**
2. Importez votre profil `.ovpn`
3. Connectez-vous
4. Vérifiez l'état de connexion

---

## 📱 Utilisation quotidienne

### Notification automatique

- Chaque matin à l'heure configurée (défaut: 7h00)
- Résumé des événements du jour
- Météo et indice UV
- Jours fériés et vacances scolaires

### Écran principal

L'application affiche :

- 🌤️ **Météo du jour** (températures min/max)
- ☀️ **Indice UV** avec niveau de protection
- 🎉 **Jours fériés** (si applicable)
- 🎒 **Vacances scolaires** Zone C (si applicable)
- 📅 **Liste des événements** triés par heure

### Actualisation

- Tirez vers le bas pour actualiser
- Ou appuyez sur l'icône ⟳ en haut à droite

---

## 🔔 Gestion des notifications

### Autoriser les notifications

Si les notifications ne s'affichent pas :

1. Ouvrez les **Paramètres Android**
2. Allez dans **Applications > SmartAgenda**
3. Appuyez sur **Notifications**
4. Activez **Autoriser les notifications**
5. Assurez-vous que "Événements quotidiens" est activé

### Désactiver l'optimisation de batterie

Pour garantir les notifications en arrière-plan :

1. **Paramètres > Applications > SmartAgenda**
2. **Batterie > Optimisation de la batterie**
3. Sélectionnez **Toutes les applications**
4. Trouvez **SmartAgenda**
5. Sélectionnez **Ne pas optimiser**

---

## 🐛 Dépannage

### Problème : "Erreur de connexion"

**Solutions :**

1. ✅ Vérifiez que votre **VPN est actif**
2. ✅ Testez l'URL dans un navigateur : `http://IP:8086`
3. ✅ Vérifiez que SmartAgenda est **démarré sur le serveur**
4. ✅ Vérifiez le **mot de passe**

### Problème : "Authentification échouée"

**Solutions :**

1. Vérifiez le **mot de passe** (sensible à la casse)
2. Assurez-vous d'utiliser le **mot de passe maître** de SmartAgenda
3. Reconnectez votre VPN

### Problème : Pas de notifications

**Solutions :**

1. Vérifiez les **autorisations de notification**
2. Désactivez l'**optimisation de batterie**
3. Vérifiez l'heure configurée dans les paramètres
4. Vérifiez que le VPN reste **actif en arrière-plan**

### Problème : Application plante au démarrage

**Solutions :**

1. Effacez le **cache de l'application** :
   - Paramètres > Applications > SmartAgenda > Stockage > Effacer le cache
2. Si problème persiste, réinstallez l'application

---

## 🔄 Mise à jour

### Depuis GitHub

1. Téléchargez le nouveau APK depuis **Releases**
2. Installez par-dessus l'ancienne version
3. Vos paramètres seront conservés

### Compilation manuelle

```bash
git pull origin main
./gradlew assembleRelease
```

---

## 📊 Informations techniques

### Permissions utilisées

- 🌐 **INTERNET** : Communication avec le serveur
- 📡 **ACCESS_NETWORK_STATE** : Vérification de la connectivité
- 🔔 **POST_NOTIFICATIONS** : Affichage des notifications
- ⏰ **SCHEDULE_EXACT_ALARM** : Planification des notifications
- 🔄 **RECEIVE_BOOT_COMPLETED** : Redémarrage des tâches après reboot
- ⚡ **WAKE_LOCK** : Réveil du téléphone pour les notifications

### Données stockées localement

- ✅ Configuration du serveur (URL)
- ✅ Mot de passe (**chiffré** avec EncryptedSharedPreferences)
- ✅ Cache des événements (7 derniers jours)
- ✅ Préférences de notification

### Consommation

- 📱 **Taille** : ~5-10 MB
- 🔋 **Batterie** : Très faible (1 vérification par jour)
- 📶 **Données** : ~100 KB par synchronisation

---

## 🆘 Support

### Problèmes techniques

Ouvrez une **issue** sur GitHub avec :

- Version d'Android
- Description du problème
- Logs si possible (Paramètres > À propos > Envoyer les logs)

### Questions

Consultez la [FAQ](FAQ.md) ou ouvrez une **discussion** sur GitHub.

---

## 📄 Licence

MIT License - Voir le fichier [LICENSE](LICENSE)

---

**Version** : 1.0.0  
**Dernière mise à jour** : Décembre 2025
