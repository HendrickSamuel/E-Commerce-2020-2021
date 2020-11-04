package Requetes_R;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Main
{

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) throws RserveException, REXPMismatchException
    {
        //String chemin = "C:/Users/delav/Documents/3eme annee/Technologie de l'e-commerce et mobiles/Technologie de l'e-commerce et mobiles Labo/";
        String chemin = "D:/Documents/Seraing/2020-2021/E-COMMERCE/";
        JFrame fen = creeFenetre();

        RConnection conn = new RConnection("localhost");

        conn.voidEval("setwd(\"" + chemin +"\")");
        conn.voidEval("batracie<-read.table(\"sante_batracie_2.csv\", sep=\",\", dec =\".\", header = T)");
        System.out.println("-->sante_bacterie_2.csv recuperee");

        conn.voidEval("batracie$edlevel <- factor(batracie$edlevel, levels= c(1,2,3,4), labels= c(\"prim\",\"secinf\",\"secsup\",\"sup\"))");
        conn.voidEval("batracie$outwork <- factor(batracie$outwork, levels = c(0,1), labels = c(\"actif\",\"non-actif\"))");
        conn.voidEval("batracie$female <- factor(batracie$female, levels = c(0,1), labels = c (\"homme\",\"femme\"))");
        conn.voidEval("batracie$married <- factor(batracie$married, levels = c(0,1), labels = c(\"celibataire\",\"marie\"))");
        conn.voidEval("batracie$kids <- factor(batracie$kids, levels=c(0,1), labels = c(\"pas enfants\",\"enfants\"))");
        conn.voidEval("batracie$self <- factor(batracie$self, levels=c(0,1), labels = c(\"dependant\",\"independant\"))");

        conn.voidEval("batracie$edlevel1 <- factor(batracie$edlevel1, levels= c(0,1), labels = c(\"sans diplome\",\"diplome\"))");
        conn.voidEval("batracie$edlevel2 <- factor(batracie$edlevel2, levels= c(0,1), labels = c(\"sans diplome\",\"diplome\"))");
        conn.voidEval("batracie$edlevel3 <- factor(batracie$edlevel3, levels= c(0,1), labels = c(\"sans diplome\",\"diplome\"))");
        conn.voidEval("batracie$edlevel4 <- factor(batracie$edlevel4, levels= c(0,1), labels = c(\"sans diplome\",\"diplome\"))");
        System.out.println("-->factor effectue");

        REXP x = conn.eval("summary(batracie)");

        System.out.println("#1. le nombre de consultations varie-t-il en fonction du niveau d'etudes ?");

        conn.voidEval("echantillon <- batracie[sample(nrow(batracie), 100), ]"); //on suppose que l'échantillon est repésentatif

        conn.voidEval("png(file = \"out.png\", width = 800, height = 700)");
        conn.voidEval("boxplot(echantillon$docvis~echantillon$edlevel)");
        conn.voidEval("dev.off()");

        afficheImage(fen, chemin, "out.png");

        System.out.println("Y a-t-il des valeurs aberrantes ?");
        System.out.println("\t0. NON");
        System.out.println("\t1. OUI");
        System.out.print("Choix : ");

        Scanner in= new Scanner(System.in);
        int choix=in.nextInt();

        if(choix == 1)
        {
            System.out.print("Quel est la valeur limite : ");
            int valeur=in.nextInt();

            conn.voidEval("echantillon <- echantillon[echantillon$docvis < "+valeur+" , ]");
            conn.voidEval("png(file = \"out2.png\", width = 800, height = 700)");
            conn.voidEval("boxplot(echantillon$docvis~echantillon$edlevel)");
            conn.voidEval("dev.off()");

            afficheImage(fen, chemin, "out2.png");
        }

        conn.voidEval("modele <- lm(echantillon$docvis~echantillon$edlevel)");

        conn.eval("resanova <- anova(modele)");
        x = conn.eval("resanova$`Pr(>F)`[1]");
        double pvalue = x.asDouble();
        System.out.println(ANSI_BLUE + "Pvalue de Anova: " + ANSI_RESET + pvalue);
        if(pvalue > 0.05)
            System.out.println( ANSI_RED + "Le modele n'apporte rien car " + pvalue + " > la limite 0.05 (5%)" + ANSI_RESET);
        else
            System.out.println(ANSI_GREEN + "Le modele apporte quelque chose car " + pvalue + " < la limite 0.05 (5%)" + ANSI_RESET);

        System.out.println("OneWay si on assume que les variances ne sont pas égales");
        conn.voidEval("ow <- oneway.test(echantillon$docvis~echantillon$edlevel, var.equal = FALSE)");
        x = conn.eval("ow$p.value");
        pvalue = x.asDouble();
        System.out.println(ANSI_BLUE + " -> " + ANSI_RESET +pvalue);

        System.out.println("OneWay si on assume que les variances sont égales");
        conn.voidEval("ow <- oneway.test(echantillon$docvis~echantillon$edlevel, var.equal = TRUE)");
        x = conn.eval("ow$p.value");
        pvalue = x.asDouble();
        System.out.println(ANSI_BLUE + " -> " + ANSI_RESET +pvalue + ANSI_CYAN + " égal à la pvalue de anova" + ANSI_RESET);


        String[] ts, ys, nomcol, nomlig;
        double[][] matrice;
        REXP namecol, namerow;

        System.out.println("pairewise false\n");
        conn.voidEval("pw <- pairwise.t.test(echantillon$docvis, echantillon$edlevel, pool.sd = FALSE)");
        conn.voidEval("pw <- pw$p.value");
        namecol = conn.eval("colnames(pw)");
        namerow = conn.eval("row.names(pw)");
        x = conn.eval("pw");
        matrice = x.asDoubleMatrix();
        nomcol = namecol.asStrings();
        nomlig = namerow.asStrings();

        for(int i =0; i<nomcol.length;i++){
            if(i==0)
                System.out.print("# \t\t    ");
            System.out.print( "|" +  nomcol[i] + " |" + "\t");
        }
        System.out.println("");


        for(int i=0; i<matrice.length; i++){
            System.out.print( "|" +  nomlig[i] + " |" + "\t");
            for(int j=0;j< matrice.length;j++){
                System.out.print( "|" +  matrice[i][j] + " |" + "\t");
            }
            System.out.println("");
        }

        System.out.println("pairewise true");
        conn.voidEval("pw <- pairwise.t.test(echantillon$docvis, echantillon$edlevel, pool.sd = TRUE)");
        conn.voidEval("pw <- pw$p.value");
        namecol = conn.eval("colnames(pw)");
        namerow = conn.eval("row.names(pw)");
        x = conn.eval("pw");
        matrice = x.asDoubleMatrix();
        nomcol = namecol.asStrings();
        nomlig = namerow.asStrings();

        for(int i =0; i<nomcol.length;i++){
            if(i==0)
                System.out.print("# \t\t    ");
            System.out.print( "|" +  nomcol[i] + " |" + "\t");
        }
        System.out.println("");


        for(int i=0; i<matrice.length; i++){
            System.out.print( "|" +  nomlig[i] + " |" + "\t");
            for(int j=0;j< matrice.length;j++){
                System.out.print( "|" +  matrice[i][j] + " |" + "\t");
            }
            System.out.println("");
        }

        in.next();
        System.out.println("NEXT");

        // -- QUESTION 2
        conn.voidEval("echantillon <- batracie[sample(nrow(batracie), 100), ]");
        conn.voidEval("png(file = \"out3.png\", width = 800, height = 700)");
        conn.voidEval("plot(echantillon$docvis[echantillon$female==\"femme\"]~echantillon$hhninc[echantillon$female==\"femme\"])");
        conn.voidEval("dev.off()");
        afficheImage(fen, chemin, "out3.png");

        conn.voidEval("res <- summary(lm(batracie$docvis[batracie$female==\"femme\"]~batracie$hhninc[batracie$female==\"femme\"]))");
        //pvalue

        x = conn.eval("pvalue <- pf(res$fstatistic[1],res$fstatistic[2],res$fstatistic[3], lower.tail = FALSE)");
        pvalue = x.asDouble();
        if(pvalue > 0.05)
            System.out.println( ANSI_RED + "Le modele n'apporte rien car " + pvalue + " > la limite 0.05 (5%)" + ANSI_RESET);
        else
            System.out.println(ANSI_GREEN + "Le modele apporte quelque chose car " + pvalue + " < la limite 0.05 (5%)" + ANSI_RESET);


        //adjusted R squared
        x = conn.eval("res$adj.r.squared");
        System.out.println(ANSI_BLUE + "Adjusted R squared: " + ANSI_RESET + x.asDouble());

    }

    static JFrame creeFenetre()
    {
        JFrame fen = new JFrame ("Graphique");
        JLabel labelImage = new JLabel();
        labelImage.setIcon(new ImageIcon());
        fen.setContentPane(labelImage);
        fen.setMinimumSize(new Dimension(800,700));
        fen.setSize(850,600);
        fen.pack();
        fen.setLocationRelativeTo(null);
        fen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        return fen;
    }

    static void afficheImage(JFrame fen, String dossier, String image)
    {
        JLabel labelImage = new JLabel();
        labelImage.setIcon(new ImageIcon(dossier+image));
        fen.setContentPane(labelImage);
        fen.setVisible(true);
    }

    /*
        conn.voidEval("modele <- lm(echantillon$docvis~echantillon$edlevel)");
        conn.voidEval("resultat <- summary(modele)");
        conn.voidEval("Fpvalue <- resultat$fstatistic");
        x = conn.eval("pvalue <- pf(Fpvalue[1],Fpvalue[2],Fpvalue[3], lower.tail = FALSE)");
        double pvalue = x.asDouble();
        System.out.println( ANSI_BLUE  + "Pvalue de Fisher : " + ANSI_RESET + pvalue +"\n"); //pour voir si les valeurs expliquées par le modèle sont significativement différentes des valeurs observées ou non

        if(pvalue > 0.05)
            System.out.println( ANSI_RED + "Le modele n'apporte rien car " + pvalue + " > la limite 0.05 (5%)");
        else
            System.out.println(ANSI_GREEN + "Le modele apporte quelque chose");
     */
}
