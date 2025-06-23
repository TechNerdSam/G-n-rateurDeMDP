# 🔐 Générateur de Mots de Passe Ultra Sécurisé

## Introduction 🚀

Bienvenue dans le générateur de mots de passe de nouvelle génération, une solution robuste et élégante conçue pour les professionnels et les organisations exigeants en matière de sécurité numérique. Cet outil intuitif vous permet de créer des mots de passe d'une force cryptographique inégalée et de valider leur résilience en temps réel, garantissant ainsi une protection optimale de vos actifs informationnels les plus précieux.

Notre engagement est de fournir un outil à la pointe de la sécurité, alliant performance, flexibilité et une expérience utilisateur raffinée.

## Fonctionnalités Clés ✨

Ce générateur de mots de passe se distingue par ses capacités avancées :

* **Génération Hautement Personnalisable :** Définissez la longueur de votre mot de passe (de 8 à 32 caractères) et choisissez précisément les types de caractères à inclure : majuscules, minuscules, chiffres et symboles. ⚙️
* **Analyse d'Entropie Scientifique :** Au-delà des indicateurs qualitatifs, obtenez une mesure quantitative et objective de la force de votre mot de passe, exprimée en bits d'entropie, reflétant sa résistance aux attaques par force brute. 📊
* **Retour de Force en Temps Réel :** Évaluez instantanément la solidité de tout mot de passe, qu'il soit généré ou saisi manuellement, avec des indicateurs visuels dynamiques et clairs. 🚦
* **Exclusion de Caractères Granulaire :** Maîtrisez la composition de vos mots de passe en spécifiant des caractères précis à omettre, idéal pour contourner les caractères ambigus ou les restrictions de certains systèmes. 🚫
* **Historique de Session Sécurisé :** Accédez à un historique éphémère des mots de passe générés pendant votre session, sans aucune persistance sur disque pour une confidentialité accrue. 🕰️
* **Copie Intuitive en un Clic :** Transférez instantanément votre mot de passe généré dans le presse-papiers pour une intégration fluide. 📋
* **Interface Utilisateur Moderne :** Profitez d'une interface graphique (GUI) épurée et réactive, conçue avec Swing, pour une expérience utilisateur agréable et efficace. 🎨

## Technologies Utilisées 💻

Ce projet est développé avec des technologies Java robustes et fiables :

* **Java :** Langage de programmation principal, assurant portabilité et performance.
* **Swing :** Framework pour l'interface utilisateur graphique.
* **`SecureRandom` :** Pour la génération de nombres aléatoires cryptographiquement sécurisés.

## Installation et Lancement ▶️

Suivez ces étapes simples pour mettre en œuvre le générateur de mots de passe :

### Prérequis
* Java Development Kit (JDK) 6 ou une version plus récente doit être installé sur votre système.

### Procédure de Lancement

1.  **Obtenir le Code Source :**
    * Si vous utilisez Git, clonez le dépôt :
        ```bash
        git clone https://github.com/technerdsam/GenerateurDeMDP .git
        cd password-generator/GenerateurDeMDP/PasswordGeneratorApp
        ```
    * Autrement, téléchargez et décompressez le fichier ZIP contenant le code source. Accédez au répertoire `GenerateurDeMDP/PasswordGeneratorApp`.

2.  **Compiler l'Application :**
    Ouvrez un terminal ou une invite de commande et naviguez jusqu'au répertoire `PasswordGeneratorApp`. Exécutez la commande de compilation :
    ```bash
    javac -encoding UTF-8 PasswordGeneratorApp.java
    ```
    _Assurez-vous que tous les fichiers `.java` nécessaires (`PasswordGeneratorApp.java`) sont présents dans le répertoire et que votre environnement JDK est correctement configuré._

3.  **Exécuter l'Application :**
    Après une compilation réussie, lancez l'application avec la commande suivante :
    ```bash
    java PasswordGeneratorApp
    ```
    L'interface graphique du générateur de mots de passe devrait apparaître.

## Structure du Projet 📂

Le projet est organisé de manière modulaire pour une clarté et une maintenabilité optimales :

* `PasswordGeneratorApp.java` : La classe principale de l'application, responsable de l'interface utilisateur (UI) et de la gestion des événements.
* `PasswordGeneratorApp.PasswordService` (classe interne) : Contient la logique métier pour la génération et l'évaluation de la force des mots de passe.
* `PasswordGeneratorApp.PasswordStrengthLevel` (énumération interne) : Définit les niveaux de force possibles des mots de passe (Faible, Fort, etc.) avec leurs propriétés d'affichage.

