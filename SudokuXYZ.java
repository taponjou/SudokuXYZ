import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SudokuXYZ {
    // Modifier ce chemin pour pointer vers votre fichier sudoku.txt
    private static final String FILE_PATH = "sudoku2.txt";

    public static void main(String[] args) {
        // Charger le fichier et remplir la grille
        File fichier = (args.length >= 1) ? new File(args[0]) : new File(FILE_PATH);
        int[][] grille = new int[9][9];
      
        // Vérifier si le fichier existe
        if (!fichier.exists() || !fichier.isFile()) {
            System.err.println("Erreur : fichier introuvable : " + fichier.getAbsolutePath());
            System.exit(3);
        }

        // Lire le fichier et remplir la grille
        try {
            lireFichierXYZ(fichier, grille);

        } catch (FileNotFoundException e) {
            System.err.println("Erreur : fichier introuvable : " + e.getMessage());
            System.exit(3);
        }

        // Valider la grille après l'avoir lue
        try {
            validerGrille(grille);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de validation de la grille 9x9 : " + e.getMessage());
            System.exit(2);
        }
        // Afficher la grille originale
        System.out.println("Grille lue (originale) :");
        afficherGrille(grille);

        // Transposer la grille et l'afficher
        int[][] transposed = transposeGrille(grille);
        System.out.println("\nGrille transposée :");
        afficherGrille(transposed);

        // Écrire le résultat dans un fichier
        String cheminFichierSortie = "sudoku_resultat.txt";

        ecrireResultatDansFichier(transposed, cheminFichierSortie);

    }

    /**
     * Lit un fichier contenant des codes XYZ non séparés
     * Chaque bloc XYZ est interprété comme :
     * X = ligne (0–8)
     * Y = colonne (0–8)
     * Z = valeur (1–9)
     */
    
    public static void lireFichierXYZ(File fichier, int[][] grille) throws FileNotFoundException {
        Scanner scanner = new Scanner(fichier);

        while (scanner.hasNext()) {
            String token = scanner.next();

            // Vérifie que le token contient bien 3 caractères
            if (token.length() != 3) {
                System.err.println("Format invalide (doit être XYZ) : " + token);
                continue;
            }

            // Extraction des valeurs numériques
            int x = Character.getNumericValue(token.charAt(0));
            int y = Character.getNumericValue(token.charAt(1));
            int z = Character.getNumericValue(token.charAt(2));

            // Vérification des bornes
            if (x < 0 || x > 8 || y < 0 || y > 8 || z < 1 || z > 9) {
                System.err.println("Triplet hors bornes : " + token);
                continue;
            }

            grille[x][y] = z;
        }

        scanner.close();
    }

    /** Transpose la grille */
    public static int[][] transposeGrille(int[][] grille) {
        int[][] t = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                t[j][i] = grille[i][j];
            }
        }
        return t;
    }

    /** Vérifie que chaque chiffre 1..9 apparaît exactement une fois par rangée et par colonne.
     *  Lance IllegalArgumentException en cas d'erreur (valeur hors plage, doublon, ou valeur manquante).
     */
    public static void validerGrille(int[][] grille) {
        // Vérification des lignes : chaque ligne doit contenir exactement les chiffres 1..9 une fois
        for (int i = 0; i < 9; i++) {
            boolean[] present = new boolean[10]; // indices 1..9
            for (int j = 0; j < 9; j++) {
                int val = grille[i][j];
                if (val < 1 || val > 9) {
                    throw new IllegalArgumentException("Case vide ou valeur hors plage en ligne " + (i + 1) + ", colonne " + (j + 1) + " : " + val);
                }
                if (present[val]) {
                    throw new IllegalArgumentException("Doublon dans la ligne " + (i + 1) + " : valeur " + val);
                }
                present[val] = true;
            }
            // vérifier que tous les chiffres 1..9 sont présents
            for (int v = 1; v <= 9; v++) {
                if (!present[v]) {
                    throw new IllegalArgumentException("Valeur manquante dans la ligne " + (i + 1) + " : " + v);
                }
            }
        }

        // Vérification des colonnes : chaque colonne doit contenir exactement les chiffres 1..9 une fois
        for (int j = 0; j < 9; j++) {
            boolean[] present = new boolean[10]; // indices 1..9
            for (int i = 0; i < 9; i++) {
                int val = grille[i][j];
                if (val < 1 || val > 9) {
                    throw new IllegalArgumentException("Case vide ou valeur hors plage en colonne " + (j + 1) + ", ligne " + (i + 1) + " : " + val);
                }
                if (present[val]) {
                    throw new IllegalArgumentException("Doublon dans la colonne " + (j + 1) + " : valeur " + val);
                }
                present[val] = true;
            }
            // vérifier que tous les chiffres 1..9 sont présents
            for (int v = 1; v <= 9; v++) {
                if (!present[v]) {
                    throw new IllegalArgumentException("Valeur manquante dans la colonne " + (j + 1) + " : " + v);
                }
            }
        }
    }

    /** Affiche la grille */
    public static void afficherGrille(int[][] grille) {
        System.out.println("Grille 9x9 :");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(grille[i][j] + " ");
            }
            System.out.println();
        }
    }

    // ecrire le sultatut dans un reportoire dans un fichier texte

    public static void ecrireResultatDansFichier(int[][] grille, String cheminFichier) {
        // Crée le fichier (et les répertoires parents si nécessaire) puis écrit :
        // une représentation 9x9 ligne par ligne 
        // une section "Triplets (x y z)" listant les cases non vides (indices 0..8)
        File out = new File(cheminFichier);
        File parent = out.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(out)))) {
            pw.println("Grille 9x9 :");
            for (int i = 0; i < 9; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < 9; j++) {
                    sb.append(grille[i][j] == 0 ? '.' : grille[i][j]);
                    if (j < 8) sb.append(' ');
                }
                pw.println(sb.toString());
            }
            System.out.println("Résultat écrit dans : " + out.getAbsolutePath());
        } catch (java.io.IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }

}
