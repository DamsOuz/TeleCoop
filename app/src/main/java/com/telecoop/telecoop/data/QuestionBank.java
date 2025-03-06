package com.telecoop.telecoop.data;
import java.util.Arrays;
import java.util.List;

public class QuestionBank {

    public List<Question> getQuestions(){
        return Arrays.asList(
                new Question("Quelle est ta qualité de sommeil ?",
                        Arrays.asList(
                                "Je n'arrive pas à m'endormir",
                                "Je me couche trop tard car je regarde mon téléphone",
                                "Je n'ai pas de problème pour dormir")),

                new Question("Dans mes activités :",
                        Arrays.asList(
                                "Je suis souvent distrait.e par mon téléphone",
                                "Je trouve que je manque de productivité",
                                "Je n'ai pas de problème à me concentrer",
                                "Je suis souvent fatigué.e")),
                new Question("Je vais me balader dehors et je me rends compte que j'ai oublié mon téléphone chez moi :",
                        Arrays.asList(
                                "Je me sens anxieux.se / stressé.e",
                                "Je me sens bien",
                                "Je m'ennuie",
                                "Je me sens libéré.e",
                                "Je me sens seul.e")),
                new Question("Je sors mon téléphone dans les situations suivantes :",
                        Arrays.asList(
                                "J'attends l'ascenseur ou le bus",
                                "Mes ami.e.s sont aux toilettes au restaurant",
                                "Je me déplace à pieds",
                                "Je regarde un film",
                                "J'en ai besoin",
                                "Je n'ai rien à faire")),
                new Question("Lorsque je me sens mal (triste, stressé.e...) :",
                        Arrays.asList(
                                "Je contacte mes proches",
                                "Je fais une activité seul.e qui me plaît",
                                "Je lance un jeu sur mon téléphone",
                                "J'ouvre un réseau social sur mon téléphone (instagram, tiktok...)",
                                "Je m'isole")),
                new Question("Le plus souvent, à la fin d'une journée :",
                        Arrays.asList(
                                "Je suis satisfait.e de ce que j'ai fait",
                                "J'ai l'impression d'avoir perdu du temps sur mon téléphone",
                                "J'ai l'impression de pas avoir fait assez",
                                "J'ai l'impression de pas avoir été efficace / concentré.e"))
        );
    }

    private static QuestionBank instance;
    public static QuestionBank getInstance(){
        if (instance == null){
            instance = new QuestionBank();
        }
        return instance;
    }
}
