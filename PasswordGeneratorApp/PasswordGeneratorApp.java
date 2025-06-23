import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PasswordGeneratorApp is a graphical user interface application
 * for generating strong, random passwords and evaluating their strength.
 * It emphasizes security by using {@link SecureRandom} for cryptographic-strength
 * random number generation. The UI is built with Swing and features a custom
 * futuristic theme.
 *
 * <p>Key features include:</p>
 * <ul>
 * <li>Customizable password length and character sets (uppercase, lowercase, numbers, symbols).</li>
 * <li>Generation of passwords with at least one character from each selected set.</li>
 * <li>Password strength evaluation (Weak, Medium, Strong, Very Strong) with Entropy calculation.</li>
 * <li>Real-time password strength feedback for typed/generated passwords.</li>
 * <li>Fine-grained character exclusion for tailored security policies.</li>
 * <li>Visual feedback for password strength and entropy.</li>
 * <li>Option to copy generated passwords to the clipboard.</li>
 * <li>Ephemeral session history of generated passwords for convenience without persistence.</li>
 * </ul>
 *
 * <p>Designed for Java 6 compatibility.</p>
 */
public class PasswordGeneratorApp {

    // --- Constantes de l'application / Application Constants ---
    private static final String APP_TITLE = "Générateur de Mots de Passe Ultra Sécurisé";
    private static final String GENERATE_BUTTON_TEXT = "Générer Mot de Passe";
    private static final String COPY_BUTTON_TEXT = "Copier";
    private static final String HISTORY_BUTTON_TEXT = "Historique";
    private static final String LENGTH_LABEL_TEXT = "Longueur (8-32) :";
    private static final String UPPERCASE_CHECKBOX_TEXT = "Majuscules (A-Z)";
    private static final String LOWERCASE_CHECKBOX_TEXT = "Minuscules (a-z)";
    private static final String NUMBERS_CHECKBOX_TEXT = "Nombres (0-9)";
    private static final String SYMBOLS_CHECKBOX_TEXT = "Symboles (!@#$)";
    private static final String EXCLUDE_CHARS_LABEL_TEXT = "Exclure caractères :";
    private static final String STRENGTH_LABEL_PREFIX = "Force: ";
    private static final String ENTROPY_LABEL_PREFIX = "Entropie: ";

    // --- Messages d'erreur et de succès / Error and Success Messages ---
    private static final String ERROR_NO_CHARSET_SELECTED = "Veuillez sélectionner au moins un type de caractère.";
    private static final String ERROR_INTERNAL_GEN_MESSAGE = "Erreur interne lors de la génération du mot de passe. Le pool de caractères est peut-être vide après les exclusions.";
    private static final String ERROR_INTERNAL_GEN_TITLE = "Erreur Interne";
    private static final String COPY_SUCCESS_MESSAGE = "Mot de passe copié dans le presse-papiers !";
    private static final String COPY_SUCCESS_TITLE = "Copie Réussie";
    private static final String COPY_ERROR_MESSAGE_PREFIX = "Erreur lors de la copie du mot de passe: ";
    private static final String COPY_ERROR_TITLE = "Erreur de Copie";
    private static final String NO_PASSWORD_TO_COPY_MESSAGE = "Aucun mot de passe à copier.";
    private static final String NO_PASSWORD_TO_COPY_TITLE = "Copie Impossible";
    private static final String HISTORY_DIALOG_TITLE = "Historique des Mots de Passe";
    private static final String HISTORY_EMPTY_MESSAGE = "Aucun mot de passe n'a encore été généré dans cette session.";
    private static final String HISTORY_EMPTY_TITLE = "Historique Vide";
    private static final String CLOSE_BUTTON_TEXT = "Fermer";
    private static final String NIMBUS_LOOK_AND_FEEL_ERROR = "Nimbus Look and Feel not found. Using default L&F. Error: ";

    // --- Dimensions UI / UI Dimensions ---
    private static final int FRAME_WIDTH = 750;
    private static final int FRAME_HEIGHT = 650;
    private static final int BORDER_PADDING = 25;
    private static final int INSETS_VERTICAL = 10;
    private static final int INSETS_HORIZONTAL = 15;
    private static final int SLIDER_MIN_LENGTH = 8;
    private static final int SLIDER_MAX_LENGTH = 32;
    private static final int SLIDER_DEFAULT_LENGTH = 16;
    private static final int PASSWORD_HISTORY_MAX_SIZE = 10;
    private static final int HISTORY_DIALOG_WIDTH = 400;
    private static final int HISTORY_DIALOG_HEIGHT = 300;


    // --- Couleurs UI / UI Colors ---
    private static final Color MAIN_GRADIENT_START = new Color(40, 20, 60); // Dark purple
    private static final Color MAIN_GRADIENT_END = new Color(25, 15, 35);   // Darker purple
    private static final Color TITLE_COLOR = new Color(255, 150, 200); // Light pink
    private static final Color LABEL_COLOR = new Color(200, 180, 220); // Light lavender
    private static final Color PASSWORD_FIELD_BG = new Color(10, 10, 15); // Very dark background
    private static final Color PASSWORD_FIELD_TEXT_COLOR = new Color(100, 255, 100); // Green text
    private static final Color PASSWORD_FIELD_BORDER_COLOR = new Color(180, 100, 255); // Purple border
    private static final Color EXCLUDE_FIELD_BG = new Color(10, 10, 20);
    private static final Color EXCLUDE_FIELD_TEXT_COLOR = new Color(200, 200, 255);
    private static final Color EXCLUDE_FIELD_BORDER_COLOR = new Color(90, 40, 150);
    private static final Color ERROR_TEXT_COLOR = new Color(255, 90, 90); // Red for errors
    private static final Color ENTROPY_LABEL_COLOR = new Color(180, 180, 255); // Light blue/purple
    private static final Color BUTTON_NORMAL_BG = new Color(120, 60, 180); // Purple
    private static final Color BUTTON_HOVER_BG = new Color(150, 90, 210);  // Lighter purple
    private static final Color BUTTON_BORDER_COLOR = new Color(90, 40, 150); // Darker purple
    private static final Color BUTTON_FOREGROUND_COLOR = Color.WHITE;


