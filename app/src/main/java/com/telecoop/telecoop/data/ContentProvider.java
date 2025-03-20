package com.telecoop.telecoop.data;

import com.telecoop.telecoop.R;

public class ContentProvider {

    public static Content getContent(String type) {
        switch (type) {
            case "temps_positifs":
                return new Content(
                        "Programmer des temps positifs quotidiens",
                        "Ceci est le contenu textuel des temps positifs",
                        null
                );
            case "gestion_objectifs":
                return new Content(
                        "Fixer des horaires d'utilisation par application",
                        "Ceci est le contenu textuel de la gestion des objectifs",
                        null
                );
            case "gestion_notifications":
                return new Content(
                        "Conscientiser mon utilisation du téléphone via des notifications",
                        "Ceci est le contenu textuel de la gestion des notifications",
                        null
                );
            case "tutoriel_notifications":
                return new Content(
                        "Tutoriel : mieux gérer mes notifications",
                        "Ceci est le contenu textuel du tutoriel sur les notifications",
                        null
                );
            case "tutoriel_blocage":
                return new Content(
                        "Tutoriel : limiter l'utilisation d'application par un blocage",
                        "Ceci est le contenu textuel du tutoriel sur les blocages",
                        null
                );
            case "tutoriel_filtres_couleurs":
                return new Content(
                        "Tutoriel : instaurer des filtres de couleurs",
                        "Bienvenue dans le tutoriel sur les filtres couleurs.\n\n" +
                                "Dans cette section, vous apprendrez à appliquer et ajuster des filtres pour modifier l'apparence de vos images ou éléments graphiques.\n" +
                                "1. Sélectionnez l'image ou l'élément à modifier.\n" +
                                "2. Choisissez une couleur ou un filtre dans la palette proposée.\n" +
                                "3. Appliquez le filtre pour obtenir l'effet désiré.\n\n" +
                                "N'hésitez pas à expérimenter pour trouver le rendu qui vous convient le mieux !",
                        // exemple pour mettre une image : R.drawable.content_filtres_couleurs
                        null
                );
            case "tutoriel_mode_travail":
                return new Content(
                        "Tutoriel : instaurer des temps de concentration",
                        "Ceci est le contenu textuel du tutoriel sur le mode travail",
                        null
                );
            default:
                return new Content("Contenu inconnu", "Aucun contenu n'est disponible.", null);
        }
    }
}
