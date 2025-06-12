import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; // Explicitly import ActionListener
import java.awt.event.MouseAdapter; // Used for button hover effects
import java.awt.event.MouseEvent;  // Used for button hover effects
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
 * Key features:
 * - Customizable password length and character sets (uppercase, lowercase, numbers, symbols).
 * - Generation of passwords with at least one character from each selected set.
 * - Password strength evaluation (Weak, Medium, Strong, Very Strong).
 * - Visual feedback for password strength.
 * - Option to copy generated passwords to the clipboard.
 *
 * Designed for Java 6 compatibility.
 */
public class PasswordGeneratorApp {

    // --- UI Components ---
    private JFrame frame;
    private JSlider lengthSlider;
    private JCheckBox upperCaseCheckBox;
    private JCheckBox lowerCaseCheckBox;
    private JCheckBox numbersCheckBox;
    private JCheckBox symbolsCheckBox;
    private JTextField passwordDisplayField;
    private JButton generateButton;
    private JButton copyButton;
    private JLabel strengthLabel;
    private JLabel charSetErrorLabel; // For displaying character set selection errors

    // --- Service for password logic ---
    private final PasswordService passwordService;

    /**
     * Represents the evaluated strength of a password.
     */
    public enum PasswordStrengthLevel {
        EMPTY("Vide", Color.WHITE), // Should be "N/A" or similar if empty means not yet evaluated
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

        public String getDisplayName() {
            return displayName;
        }

        public Color getDisplayColor() {
            return displayColor;
        }
    }

    /**
     * Handles password generation and strength evaluation logic.
     * This class is designed to be testable and independent of the UI.
     */
    private static class PasswordService {
        private final SecureRandom secureRandom;

        // Character sets for password generation
        private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
        private static final String NUMBERS_CHARS = "0123456789";
        private static final String SYMBOLS_CHARS = "!@#$%^&*()_-+=<>?/{}[]|";

        // Constants for password strength evaluation
        private static final int SCORE_THRESHOLD_VERY_STRONG = 45;
        private static final int SCORE_THRESHOLD_STRONG = 30;
        private static final int SCORE_THRESHOLD_MEDIUM = 15;

        // Lists for strength evaluation penalties
        private static final String[] COMMON_SEQUENCES_LOWER = {
            "abc", "bcd", "cde", "def", "efg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "mno", "nop", "opq", "pqr", "qrs", "rst", "stu", "tuv", "uvw", "vwx", "wxy", "xyz",
            "qwe", "wer", "ert", "rty", "tyu", "yui", "uio", "iop",
            "asd", "sdf", "dfg", "fgh", "ghj", "hjk", "jkl",
            "zxc", "xcv", "cvb", "vbn", "bnm"
        };
        private static final String[] COMMON_SEQUENCES_NUM = {"123", "234", "345", "456", "567", "678", "789", "890", "098", "987", "876", "765", "654", "543", "432", "321"};
        private static final String[] COMMON_WEAK_WORDS = {"password", "pass", "admin", "administrator", "user", "username", "login", "logon", "guest", "test", "secret", "qwerty", "azerty", "12345", "123456", "1234567", "12345678", "123456789", "root", "support", "service", "welcome", "example", "demo", "changeme"};


        /**
         * Constructs a PasswordService.
         */
        public PasswordService() {
            this.secureRandom = new SecureRandom();
        }