    // --- Composants de l'interface utilisateur / UI Components ---
    private JFrame frame;
    private JSlider lengthSlider;
    private JCheckBox upperCaseCheckBox;
    private JCheckBox lowerCaseCheckBox;
    private JCheckBox numbersCheckBox;
    private JCheckBox symbolsCheckBox;
    private JTextField excludeCharsField;
    private JTextField passwordDisplayField;
    private JButton generateButton;
    private JButton copyButton;
    private JButton showHistoryButton;
    private JLabel strengthLabel;
    private JLabel entropyLabel;
    private JLabel charSetErrorLabel;

    // --- Service pour la logique des mots de passe / Service for password logic ---
    private final PasswordService passwordService;

    // --- Historique des mots de passe (en mémoire pour la session courante) / Password History (in-memory for current session) ---
    private final List<String> passwordHistory;

    /**
     * Represents the evaluated strength of a password.
     * Représente le niveau de force évalué d'un mot de passe.
     */
    public enum PasswordStrengthLevel {
        EMPTY("N/A", Color.WHITE), // Non applicable, for empty or unevaluated passwords
        WEAK("Faible", new Color(255, 80, 80)),         // Red
        MEDIUM("Moyen", new Color(255, 180, 50)),       // Orange
        STRONG("Fort", new Color(100, 220, 100)),       // Light Green
        VERY_STRONG("Très Fort", new Color(50, 200, 255)); // Bright Blue/Cyan

        private final String displayName;
        private final Color displayColor;

        PasswordStrengthLevel(String displayName, Color displayColor) {
            this.displayName = displayName;
            this.displayColor = displayColor;
        }

        /**
         * Returns the display name for the strength level.
         * @return The display name (e.g., "Faible", "Fort").
         * Retourne le nom d'affichage pour le niveau de force.
         * @return Le nom d'affichage (ex: "Faible", "Fort").
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Returns the color associated with the strength level for UI display.
         * @return The display color.
         * Retourne la couleur associée au niveau de force pour l'affichage UI.
         * @return La couleur d'affichage.
         */
        public Color getDisplayColor() {
            return displayColor;
        }
    }

    /**
     * Data class to hold password strength evaluation results, including strength level and entropy.
     * Classe de données pour contenir les résultats de l'évaluation de la force du mot de passe,
     * incluant le niveau de force et l'entropie.
     */
    private static class PasswordEvaluationResult {
        final PasswordStrengthLevel strengthLevel;
        final double entropy;

        /**
         * Constructs a new PasswordEvaluationResult.
         * @param strengthLevel The evaluated password strength level.
         * @param entropy The calculated entropy in bits.
         * Construit un nouveau PasswordEvaluationResult.
         * @param strengthLevel Le niveau de force évalué du mot de passe.
         * @param entropy L'entropie calculée en bits.
         */
        PasswordEvaluationResult(PasswordStrengthLevel strengthLevel, double entropy) {
            this.strengthLevel = strengthLevel;
            this.entropy = entropy;
        }
    }

    /**
     * Handles password generation and strength evaluation logic.
     * This class is designed to be testable and independent of the UI.
     * Gère la logique de génération et d'évaluation de la force des mots de passe.
     * Cette classe est conçue pour être testable et indépendante de l'interface utilisateur.
     */
    private static class PasswordService {
        private final SecureRandom secureRandom;

        // --- Ensembles de caractères pour la génération de mots de passe / Character sets for password generation ---
        private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
        private static final String NUMBERS_CHARS = "0123456789";
        private static final String SYMBOLS_CHARS = "!@#$%^&*()_-+=<>?/{}[]|";

        // --- Constantes pour l'évaluation de la force des mots de passe / Constants for password strength evaluation ---
        private static final int SCORE_THRESHOLD_VERY_STRONG = 45;
        private static final int SCORE_THRESHOLD_STRONG = 30;
        private static final int SCORE_THRESHOLD_MEDIUM = 15;

        // --- Listes pour les pénalités d'évaluation de la force / Lists for strength evaluation penalties ---
        private static final String[] COMMON_SEQUENCES_LOWER = {
            "abc", "bcd", "cde", "def", "efg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "mno", "nop", "opq", "pqr", "qrs", "rst", "stu", "tuv", "uvw", "vwx", "wxy", "xyz",
            "qwe", "wer", "ert", "rty", "tyu", "yui", "uio", "iop",
            "asd", "sdf", "dfg", "fgh", "ghj", "hjk", "jkl",
            "zxc", "xcv", "cvb", "vbn", "bnm"
        };
        private static final String[] COMMON_SEQUENCES_NUM = {"123", "234", "345", "456", "567", "678", "789", "890", "098", "987", "876", "765", "654", "543", "432", "321"};
        private static final String[] COMMON_WEAK_WORDS = {"password", "pass", "admin", "administrator", "user", "username", "login", "logon", "guest", "test", "secret", "qwerty", "azerty", "12345", "123456", "1234567", "12345678", "123456789", "root", "support", "service", "welcome", "example", "demo", "changeme"};

