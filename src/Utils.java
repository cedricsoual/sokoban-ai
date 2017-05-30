import java.io.*;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by cedric on 12/04/2017.
 */
 public class Utils {

    // methode permettant de lire le fichier d'entrée
    public static Case[][] readFile(String path) throws IOException {
        Case[][] niveau = new Case[16][19];
        File initialFile = new File(path);
        InputStream in = new FileInputStream(initialFile.toString());

        BufferedReader flot = new BufferedReader(new InputStreamReader(in));
        int ligneCourante  = 0;
        String ligne = flot.readLine();
        while (ligne.charAt(0) != 'A') {
            for (int i = 0; i < Math.min(19,ligne.length());i++) {
                switch(ligne.charAt(i)) {
                    case '@' :
                        niveau[ligneCourante][i] = new Case(Case.PERSO);
                        break;
                    case ' ' :
                        niveau[ligneCourante][i] = new Case(Case.CASE_VIDE);
                        break;
                    case '#' :
                        niveau[ligneCourante][i] = new Case(Case.MUR);
                        break;
                    case '*' :
                        niveau[ligneCourante][i] = new Case(Case.CAISSE_SUR_ZONE);
                        break;
                    case '$' :
                        niveau[ligneCourante][i] = new Case(Case.CAISSE);
                        break;
                    case '.' :
                        niveau[ligneCourante][i] = new Case(Case.ZONE_RANGEMENT);
                        break;
                    case '+' :
                        niveau[ligneCourante][i] = new Case(Case.PERSO_SUR_ZONE);
                        break;
                }
            }
            for (int i = Math.min(19,ligne.length()); i < 19;i++) {
                niveau[ligneCourante][i] = new Case(Case.CASE_VIDE);
            }
            ligneCourante++;
            ligne = flot.readLine();
        }
        for (int i = ligneCourante; i < 16; i++) {
            for(int j = 0; j < 19; j++) {
                niveau[i][j] = new Case(Case.CASE_VIDE);
            }
        }
        flot.close();
        return niveau;
    }

    public static String formatTime(long millis){
        final TimeUnit scale = MILLISECONDS;
        System.out.println("Temps : "+millis+" millis");

        long minutes = scale.toMinutes( millis );
        millis -= MINUTES.toMillis( minutes );
        long seconds = scale.toSeconds( millis );
        millis -= SECONDS.toMillis( seconds );
        long milli = scale.toMillis( millis );

        return String.format(
                "%d minutes, %d seconds, %d millis",
                minutes, seconds, milli );
    }
}
