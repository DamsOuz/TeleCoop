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
                        "Tutoriel : mieux gérer mes notifications afin de réduire la stimulation",
                        "Être constamment sollicité.e par des notifications peut facilement devenir fatigant et stressant, peut-être que désactiver certaines notifications peut t’être bénéfique.\n\n" +
                                "Sur Android :\n" +
                                "1. Ouvre l'application de paramètres de ton appareil Android.\n" +
                                "2. Recherche « Notifications »  et clique dessus.\n" +
                                "3. Recherche « Récemment envoyés » puis clique sur « Afficher tout ».\n" +
                                "4. Tu peux alors choisir les applications dont tu ne veux plus de notification.\n\n" +
                                "Pense aussi à personnaliser les notifications sur tes messageries !\n" +
                                "Pour ne pas être constamment dérangé.e, tu peux aussi activer le mode silencieux de ton téléphone.",
                        null
                );
            case "tutoriel_blocage":
                return new Content(
                        "Tutoriel : limiter l’utilisation d’une application par un blocage forcé",
                        "Cela te permettra d’avoir des barrières dures supplémentaires à franchir, afin de te dissuader de trop utiliser certaines applications\n\n" +
                                "Sur Android :\n" +
                                "1. Ouvre l'application de paramètres de ton appareil Android.\n" +
                                "2. Recherche « Bien-être numérique & contrôle parental » et clique dessus.\n" +
                                "3. Sur la nouvelle page, clique sur le tableau de bord.\n" +
                                "4. Recherche l'application que tu veux bloquer.\n" +
                                "5. Paramétre la durée limite d'utilisation et clique sur « Ok ».\n",
                        null
                );
            case "tutoriel_filtres_couleurs":
                return new Content(
                        "Tutoriel : instaurer des filtres de couleurs (lumière bleue et noir et blanc)",
                        "Au-delà de l’impact écologique réduit et de la protection anti-lumière bleue, changer les couleurs de ton téléphone peut aussi moins te distraire et réduire ton envie de cliquer sur une application.\n\n" +
                                "Sur Android :\n\n" +
                                "Filtre de lumière bleue \n" +
                                "1. Ouvre l'application de paramètres de ton appareil Android.\n" +
                                "2. Recherche « Affichage » et clique dessus.\n" +
                                "3. Touche filtre de lumière bleue.\n" +
                                "4. Touche le bouton coulissant pour Activer maintenant le filtre de lumière bleue.\n\n" +
                                "Filtre noir et blanc \n" +
                                "1. Ouvre l'application de paramètres de ton appareil Android.\n" +
                                "2. Recherche « Bien-être numérique & contrôle parental »  et clique dessus.\n" +
                                "3. Recherche Mode Heure de coucher.\n" +
                                "4. Clique sur activer.",
                        // exemple pour mettre une image : R.drawable.content_filtres_couleurs
                        null
                );
            case "tutoriel_mode_travail":
                return new Content(
                        "Tutoriel : instaurer des temps de concentration pour réduire mon utilisation sur des plages horaires",
                        "Si tu as des difficultés à ne pas utiliser ton téléphone lorsque tu travailles ou dois te concentrer, instaurer des modes spécifiques qui ne donnent accès qu’à certaines applications peut peut-être t’aider.\n\n" +
                                "Sur Android :\n" +
                                "1. Ouvre l'application de paramètres de ton appareil Android.\n" +
                                "2. Recherche « Bien-être numérique & contrôle parental »  et clique dessus.\n" +
                                "3. Recherche Mode Concentration.\n" +
                                "4. Tu peux alors créer des temps de travail / temps personnels et les activer.\n",
                        null
                );
            default:
                return new Content("Contenu inconnu", "Aucun contenu n'est disponible.", null);
        }
    }
}