        /**
         * Constructs a PasswordService and initializes {@link SecureRandom}.
         * Construit un PasswordService et initialise {@link SecureRandom}.
         */
        public PasswordService() {
            this.secureRandom = new SecureRandom();
        }

        /**
         * Filters a character set by removing specified characters.
         * This ensures that excluded characters (e.g., ambiguous ones) are not used.
         * Filtre un ensemble de caractères en supprimant les caractères spécifiés.
         * Cela garantit que les caractères exclus (par exemple, les caractères ambigus) ne sont pas utilisés.
         *
         * @param charSet The original character set string.
         * @param excludeChars The string of characters to exclude. Can be null or empty.
         * @return A new string with excluded characters removed. Returns the original charSet if excludeChars is null/empty.
         * @param charSet La chaîne de caractères de l'ensemble original.
         * @param excludeChars La chaîne de caractères à exclure. Peut être null ou vide.
         * @return Une nouvelle chaîne sans les caractères exclus. Retourne l'ensemble original si excludeChars est null/vide.
         */
        private String filterChars(final String charSet, final String excludeChars) {
            if (excludeChars == null || excludeChars.isEmpty()) {
                return charSet;
            }
            final StringBuilder filtered = new StringBuilder();
            for (int i = 0; i < charSet.length(); i++) {
                final char c = charSet.charAt(i);
                if (excludeChars.indexOf(c) == -1) { // If char is not in excludeChars
                    filtered.append(c);
                }
            }
            return filtered.toString();
        }

        /**
         * Generates a password based on the specified criteria.
         *
         * @param length       The desired length of the password.
         * @param useUpperCase Whether to include uppercase letters.
         * @param useLowerCase Whether to include lowercase letters.
         * @param useNumbers   Whether to include numbers.
         * @param useSymbols   Whether to include symbols.
         * @param excludeChars Characters to exclude from the generated password.
         * @return The generated password, or {@code null} if no valid character types are selected or the
         * effective character pool becomes empty after exclusions.
         * Génère un mot de passe basé sur les critères spécifiés.
         *
         * @param length       La longueur désirée du mot de passe.
         * @param useUpperCase Si les lettres majuscules doivent être incluses.
         * @param useLowerCase Si les lettres minuscules doivent être incluses.
         * @param useNumbers   Si les chiffres doivent être inclus.
         * @param useSymbols   Si les symboles doivent être inclus.
         * @param excludeChars Caractères à exclure du mot de passe généré.
         * @return Le mot de passe généré, ou {@code null} si aucun type de caractère valide n'est sélectionné
         * ou si le pool de caractères effectif devient vide après les exclusions.
         */
        public String generatePassword(final int length, final boolean useUpperCase, final boolean useLowerCase, final boolean useNumbers, final boolean useSymbols, final String excludeChars) {
            // Check if at least one character type is selected
            if (!useUpperCase && !useLowerCase && !useNumbers && !useSymbols) {
                return null;
            }

            final StringBuilder charPool = new StringBuilder();
            final List<Character> requiredChars = new ArrayList<Character>();

            // Filter character sets based on exclusions and build the main character pool
            final String filteredUpperCase = filterChars(UPPERCASE_CHARS, excludeChars);
            final String filteredLowerCase = filterChars(LOWERCASE_CHARS, excludeChars);
            final String filteredNumbers = filterChars(NUMBERS_CHARS, excludeChars);
            final String filteredSymbols = filterChars(SYMBOLS_CHARS, excludeChars);

            if (useUpperCase && !filteredUpperCase.isEmpty()) {
                charPool.append(filteredUpperCase);
                requiredChars.add(filteredUpperCase.charAt(secureRandom.nextInt(filteredUpperCase.length())));
            }
            if (useLowerCase && !filteredLowerCase.isEmpty()) {
                charPool.append(filteredLowerCase);
                requiredChars.add(filteredLowerCase.charAt(secureRandom.nextInt(filteredLowerCase.length())));
            }
            if (useNumbers && !filteredNumbers.isEmpty()) {
                charPool.append(filteredNumbers);
                requiredChars.add(filteredNumbers.charAt(secureRandom.nextInt(filteredNumbers.length())));
            }
            if (useSymbols && !filteredSymbols.isEmpty()) {
                charPool.append(filteredSymbols);
                requiredChars.add(filteredSymbols.charAt(secureRandom.nextInt(filteredSymbols.length())));
            }

            // If the character pool is empty after filtering/selection, we cannot generate a password
            if (charPool.length() == 0) {
                return null;
            }

            // Determine the actual password length, ensuring it's at least as long as the number of required unique characters
            final int actualLength = Math.max(length, requiredChars.size());

            final List<Character> passwordChars = new ArrayList<Character>(actualLength);

            // Add all required characters first
            passwordChars.addAll(requiredChars);

            // Fill the remaining length with random characters from the combined pool
            for (int i = requiredChars.size(); i < actualLength; i++) {
                passwordChars.add(charPool.charAt(secureRandom.nextInt(charPool.length())));
            }

            // Shuffle the entire list of characters to ensure randomness
            Collections.shuffle(passwordChars, secureRandom);

            // Construct the final password string from the shuffled characters
            final StringBuilder finalPassword = new StringBuilder(actualLength);
            for (final Character ch : passwordChars) {
                finalPassword.append(ch);
            }

            return finalPassword.toString();
        }