## Contribution 🤝

Les contributions sont les bienvenues ! Pour proposer des améliorations :
1.  Forkez le dépôt.
2.  Créez une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`).
3.  Commitez vos changements (`git commit -m 'Add some AmazingFeature'`).
4.  Pushez vers la branche (`git push origin feature/AmazingFeature`).
5.  Ouvrez une Pull Request.

## Contact ✉️

Pour toute question, support ou proposition de collaboration, veuillez contacter :
* Samyn-Antoy ABASSE
* samynantoy@gmail.com

# 🔐 Ultra-Secure Password Generator

## Introduction 🚀

Welcome to the next-generation password generator, a robust and elegant solution designed for demanding professionals and organizations in digital security. This intuitive tool allows you to create passwords of unparalleled cryptographic strength and validate their resilience in real-time, thereby ensuring optimal protection for your most valuable information assets.

Our commitment is to deliver a cutting-edge security tool that combines performance, flexibility, and a refined user experience.

## Key Features ✨

This password generator stands out with its advanced capabilities:

* **Highly Customizable Generation:** Define your password length (from 8 to 32 characters) and precisely choose the character types to include: uppercase, lowercase, numbers, and symbols. ⚙️
* **Scientific Entropy Analysis:** Beyond qualitative indicators, obtain a quantitative and objective measure of your password's strength, expressed in bits of entropy, reflecting its resistance to brute-force attacks. 📊
* **Real-Time Strength Feedback:** Instantly evaluate the solidity of any password, whether generated or manually entered, with dynamic and clear visual indicators. 🚦
* **Granular Character Exclusion:** Master your password composition by specifying precise characters to omit, ideal for bypassing ambiguous characters or specific system restrictions. 🚫
* **Secure Session History:** Access an ephemeral history of passwords generated during your session, with no persistence to disk for enhanced confidentiality. 🕰️
* **Intuitive One-Click Copy:** Instantly transfer your generated password to the clipboard for seamless integration. 📋
* **Modern User Interface:** Enjoy a clean and responsive Graphical User Interface (GUI), designed with Swing, for a pleasant and efficient user experience. 🎨

## Technologies Used 💻

This project is developed with robust and reliable Java technologies:

* **Java:** Primary programming language, ensuring portability and performance.
* **Swing:** Framework for the graphical user interface.
* **`SecureRandom`:** For cryptographically secure random number generation.

## Installation and Launch ▶️

Follow these simple steps to implement the password generator:

### Prerequisites
* Java Development Kit (JDK) 6 or a more recent version must be installed on your system.

### Launch Procedure

1.  **Obtain the Source Code:**
    * If you use Git, clone the repository:
        ```bash
        git clone git clone https://github.com/technerdsam/GenerateurDeMDp.git
        cd password-generator/GenerateurDeMDP/PasswordGeneratorApp
        ```
    * Alternatively, download and decompress the ZIP file containing the source code. Navigate to the `GenerateurDeMDP/PasswordGeneratorApp` directory.

2.  **Compile the Application:**
    Open a terminal or command prompt and navigate to the `PasswordGeneratorApp` directory. Execute the compilation command:
    ```bash
    javac -encoding UTF-8 PasswordGeneratorApp.java
    ```
    _Ensure that all necessary `.java` files (`PasswordGeneratorApp.java`) are present in the directory and that your JDK environment is correctly configured._

3.  **Execute the Application:**
    After successful compilation, launch the application with the following command:
    ```bash
    java PasswordGeneratorApp
    ```
    The password generator's graphical interface should appear.

## Project Structure 📂

The project is organized modularly for optimal clarity and maintainability:

* `PasswordGeneratorApp.java`: The main application class, responsible for the User Interface (UI) and event management.
* `PasswordGeneratorApp.PasswordService` (inner class): Contains the business logic for password generation and strength evaluation.
* `PasswordGeneratorApp.PasswordStrengthLevel` (inner enumeration): Defines the possible password strength levels (Weak, Strong, etc.) with their display properties.

## Contribution 🤝

Contributions are welcome! To propose improvements:
1.  Fork the repository.
2.  Create a branch for your feature (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

## Contact ✉️

For any questions, support, or collaboration proposals, please contact:
* Samyn-Antoy ABASSE
* samynantoy@gmail.com