        /**
         * Generates a password based on the specified criteria.
         *
         * @param length       The desired length of the password.
         * @param useUpperCase Whether to include uppercase letters.
         * @param useLowerCase Whether to include lowercase letters.
         * @param useNumbers   Whether to include numbers.
         * @param useSymbols   Whether to include symbols.
         * @return The generated password, or null if no character types are selected.
         */
        public String generatePassword(int length, boolean useUpperCase, boolean useLowerCase, boolean useNumbers, boolean useSymbols) {
            if (!useUpperCase && !useLowerCase && !useNumbers && !useSymbols) {
                return null; // Or throw IllegalArgumentException
            }

            StringBuilder charPool = new StringBuilder();
            List<Character> requiredChars = new ArrayList<Character>();

            if (useUpperCase) {
                charPool.append(UPPERCASE_CHARS);
                requiredChars.add(UPPERCASE_CHARS.charAt(secureRandom.nextInt(UPPERCASE_CHARS.length())));
            }
            if (useLowerCase) {
                charPool.append(LOWERCASE_CHARS);
                requiredChars.add(LOWERCASE_CHARS.charAt(secureRandom.nextInt(LOWERCASE_CHARS.length())));
            }
            if (useNumbers) {
                charPool.append(NUMBERS_CHARS);
                requiredChars.add(NUMBERS_CHARS.charAt(secureRandom.nextInt(NUMBERS_CHARS.length())));
            }
            if (useSymbols) {
                charPool.append(SYMBOLS_CHARS);
                requiredChars.add(SYMBOLS_CHARS.charAt(secureRandom.nextInt(SYMBOLS_CHARS.length())));
            }

            if (charPool.length() == 0) { // Should be caught by the initial check
                return null;
            }

            // Ensure the requested length is not less than the number of required character types
            int actualLength = Math.max(length, requiredChars.size());

            StringBuilder password = new StringBuilder(actualLength);

            // Fill the password with characters from the pool, leaving space for required characters
            for (int i = 0; i < actualLength - requiredChars.size(); i++) {
                password.append(charPool.charAt(secureRandom.nextInt(charPool.length())));
            }

            // Insert required characters at random positions
            for (Character c : requiredChars) {
                if (password.length() < actualLength) { // Should always be true if logic is correct
                     password.insert(secureRandom.nextInt(password.length() + 1), c);
                } else if (password.length() == actualLength && !requiredChars.isEmpty()) {
                    // If password is full but we still have required chars (e.g. length was too small)
                    // Replace a random char. This case implies length < requiredChars.size() initially.
                    // The Math.max should prevent this, but as a safeguard:
                    int randomIndex = secureRandom.nextInt(password.length());
                    password.setCharAt(randomIndex, c);
                }
            }

            // Shuffle the final password string to ensure random distribution of required characters
            List<Character> passwordChars = new ArrayList<Character>();
            for (int i = 0; i < password.length(); i++) {
                passwordChars.add(password.charAt(i));
            }
            Collections.shuffle(passwordChars, secureRandom);

            StringBuilder finalPassword = new StringBuilder(actualLength);
            for (Character ch : passwordChars) {
                finalPassword.append(ch);
            }

            // Ensure final password is exactly actualLength
            if (finalPassword.length() > actualLength) {
                return finalPassword.substring(0, actualLength);
            }
            // This part should ideally not be needed if logic above is perfect
            while (finalPassword.length() < actualLength && charPool.length() > 0) {
                 finalPassword.append(charPool.charAt(secureRandom.nextInt(charPool.length())));
            }

            return finalPassword.toString();
        }

        /**
         * Evaluates the strength of a given password.
         *
         * @param password The password to evaluate.
         * @return A {@link PasswordStrengthLevel} enum value indicating the password's strength.
         */
        public PasswordStrengthLevel evaluatePasswordStrength(String password) {
            if (password == null || password.isEmpty()) {
                return PasswordStrengthLevel.EMPTY;
            }

            int score = 0;
            int length = password.length();

            if (length < 8) {
                return PasswordStrengthLevel.WEAK; // Too short is always weak
            }

            // Character type presence
            boolean hasLowerCase = false;
            boolean hasUpperCase = false;
            boolean hasDigit = false;
            boolean hasSymbol = false;

            for (char c : password.toCharArray()) {
                if (Character.isLowerCase(c)) hasLowerCase = true;
                else if (Character.isUpperCase(c)) hasUpperCase = true;
                else if (Character.isDigit(c)) hasDigit = true;
                else if (SYMBOLS_CHARS.indexOf(c) != -1) hasSymbol = true;
            }

            if (!hasLowerCase && !hasUpperCase && !hasDigit && !hasSymbol) { // Should not happen with generator
                 return PasswordStrengthLevel.WEAK;
            }

            // Minimum requirement: at least lowercase or uppercase for not being weak
            if (!hasLowerCase && !hasUpperCase) {
                return PasswordStrengthLevel.WEAK;
            }


            // 1. Score based on length
            score += calculateLengthScore(length);

            // 2. Score based on presence of character types
            if (hasLowerCase) score += 5;
            if (hasUpperCase) score += 8;
            if (hasDigit) score += 8;
            if (hasSymbol) score += 12;

            // 3. Bonus for number of distinct character types
            int typesCount = 0;
            if (hasLowerCase) typesCount++;
            if (hasUpperCase) typesCount++;
            if (hasDigit) typesCount++;
            if (hasSymbol) typesCount++;

            score += calculateDistinctCharacterTypesBonus(typesCount, length);

            // 4. Apply penalties
            score = applyPenalties(password, score);


            // 5. Final categorization based on score and character types
            if (typesCount == 4 && score >= SCORE_THRESHOLD_VERY_STRONG) {
                return PasswordStrengthLevel.VERY_STRONG;
            } else if (typesCount >= 3 && score >= SCORE_THRESHOLD_STRONG) {
                return PasswordStrengthLevel.STRONG;
            } else if (typesCount >= 2 && score >= SCORE_THRESHOLD_MEDIUM) {
                return PasswordStrengthLevel.MEDIUM;
            } else {
                return PasswordStrengthLevel.WEAK;
            }
        }