        /**
         * Evaluates the strength of a given password and calculates its entropy.
         * The strength is categorized into levels (Weak, Medium, Strong, Very Strong)
         * and a quantitative entropy value (in bits) is provided.
         * Évalue la force d'un mot de passe donné et calcule son entropie.
         * La force est catégorisée en niveaux (Faible, Moyen, Fort, Très Fort)
         * et une valeur d'entropie quantitative (en bits) est fournie.
         *
         * @param password The password string to evaluate.
         * @return A {@link PasswordEvaluationResult} containing the strength level and entropy.
         * @param password La chaîne du mot de passe à évaluer.
         * @return Un {@link PasswordEvaluationResult} contenant le niveau de force et l'entropie.
         */
        public PasswordEvaluationResult evaluatePasswordStrength(final String password) {
            if (password == null || password.isEmpty()) {
                return new PasswordEvaluationResult(PasswordStrengthLevel.EMPTY, 0.0);
            }

            int score = 0;
            final int length = password.length();

            // --- Calcul de l'entropie / Entropy Calculation ---
            // A simplified calculation based on character types present.
            // Une estimation simplifiée basée sur les types de caractères présents.
            int estimatedCharsetSize = 0;
            boolean hasLowerCase = false;
            boolean hasUpperCase = false;
            boolean hasDigit = false;
            boolean hasSymbol = false;

            for (int i = 0; i < length; i++) {
                final char c = password.charAt(i);
                if (Character.isLowerCase(c)) {
                    hasLowerCase = true;
                } else if (Character.isUpperCase(c)) {
                    hasUpperCase = true;
                } else if (Character.isDigit(c)) {
                    hasDigit = true;
                } else if (SYMBOLS_CHARS.indexOf(c) != -1) {
                    hasSymbol = true;
                }
            }

            if (hasLowerCase) { estimatedCharsetSize += 26; }
            if (hasUpperCase) { estimatedCharsetSize += 26; }
            if (hasDigit) { estimatedCharsetSize += 10; }
            if (hasSymbol) { estimatedCharsetSize += SYMBOLS_CHARS.length(); }

            double entropy = 0.0;
            // Entropy = length * log2(charset_size)
            // Math.log is natural logarithm (ln), so log2(x) = ln(x) / ln(2)
            if (estimatedCharsetSize > 1) { // Avoid log(0) or log(1) issues
                entropy = length * (Math.log(estimatedCharsetSize) / Math.log(2));
            }


            // --- Évaluation de la force (scoring) / Strength Evaluation (Scoring) ---
            if (length < 8) { // Passwords shorter than 8 characters are considered weak
                return new PasswordEvaluationResult(PasswordStrengthLevel.WEAK, entropy);
            }

            // Penalty if no character types are found (should be rare with generated passwords)
            if (!hasLowerCase && !hasUpperCase && !hasDigit && !hasSymbol) {
                 return new PasswordEvaluationResult(PasswordStrengthLevel.WEAK, entropy);
            }

            // Score based on length
            score += calculateLengthScore(length);

            // Score based on presence of character types
            if (hasLowerCase) { score += 5; }
            if (hasUpperCase) { score += 8; }
            if (hasDigit) { score += 8; }
            if (hasSymbol) { score += 12; }

            // Bonus for number of distinct character types
            int typesCount = 0;
            if (hasLowerCase) { typesCount++; }
            if (hasUpperCase) { typesCount++; }
            if (hasDigit) { typesCount++; }
            if (hasSymbol) { typesCount++; }

            score += calculateDistinctCharacterTypesBonus(typesCount, length);

            // Apply penalties for common weaknesses
            score = applyPenalties(password, score);


            // Final categorization based on score and character types
            final PasswordStrengthLevel strengthLevel;
            if (typesCount == 4 && score >= SCORE_THRESHOLD_VERY_STRONG) {
                strengthLevel = PasswordStrengthLevel.VERY_STRONG;
            } else if (typesCount >= 3 && score >= SCORE_THRESHOLD_STRONG) {
                strengthLevel = PasswordStrengthLevel.STRONG;
            } else if (typesCount >= 2 && score >= SCORE_THRESHOLD_MEDIUM) {
                strengthLevel = PasswordStrengthLevel.MEDIUM;
            } else {
                strengthLevel = PasswordStrengthLevel.WEAK;
            }
            return new PasswordEvaluationResult(strengthLevel, entropy);
        }

        /**
         * Calculates score based on password length. Longer passwords get higher scores.
         * Calcule le score basé sur la longueur du mot de passe. Les mots de passe plus longs obtiennent des scores plus élevés.
         * @param length The length of the password.
         * @return The score contribution from length.
         * @param length La longueur du mot de passe.
         * @return La contribution au score de la longueur.
         */
        private int calculateLengthScore(final int length) {
            if (length >= 8 && length <= 9) { return -5; } // Slight penalty for bare minimum acceptable length
            if (length >= 10 && length <= 12) { return 10; }
            if (length >= 13 && length <= 15) { return 15; }
            if (length >= 16 && length <= 20) { return 20; }
            if (length > 20) { return 25; }
            return 0;
        }

