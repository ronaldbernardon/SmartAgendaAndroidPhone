# Icônes de l'application

## Icône de notification (ic_notification.xml)

Pour générer cette icône, vous pouvez :

1. **Via Android Studio** :
   - Clic droit sur `res/drawable` > New > Vector Asset
   - Choisir l'icône Calendar ou Event
   - Nommer : `ic_notification`

2. **Via Image Asset Studio** :
   - Tools > Resource Manager > + > Image Asset
   - Icon Type : Notification Icons
   - Choisir une icône de calendrier
   - Générer

3. **Temporairement** : L'icône par défaut d'Android sera utilisée

## Icônes de launcher (ic_launcher)

Pour les icônes de l'application :

1. **Via Android Studio** :
   - Clic droit sur `res` > New > Image Asset
   - Icon Type : Launcher Icons (Adaptive and Legacy)
   - Foreground Layer : Choisir une image de calendrier
   - Background Layer : Couleur #667EEA (violet du thème)
   - Générer

2. **Manuellement** :
   - Placez vos icônes dans les dossiers mipmap-* selon les tailles :
     - mdpi: 48x48 px
     - hdpi: 72x72 px
     - xhdpi: 96x96 px
     - xxhdpi: 144x144 px
     - xxxhdpi: 192x192 px

## Note

L'application fonctionnera sans ces icônes personnalisées, mais il est recommandé d'en ajouter pour une meilleure apparence.