        private int calculateLengthScore(int length) {
            if (length >= 8 && length <= 9) return -5; // Slight penalty for bare minimum
            if (length >= 10 && length <= 12) return 10;
            if (length >= 13 && length <= 15) return 15;
            if (length >= 16 && length <= 20) return 20;
            if (length > 20) return 25;
            return 0; // Should be caught by length < 8 check earlier
        }

        private int calculateDistinctCharacterTypesBonus(int typesCount, int length) {
            if (typesCount == 1 && length >= 8) return -5; // Penalty if only one type (even if long)
            if (typesCount == 2) return 7;
            if (typesCount == 3) return 12;
            if (typesCount == 4) return 18;
            return 0;
        }

        private int applyPenalties(String password, int currentScore) {
            int score = currentScore;
            String passwordLower = password.toLowerCase();
            int length = password.length();

            // Penalty for common sequences (letters or numbers)
            boolean sequenceFound = false;
            for (String seq : COMMON_SEQUENCES_LOWER) {
                if (passwordLower.contains(seq)) {
                    score -= 7; // Increased penalty
                    sequenceFound = true;
                    break;
                }
            }
            if (!sequenceFound) {
                for (String seq : COMMON_SEQUENCES_NUM) {
                    if (password.contains(seq)) {
                        score -= 7; // Increased penalty
                        break;
                    }
                }
            }

            // Penalty for common weak words
            for (String weakWord : COMMON_WEAK_WORDS) {
                if (passwordLower.contains(weakWord)) {
                    score -= 12; // Increased penalty
                    break;
                }
            }

            // Penalty for excessive character repetition (3+ consecutive identical chars)
            for (int i = 0; i < length - 2; i++) {
                if (password.charAt(i) == password.charAt(i + 1) &&
                    password.charAt(i + 1) == password.charAt(i + 2)) {
                    score -= 6; // Increased penalty
                    break;
                }
            }

            // Penalty for overall character repetition (if one char is > 1/3 of password)
            if (length > 5) {
                Map<Character, Integer> charCounts = new HashMap<Character, Integer>();
                for (char c : password.toCharArray()) {
                    // Java 6 compatible way to update map count
                    if (charCounts.containsKey(c)) {
                        charCounts.put(c, charCounts.get(c) + 1);
                    } else {
                        charCounts.put(c, 1);
                    }
                }
                for (Map.Entry<Character, Integer> entry : charCounts.entrySet()) {
                    if (entry.getValue() > length / 3) {
                        score -= (entry.getValue() - (length / 3)) * 3;
                    }
                }
            }
            return score;
        }
    }

    /**
     * Constructor for PasswordGeneratorApp.
     * Initializes the password service and builds the UI.
     */
    public PasswordGeneratorApp() {
        this.passwordService = new PasswordService();
        initializeUI();
    }