        /**
         * Calculates bonus score based on the number of distinct character types used.
         * Calcule le score bonus basé sur le nombre de types de caractères distincts utilisés.
         * @param typesCount The number of distinct character types (lowercase, uppercase, digit, symbol).
         * @param length The length of the password.
         * @return The score contribution from distinct character types.
         * @param typesCount Le nombre de types de caractères distincts (minuscules, majuscules, chiffres, symboles).
         * @param length La longueur du mot de passe.
         * @return La contribution au score des types de caractères distincts.
         */
        private int calculateDistinctCharacterTypesBonus(final int typesCount, final int length) {
            if (typesCount == 1 && length >= 8) { return -5; } // Penalty if only one type, even if long
            if (typesCount == 2) { return 7; }
            if (typesCount == 3) { return 12; }
            if (typesCount == 4) { return 18; }
            return 0;
        }

        /**
         * Applies penalties to the score based on common password weaknesses like sequences, weak words, and repetitions.
         * Applique des pénalités au score en fonction des faiblesses courantes des mots de passe
         * comme les séquences, les mots faibles et les répétitions.
         * @param password The password string.
         * @param currentScore The current score before applying penalties.
         * @return The updated score after applying penalties.
         * @param password La chaîne du mot de passe.
         * @param currentScore Le score actuel avant l'application des pénalités.
         * @return Le score mis à jour après l'application des pénalités.
         */
        private int applyPenalties(final String password, int currentScore) {
            final String passwordLower = password.toLowerCase();
            final int length = password.length();

            // Penalty for common sequences (letters or numbers)
            boolean sequenceFound = false;
            for (int i = 0; i < COMMON_SEQUENCES_LOWER.length; i++) {
                final String seq = COMMON_SEQUENCES_LOWER[i];
                if (passwordLower.contains(seq)) {
                    currentScore -= 7;
                    sequenceFound = true;
                    break;
                }
            }
            if (!sequenceFound) {
                for (int i = 0; i < COMMON_SEQUENCES_NUM.length; i++) {
                    final String seq = COMMON_SEQUENCES_NUM[i];
                    if (password.contains(seq)) {
                        currentScore -= 7;
                        break;
                    }
                }
            }

            // Penalty for common weak words
            for (int i = 0; i < COMMON_WEAK_WORDS.length; i++) {
                final String weakWord = COMMON_WEAK_WORDS[i];
                if (passwordLower.contains(weakWord)) {
                    currentScore -= 12;
                    break;
                }
            }

            // Penalty for excessive character repetition (3+ consecutive identical chars)
            for (int i = 0; i < length - 2; i++) {
                if (password.charAt(i) == password.charAt(i + 1) &&
                    password.charAt(i + 1) == password.charAt(i + 2)) {
                    currentScore -= 6;
                    break;
                }
            }

            // Penalty for overall character repetition (if one char is > 1/3 of password)
            if (length > 5) {
                final Map<Character, Integer> charCounts = new HashMap<Character, Integer>();
                for (int i = 0; i < password.length(); i++) {
                    final char c = password.charAt(i);
                    // Java 6 compatible way to update map count. Autoboxing handles Integer conversion.
                    if (charCounts.containsKey(c)) {
                        charCounts.put(c, charCounts.get(c) + 1);
                    } else {
                        charCounts.put(c, 1);
                    }
                }
                // Iterate over Map.EntrySet as direct for-each over Map is Java 8+
                final java.util.Iterator<Map.Entry<Character, Integer>> it = charCounts.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry<Character, Integer> entry = it.next();
                    if (entry.getValue().intValue() > length / 3) {
                        currentScore -= (entry.getValue().intValue() - (length / 3)) * 3;
                    }
                }
            }
            return currentScore;
        }
    }

    /**
     * Constructor for PasswordGeneratorApp.
     * Initializes the password service and history, then builds the UI.
     * Constructeur de PasswordGeneratorApp.
     * Initialise le service de mots de passe et l'historique, puis construit l'interface utilisateur.
     */
    public PasswordGeneratorApp() {
        this.passwordService = new PasswordService();
        this.passwordHistory = new ArrayList<String>(); // Initialize history
        initializeUI();
    }

    /**
     * Initializes and configures all UI components.
     * Applies a modern visual theme and attractive colors.
     * Initialise et configure tous les composants de l'interface utilisateur.
     * Applique un thème visuel moderne et des couleurs attrayantes.
     */
    private void initializeUI() {
        setupFrame();
        final JPanel mainPanel = createMainPanel();
        mainPanel.add(createTitleLabel(), BorderLayout.NORTH);
        mainPanel.add(createOptionsPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Sets up the main JFrame properties.
     * Configure les propriétés de la JFrame principale.
     */
    private void setupFrame() {
        try {
            // Attempt to set Nimbus Look and Feel for a more modern appearance
            for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (final Exception e) {
            System.err.println(NIMBUS_LOOK_AND_FEEL_ERROR + e.getMessage());
        }

        frame = new JFrame(APP_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Center on screen
    }

    /**
     * Creates the main content panel with a custom gradient background.
     * Crée le panneau de contenu principal avec un fond dégradé personnalisé.
     * @return The configured JPanel.
     * @return Le JPanel configuré.
     */
    private JPanel createMainPanel() {
        final JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);
                final Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                final GradientPaint gp = new GradientPaint(0, 0, MAIN_GRADIENT_START, 0, getHeight(), MAIN_GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        return mainPanel;
    }

    /**
     * Creates the title label for the application.
     * Crée le titre de l'application.
     * @return The configured JLabel.
     * @return Le JLabel configuré.
     */
    private JLabel createTitleLabel() {
        final JLabel titleLabel = new JLabel("Générateur de Mots de Passe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30));
        titleLabel.setForeground(TITLE_COLOR);
        return titleLabel;
    }

    /**
     * Creates the panel containing password generation options (length, char types, exclusions).
     * Crée le panneau contenant les options de génération de mot de passe (longueur, types de caractères, exclusions).
     * @return The configured JPanel.
     * @return Le JPanel configuré.
     */
    private JPanel createOptionsPanel() {
        final JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setOpaque(false);
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(INSETS_VERTICAL, INSETS_HORIZONTAL, INSETS_VERTICAL, INSETS_HORIZONTAL);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Length Slider Section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        final JLabel lengthLabelText = new JLabel(LENGTH_LABEL_TEXT, SwingConstants.RIGHT);
        styleLabel(lengthLabelText);
        optionsPanel.add(lengthLabelText, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        lengthSlider = new JSlider(JSlider.HORIZONTAL, SLIDER_MIN_LENGTH, SLIDER_MAX_LENGTH, SLIDER_DEFAULT_LENGTH);
        styleSlider(lengthSlider);
        optionsPanel.add(lengthSlider, gbc);

        // Checkboxes Section
        upperCaseCheckBox = createStyledCheckBox(UPPERCASE_CHECKBOX_TEXT, true);
        lowerCaseCheckBox = createStyledCheckBox(LOWERCASE_CHECKBOX_TEXT, true);
        numbersCheckBox = createStyledCheckBox(NUMBERS_CHECKBOX_TEXT, true);
        symbolsCheckBox = createStyledCheckBox(SYMBOLS_CHECKBOX_TEXT, true);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        optionsPanel.add(upperCaseCheckBox, gbc);

        gbc.gridy = 2;
        optionsPanel.add(lowerCaseCheckBox, gbc);

        gbc.gridy = 3;
        optionsPanel.add(numbersCheckBox, gbc);

        gbc.gridy = 4;
        optionsPanel.add(symbolsCheckBox, gbc);

        // Exclude Characters Field Section
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        final JLabel excludeLabel = new JLabel(EXCLUDE_CHARS_LABEL_TEXT, SwingConstants.RIGHT);
        styleLabel(excludeLabel);
        optionsPanel.add(excludeLabel, gbc);

        excludeCharsField = new JTextField(15);
        excludeCharsField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        excludeCharsField.setBackground(EXCLUDE_FIELD_BG);
        excludeCharsField.setForeground(EXCLUDE_FIELD_TEXT_COLOR);
        excludeCharsField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EXCLUDE_FIELD_BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 5;
        optionsPanel.add(excludeCharsField, gbc);

        // Error Label Section
        charSetErrorLabel = new JLabel(" "); // Placeholder for error messages
        charSetErrorLabel.setFont(new Font("Inter", Font.ITALIC, 13));
        charSetErrorLabel.setForeground(ERROR_TEXT_COLOR);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, INSETS_HORIZONTAL, 5, INSETS_HORIZONTAL);
        optionsPanel.add(charSetErrorLabel, gbc);

        return optionsPanel;
    }

    /**
     * Creates the bottom panel containing the password display, strength info, and action buttons.
     * Crée le panneau inférieur contenant l'affichage du mot de passe, les informations de force et les boutons d'action.
     * @return The configured JPanel.
     * @return Le JPanel configuré.
     */
    private JPanel createBottomPanel() {
        final JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        final GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.insets = new Insets(INSETS_VERTICAL, INSETS_VERTICAL, INSETS_VERTICAL, INSETS_VERTICAL);
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;

        // Password Display Field
        passwordDisplayField = new JTextField(35);
        passwordDisplayField.setEditable(true); // Enabled for real-time strength feedback
        passwordDisplayField.setFont(new Font("Monospaced", Font.BOLD, 22));
        passwordDisplayField.setBackground(PASSWORD_FIELD_BG);
        passwordDisplayField.setForeground(PASSWORD_FIELD_TEXT_COLOR);
        passwordDisplayField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PASSWORD_FIELD_BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        passwordDisplayField.setHorizontalAlignment(JTextField.CENTER);
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 0;
        gbcBottom.gridwidth = 3; // Spans across all three columns for buttons below
        gbcBottom.weightx = 1.0;
        bottomPanel.add(passwordDisplayField, gbcBottom);

        // Add DocumentListener for real-time strength feedback
        passwordDisplayField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(final DocumentEvent e) { updateStrengthFromField(); }
            public void removeUpdate(final DocumentEvent e) { updateStrengthFromField(); }
            public void insertUpdate(final DocumentEvent e) { updateStrengthFromField(); }

            private void updateStrengthFromField() {
                final String currentPassword = passwordDisplayField.getText();
                final PasswordEvaluationResult result = passwordService.evaluatePasswordStrength(currentPassword);
                updateStrengthLabel(result.strengthLevel, result.entropy);
            }
        });


        // Password Strength Label
        strengthLabel = new JLabel(STRENGTH_LABEL_PREFIX + PasswordStrengthLevel.EMPTY.getDisplayName(), SwingConstants.CENTER);
        strengthLabel.setFont(new Font("Inter", Font.BOLD, 16));
        strengthLabel.setForeground(PasswordStrengthLevel.EMPTY.getDisplayColor());
        gbcBottom.gridy = 1;
        gbcBottom.insets = new Insets(8, INSETS_VERTICAL, 0, INSETS_VERTICAL);
        bottomPanel.add(strengthLabel, gbcBottom);

        // Entropy Label
        entropyLabel = new JLabel(ENTROPY_LABEL_PREFIX + "0.00 bits", SwingConstants.CENTER);
        entropyLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        entropyLabel.setForeground(ENTROPY_LABEL_COLOR);
        gbcBottom.gridy = 2;
        gbcBottom.insets = new Insets(0, INSETS_VERTICAL, 12, INSETS_VERTICAL);
        bottomPanel.add(entropyLabel, gbcBottom);


        // Buttons Section
        generateButton = createStyledButton(GENERATE_BUTTON_TEXT);
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) { handleGeneratePassword(); }
        });
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 3;
        gbcBottom.gridwidth = 1;
        gbcBottom.weightx = 0.33;
        gbcBottom.insets = new Insets(15, INSETS_VERTICAL, 0, 5); // More top margin
        bottomPanel.add(generateButton, gbcBottom);

        copyButton = createStyledButton(COPY_BUTTON_TEXT);
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) { handleCopyPassword(); }
        });
        gbcBottom.gridx = 1;
        gbcBottom.gridy = 3;
        gbcBottom.weightx = 0.33;
        gbcBottom.insets = new Insets(15, 5, 0, 5);
        bottomPanel.add(copyButton, gbcBottom);

        showHistoryButton = createStyledButton(HISTORY_BUTTON_TEXT);
        showHistoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) { showPasswordHistory(); }
        });
        gbcBottom.gridx = 2;
        gbcBottom.gridy = 3;
        gbcBottom.weightx = 0.34;
        gbcBottom.insets = new Insets(15, 5, 0, INSETS_VERTICAL);
        bottomPanel.add(showHistoryButton, gbcBottom);

        return bottomPanel;
    }

    /**
     * Styles a generic JLabel component with consistent font and color.
     * Style un composant JLabel générique avec une police et une couleur cohérentes.
     * @param label The JLabel to style.
     * @param label Le JLabel à styliser.
     */
    private void styleLabel(final JLabel label) {
        label.setForeground(LABEL_COLOR);
        label.setFont(new Font("Inter", Font.PLAIN, 16));
    }

    /**
     * Styles a JSlider component for consistent appearance and behavior.
     * Style un composant JSlider pour une apparence et un comportement cohérents.
     * @param slider The JSlider to style.
     * @param slider Le JSlider à styliser.
     */
    private void styleSlider(final JSlider slider) {
        slider.setMajorTickSpacing(4);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setOpaque(false);
        slider.setForeground(LABEL_COLOR); // Ticks and labels color
        slider.setFont(new Font("Inter", Font.PLAIN, 12));
    }

    /**
     * Creates and styles a JCheckBox with consistent appearance.
     * Crée et stylise une JCheckBox avec une apparence cohérente.
     * @param text The text for the checkbox.
     * @param selected The initial selected state.
     * @return The styled JCheckBox.
     * @param text Le texte de la case à cocher.
     * @param selected L'état initial sélectionné.
     * @return La JCheckBox stylisée.
     */
    private JCheckBox createStyledCheckBox(final String text, final boolean selected) {
        final JCheckBox checkBox = new JCheckBox(text, selected);
        checkBox.setOpaque(false);
        checkBox.setForeground(LABEL_COLOR);
        checkBox.setFont(new Font("Inter", Font.PLAIN, 16));
        checkBox.setFocusPainted(false);
        return checkBox;
    }

    /**
     * Creates and styles a JButton with consistent appearance and hover effects.
     * Crée et stylise un JButton avec une apparence et des effets de survol cohérents.
     * @param text The text for the button.
     * @return The styled JButton.
     * @param text Le texte du bouton.
     * @return Le JButton stylisé.
     */
    private JButton createStyledButton(final String text) {
        final JButton button = new JButton(text);

        button.setFont(new Font("Inter", Font.BOLD, 16));
        button.setBackground(BUTTON_NORMAL_BG);
        button.setForeground(BUTTON_FOREGROUND_COLOR);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final Border lineBorder = BorderFactory.createLineBorder(BUTTON_BORDER_COLOR, 1);
        final Border emptyBorder = BorderFactory.createEmptyBorder(12, 22, 12, 22);
        button.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_BG);
            }

            @Override
            public void mouseExited(final MouseEvent evt) {
                button.setBackground(BUTTON_NORMAL_BG);
            }
        });
        return button;
    }

    /**
     * Handles the password generation request.
     * Retrieves options from the UI, calls the PasswordService, and updates the display.
     * Gère la requête de génération de mot de passe.
     * Récupère les options de l'interface utilisateur, appelle le PasswordService et met à jour l'affichage.
     */
    private void handleGeneratePassword() {
        charSetErrorLabel.setText(" "); // Clear previous error

        final boolean useUpperCase = upperCaseCheckBox.isSelected();
        final boolean useLowerCase = lowerCaseCheckBox.isSelected();
        final boolean useNumbers = numbersCheckBox.isSelected();
        final boolean useSymbols = symbolsCheckBox.isSelected();
        final String excludeChars = excludeCharsField.getText(); // Get excluded characters from UI

        if (!useUpperCase && !useLowerCase && !useNumbers && !useSymbols) {
            charSetErrorLabel.setText(ERROR_NO_CHARSET_SELECTED);
            passwordDisplayField.setText("");
            updateStrengthLabel(PasswordStrengthLevel.EMPTY, 0.0);
            return;
        }

        final int length = lengthSlider.getValue();
        final String generatedPassword = passwordService.generatePassword(length, useUpperCase, useLowerCase, useNumbers, useSymbols, excludeChars);

        if (generatedPassword != null) {
            passwordDisplayField.setText(generatedPassword);
            passwordHistory.add(0, generatedPassword); // Add to history (most recent first)
            if (passwordHistory.size() > PASSWORD_HISTORY_MAX_SIZE) { // Keep history limited
                passwordHistory.remove(passwordHistory.size() - 1);
            }
            final PasswordEvaluationResult result = passwordService.evaluatePasswordStrength(generatedPassword);
            updateStrengthLabel(result.strengthLevel, result.entropy);
        } else {
            passwordDisplayField.setText("");
            updateStrengthLabel(PasswordStrengthLevel.EMPTY, 0.0);
             JOptionPane.showMessageDialog(frame,
                ERROR_INTERNAL_GEN_MESSAGE,
                ERROR_INTERNAL_GEN_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the strength label text and color based on the evaluated strength, and updates entropy label.
     * Met à jour le texte et la couleur du label de force en fonction de la force évaluée, et met à jour le label d'entropie.
     * @param strengthLevel The evaluated password strength.
     * @param entropy The calculated entropy in bits.
     * @param strengthLevel La force évaluée du mot de passe.
     * @param entropy L'entropie calculée en bits.
     */
    private void updateStrengthLabel(final PasswordStrengthLevel strengthLevel, final double entropy) {
        final PasswordStrengthLevel displayStrength = (strengthLevel == null) ? PasswordStrengthLevel.EMPTY : strengthLevel;

        strengthLabel.setText(STRENGTH_LABEL_PREFIX + displayStrength.getDisplayName());
        strengthLabel.setForeground(displayStrength.getDisplayColor());
        entropyLabel.setText(ENTROPY_LABEL_PREFIX + String.format("%.2f", entropy) + " bits");
    }

    /**
     * Handles copying the displayed password to the system clipboard.
     * Gère la copie du mot de passe affiché dans le presse-papiers du système.
     */
    private void handleCopyPassword() {
        final String password = passwordDisplayField.getText();
        if (password != null && !password.isEmpty()) {
            try {
                final StringSelection stringSelection = new StringSelection(password);
                final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(frame,
                        COPY_SUCCESS_MESSAGE,
                        COPY_SUCCESS_TITLE,
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (final Exception ex) {
                // Log detailed error for developers
                System.err.println(COPY_ERROR_MESSAGE_PREFIX + ex.getMessage());
                ex.printStackTrace();
                // User-friendly message
                JOptionPane.showMessageDialog(frame,
                        COPY_ERROR_MESSAGE_PREFIX + ex.getMessage(),
                        COPY_ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    NO_PASSWORD_TO_COPY_MESSAGE,
                    NO_PASSWORD_TO_COPY_TITLE,
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Displays the history of generated passwords in a new modal dialog.
     * Affiche l'historique des mots de passe générés dans une nouvelle boîte de dialogue modale.
     */
    private void showPasswordHistory() {
        if (passwordHistory.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    HISTORY_EMPTY_MESSAGE,
                    HISTORY_EMPTY_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final JDialog historyDialog = new JDialog(frame, HISTORY_DIALOG_TITLE, true); // Modal dialog
        historyDialog.setSize(HISTORY_DIALOG_WIDTH, HISTORY_DIALOG_HEIGHT);
        historyDialog.setLocationRelativeTo(frame);
        historyDialog.setLayout(new BorderLayout());

        final JTextArea historyDisplay = new JTextArea();
        historyDisplay.setEditable(false);
        historyDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        historyDisplay.setBackground(PASSWORD_FIELD_BG); // Reusing password field's dark background
        historyDisplay.setForeground(EXCLUDE_FIELD_TEXT_COLOR); // Reusing exclude field's light text color
        historyDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final StringBuilder historyText = new StringBuilder();
        for (int i = 0; i < passwordHistory.size(); i++) {
            historyText.append((i + 1)).append(". ").append(passwordHistory.get(i)).append("\n");
        }
        historyDisplay.setText(historyText.toString());

        final JScrollPane scrollPane = new JScrollPane(historyDisplay);
        historyDialog.add(scrollPane, BorderLayout.CENTER);

        final JButton closeButton = createStyledButton(CLOSE_BUTTON_TEXT);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                historyDialog.dispose();
            }
        });
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Transparent background
        buttonPanel.add(closeButton);
        historyDialog.add(buttonPanel, BorderLayout.SOUTH);

        historyDialog.setVisible(true);
    }

    /**
     * Main method to launch the application.
     * Ensures UI operations are done on the Event Dispatch Thread.
     * Méthode principale pour lancer l'application.
     * Assure que les opérations de l'interface utilisateur sont exécutées sur le
     * Event Dispatch Thread (EDT).
     * @param args Command line arguments (not used).
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PasswordGeneratorApp();
            }
        });
    }
}