package Requetes_R;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import javax.swing.*;
import java.util.Scanner;

public class Main
{

    public static void main(String[] args) throws RserveException, REXPMismatchException
    {
        JFrame fen = creeFenetre();

        RConnection conn = new RConnection("localhost");
        //double[] d= conn.eval("rnorm(100)").asDoubles();
        conn.voidEval("setwd(\"C:/Users/delav/Documents/3eme annee/Technologie de l'e-commerce et mobiles/Technologie de l'e-commerce et mobiles Labo/\")");
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
        /*String[] summary = x.asStrings();

        System.out.println("-->summary effectue");
        for( int i = 0; i < summary.length ; i++)
        {
            System.out.println(summary[i]);
        }*/

        System.out.println("#1. le nombre de consultations varie-t-il en fonction du niveau d'etudes ?");

        conn.voidEval("echantillon <- batracie[sample(nrow(batracie), 100), ]"); //on suppose que l'échantillon est repésentatif

        conn.voidEval("png(file = \"out.png\", width = 800, height = 700)");
        conn.voidEval("boxplot(echantillon$docvis~echantillon$edlevel)");
        conn.voidEval("dev.off()");

        afficheImage(fen);


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
            conn.voidEval("png(file = \"out.png\", width = 800, height = 700)");
            conn.voidEval("boxplot(echantillon$docvis~echantillon$edlevel)");
            conn.voidEval("dev.off()");


            afficheImage(fen);
        }
    }

    static JFrame creeFenetre()
    {
        JFrame fen = new JFrame ("Graphique");
        JLabel labelImage = new JLabel();
        labelImage.setIcon(new ImageIcon());
        fen.setContentPane(labelImage);
        fen.setSize(850,600);
        fen.pack();
        fen.setLocationRelativeTo(null);
        fen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        return fen;
    }

    static void afficheImage(JFrame fen)
    {
        JLabel labelImage = new JLabel();
        labelImage.setIcon(new ImageIcon("C:/Users/delav/Documents/3eme annee/Technologie de l'e-commerce et mobiles/Technologie de l'e-commerce et mobiles Labo/out.png"));
        fen.setContentPane(labelImage);
        fen.setVisible(true);
    }
}
