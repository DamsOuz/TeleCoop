package com.telecoop.telecoop.data;
import java.util.Arrays;
import java.util.List;

public class QuestionBank {

    public List<Question> getQuestions(){
        return Arrays.asList(
                new Question("Quelles sont tes motivations ?",
                        Arrays.asList(
                                "Je veux être plus autonome",
                                "Je veux arrêter de perdre du temps",
                                "Je souhaite diminuer mon temps d'écran",
                                "Je veux retrouver de la joie dans mes utilisations",
                                "Je veux reprendre le contrôle")),

                new Question("Voici nos suggestions d'objectifs, lequel veux-tu utiliser ?",
                        Arrays.asList(
                                "Fixer une limite de temps d'écran par jour",
                                "Fixer des limites par applications par jour",
                                "Fixer des horaires de temps d'écran limité")),
                new Question("Ceci est une question test pour mon dev :)",
                        Arrays.asList(
                                "Réponse A",
                                "Réponse B",
                                "Réponse C",
                                "Réponse D"))
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