    /**
     * Initializes and configures all UI components.
     * Applies a modern visual theme and attractive colors.
     */
    private void initializeUI() {
        try {
            // Attempt to set Nimbus Look and Feel for a more modern appearance
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, the default L&F will be used.
            // Log this for debugging, but don't show a disruptive error to the user.
            System.err.println("Nimbus Look and Feel not found. Using default L&F. Error: " + e.getMessage());
        }

        frame = new JFrame("Générateur de Mots de Passe Ultra Sécurisé");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(680, 550); // Slightly increased size for better layout
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Center on screen

        // Main panel with custom gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color startColor = new Color(40, 20, 60); // Dark purple
                Color endColor = new Color(25, 15, 35);   // Darker purple
                GradientPaint gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title Label
        JLabel titleLabel = new JLabel("Générateur de Mots de Passe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30)); // Using "Inter" as requested
        titleLabel.setForeground(new Color(255, 150, 200)); // Light pink
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Options Panel (center)
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setOpaque(false); // Transparent to show main panel's gradient
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 18, 12, 18); // Adjusted insets
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;


        // Length Slider
        JLabel lengthLabelText = new JLabel("Longueur (8-32) :", SwingConstants.RIGHT); // Renamed to avoid conflict
        styleLabel(lengthLabelText);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3; // Give some weight for alignment
        optionsPanel.add(lengthLabelText, gbc);

        lengthSlider = new JSlider(JSlider.HORIZONTAL, 8, 32, 16); // Default length 16
        styleSlider(lengthSlider);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        optionsPanel.add(lengthSlider, gbc);

        // Checkboxes for character types
        upperCaseCheckBox = createStyledCheckBox("Majuscules (A-Z)", true);
        lowerCaseCheckBox = createStyledCheckBox("Minuscules (a-z)", true);
        numbersCheckBox = createStyledCheckBox("Nombres (0-9)", true);
        symbolsCheckBox = createStyledCheckBox("Symboles (!@#$)", true);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span across two columns
        optionsPanel.add(upperCaseCheckBox, gbc);

        gbc.gridy = 2;
        optionsPanel.add(lowerCaseCheckBox, gbc);

        gbc.gridy = 3;
        optionsPanel.add(numbersCheckBox, gbc);

        gbc.gridy = 4;
        optionsPanel.add(symbolsCheckBox, gbc);

        // Error label for character set selection
        charSetErrorLabel = new JLabel(" "); // Placeholder for error messages
        charSetErrorLabel.setFont(new Font("Inter", Font.ITALIC, 13));
        charSetErrorLabel.setForeground(new Color(255, 90, 90)); // Red for errors
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 18, 5, 18); // Less top inset
        optionsPanel.add(charSetErrorLabel, gbc);


        mainPanel.add(optionsPanel, BorderLayout.CENTER);

        // Bottom Panel (password display, strength, buttons)
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.insets = new Insets(10, 10, 10, 10);
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;

        // Password Display Field
        passwordDisplayField = new JTextField(30); // Increased default columns
        passwordDisplayField.setEditable(false);
        passwordDisplayField.setFont(new Font("Monospaced", Font.BOLD, 22));
        passwordDisplayField.setBackground(new Color(10, 10, 15)); // Very dark background
        passwordDisplayField.setForeground(new Color(100, 255, 100)); // Green text
        passwordDisplayField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 100, 255), 1), // Purple border
                BorderFactory.createEmptyBorder(10, 12, 10, 12) // Padding
        ));
        passwordDisplayField.setHorizontalAlignment(JTextField.CENTER);
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 0;
        gbcBottom.gridwidth = 2;
        gbcBottom.weightx = 1.0;
        bottomPanel.add(passwordDisplayField, gbcBottom);

        // Password Strength Label
        strengthLabel = new JLabel("Force: N/A", SwingConstants.CENTER);
        strengthLabel.setFont(new Font("Inter", Font.BOLD, 16));
        strengthLabel.setForeground(PasswordStrengthLevel.EMPTY.getDisplayColor());
        gbcBottom.gridy = 1;
        gbcBottom.insets = new Insets(8, 10, 12, 10); // Adjusted insets
        bottomPanel.add(strengthLabel, gbcBottom);

        // Generate Button
        generateButton = createStyledButton("Générer Mot de Passe");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleGeneratePassword();
            }
        });
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 2;
        gbcBottom.gridwidth = 1;
        gbcBottom.weightx = 0.5;
        gbcBottom.insets = new Insets(15, 10, 0, 5); // More top margin
        bottomPanel.add(generateButton, gbcBottom);

        // Copy Button
        copyButton = createStyledButton("Copier");
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCopyPassword();
            }
        });
        gbcBottom.gridx = 1;
        gbcBottom.gridy = 2;
        gbcBottom.weightx = 0.5;
        gbcBottom.insets = new Insets(15, 5, 0, 10);
        bottomPanel.add(copyButton, gbcBottom);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Styles a generic JLabel component.
     * @param label The JLabel to style.
     */
    private void styleLabel(JLabel label) {
        label.setForeground(new Color(200, 180, 220)); // Light lavender
        label.setFont(new Font("Inter", Font.PLAIN, 16));
    }

    /**
     * Styles a JSlider component.
     * @param slider The JSlider to style.
     */
    private void styleSlider(JSlider slider) {
        slider.setMajorTickSpacing(4);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setOpaque(false);
        slider.setForeground(new Color(200, 180, 220));
        slider.setFont(new Font("Inter", Font.PLAIN, 12));
    }

    /**
     * Creates and styles a JCheckBox.
     * @param text The text for the checkbox.
     * @param selected The initial selected state.
     * @return The styled JCheckBox.
     */
    private JCheckBox createStyledCheckBox(String text, boolean selected) {
        JCheckBox checkBox = new JCheckBox(text, selected);
        checkBox.setOpaque(false);
        checkBox.setForeground(new Color(200, 180, 220));
        checkBox.setFont(new Font("Inter", Font.PLAIN, 16));
        checkBox.setFocusPainted(false);
        return checkBox;
    }

    /**
     * Creates and styles a JButton with hover effects.
     * @param text The text for the button.
     * @return The styled JButton.
     */
    private JButton createStyledButton(String text) {
        final JButton button = new JButton(text); // Make button effectively final for use in MouseAdapter
        final Color normalBg = new Color(120, 60, 180); // Purple
        final Color hoverBg = new Color(150, 90, 210);  // Lighter purple
        final Color borderColor = new Color(90, 40, 150); // Darker purple

        button.setFont(new Font("Inter", Font.BOLD, 16));
        button.setBackground(normalBg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Border lineBorder = BorderFactory.createLineBorder(borderColor, 1);
        Border emptyBorder = BorderFactory.createEmptyBorder(12, 22, 12, 22); // More padding
        button.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(normalBg);
            }
        });
        return button;
    }

    /**
     * Handles the password generation request.
     * Retrieves options from the UI, calls the PasswordService, and updates the display.
     */
    private void handleGeneratePassword() {
        charSetErrorLabel.setText(" "); // Clear previous error

        boolean useUpperCase = upperCaseCheckBox.isSelected();
        boolean useLowerCase = lowerCaseCheckBox.isSelected();
        boolean useNumbers = numbersCheckBox.isSelected();
        boolean useSymbols = symbolsCheckBox.isSelected();

        if (!useUpperCase && !useLowerCase && !useNumbers && !useSymbols) {
            charSetErrorLabel.setText("Veuillez sélectionner au moins un type de caractère.");
            passwordDisplayField.setText("");
            updateStrengthLabel(PasswordStrengthLevel.EMPTY);
            // Optionally, show a dialog as well, but inline error is often better
            // JOptionPane.showMessageDialog(frame, "Veuillez sélectionner au moins un type de caractère.", "Erreur de Génération", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int length = lengthSlider.getValue();
        String generatedPassword = passwordService.generatePassword(length, useUpperCase, useLowerCase, useNumbers, useSymbols);

        if (generatedPassword != null) {
            passwordDisplayField.setText(generatedPassword);
            PasswordStrengthLevel strength = passwordService.evaluatePasswordStrength(generatedPassword);
            updateStrengthLabel(strength);
        } else {
            // This case should ideally be caught by the char set check above
            passwordDisplayField.setText("");
            updateStrengthLabel(PasswordStrengthLevel.EMPTY);
             JOptionPane.showMessageDialog(frame,
                "Erreur interne lors de la génération du mot de passe.",
                "Erreur Interne",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the strength label text and color based on the evaluated strength.
     * @param strengthLevel The evaluated password strength.
     */
    private void updateStrengthLabel(PasswordStrengthLevel strengthLevel) {
        if (strengthLevel == null) { // Should not happen
            strengthLevel = PasswordStrengthLevel.EMPTY;
        }
        strengthLabel.setText("Force: " + strengthLevel.getDisplayName());
        strengthLabel.setForeground(strengthLevel.getDisplayColor());
    }

    /**
     * Handles copying the generated password to the system clipboard.
     */
    private void handleCopyPassword() {
        String password = passwordDisplayField.getText();
        if (password != null && !password.isEmpty()) {
            try {
                StringSelection stringSelection = new StringSelection(password);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(frame,
                        "Mot de passe copié dans le presse-papiers !",
                        "Copie Réussie",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                // Log detailed error for developers
                System.err.println("Error copying to clipboard: " + ex.getMessage());
                ex.printStackTrace();
                // User-friendly message
                JOptionPane.showMessageDialog(frame,
                        "Erreur lors de la copie du mot de passe: " + ex.getMessage(),
                        "Erreur de Copie",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Aucun mot de passe à copier.",
                    "Copie Impossible",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Main method to launch the application.
     * Ensures UI operations are done on the Event Dispatch Thread.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PasswordGeneratorApp(); // Changed to new class name
            }
        });
    }
}
