package com.telecoop.telecoop.data;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class QuestionBank {

    private static QuestionBank instance;
    private List<Question> questions;
    private final int baseQuestionCount; //nombre initial de questions fixes

    private QuestionBank(){
        questions = new ArrayList<>(Arrays.asList(
                new Question("Quelle est ta qualité de sommeil ?",
                        Arrays.asList(
                                new AnswerChoice(
                                        "Je n'arrive pas à m'endormir",
                                        Arrays.asList(Profile.SOMMEIL)
                                ),
                                new AnswerChoice(
                                        "Je me couche trop tard car je regarde mon téléphone",
                                        Arrays.asList(Profile.SOMMEIL)
                                ),
                                new AnswerChoice(
                                        "Je n'ai pas de problème pour dormir",
                                        Arrays.asList(Profile.PROFIL_PAR_DEFAUT)
                                )
                        )),

                new Question("Dans mes activités :",
                        Arrays.asList(
                                new AnswerChoice(
                                        "Je suis souvent distrait.e par mon téléphone",
                                        Arrays.asList(Profile.MANQUE_PRODUCTIVITE)
                                ),
                                new AnswerChoice(
                                        "Je n'ai pas de problème à me concentrer",
                                        Arrays.asList(Profile.PROFIL_PAR_DEFAUT)
                                ),
                                new AnswerChoice(
                                        "Je suis souvent fatigué.e",
                                        Arrays.asList(Profile.SOMMEIL)
                                )
                        )),

                new Question("Je vais me balader dehors et je me rends compte que j'ai oublié mon téléphone chez moi :",
                        Arrays.asList(
                                new AnswerChoice(
                                        "Je me sens anxieux.se / stressé.e",
                                        Arrays.asList(Profile.FOMO)
                                ),
                                new AnswerChoice(
                                        "Je me sens bien",
                                        Arrays.asList(Profile.PROFIL_PAR_DEFAUT)
                                ),
                                new AnswerChoice(
                                        "Je m'ennuie",
                                        Arrays.asList(Profile.PERTE_TEMPS_ENNUI)
                                ),
                                new AnswerChoice(
                                        "Je me sens libéré.e",
                                        Arrays.asList(Profile.PROFIL_PAR_DEFAUT)
                                ),
                                new AnswerChoice(
                                        "Je me sens seul.e",
                                        Arrays.asList(Profile.FOMO)
                                )
                        )),

                new Question("Je sors mon téléphone dans les situations suivantes :",
                        Arrays.asList(
                                new AnswerChoice(
                                        "J'attends l'ascenseur ou le bus",
                                        Arrays.asList(Profile.AUTOMATISME)
                                ),
                                new AnswerChoice(
                                        "Mes ami.e.s sont aux toilettes au restaurant",
                                        Arrays.asList(Profile.AUTOMATISME)
                                ),
                                new AnswerChoice(
                                        "Je me déplace à pieds",
                                        Arrays.asList(Profile.AUTOMATISME)
                                ),
                                new AnswerChoice(
                                        "Je regarde un film",
                                        Arrays.asList(Profile.MANQUE_PRODUCTIVITE)
                                ),
                                new AnswerChoice(
                                        "J'en ai besoin",
                                        Arrays.asList(Profile.PROFIL_PAR_DEFAUT)
                                ),
                                new AnswerChoice(
                                        "Je n'ai rien à faire",
                                        Arrays.asList(Profile.PERTE_TEMPS_ENNUI)
                                )
                        )),

                new Question("Lorsque je me sens mal (triste, stressé.e...) :",
                        Arrays.asList(
                                new AnswerChoice(
                                        "Je contacte mes proches",
                                        Arrays.asList(Profile.PROFIL_PAR_DEFAUT)
                                ),
                                new AnswerChoice(
                                        "Je fais une activité seul.e qui me plaît",
                                        Arrays.asList(Profile.PROFIL_PAR_DEFAUT)
                                ),
                                new AnswerChoice(
                                        "Je lance un jeu sur mon téléphone",
                                        Arrays.asList(Profile.DOOMSCROLL)
                                ),
                                new AnswerChoice(
                                        "J'ouvre un réseau social sur mon téléphone (instagram, tiktok...)",
                                        Arrays.asList(Profile.DOOMSCROLL)
                                ),
                                new AnswerChoice(
                                        "Je m'isole",
                                        Arrays.asList(Profile.PROFIL_PAR_DEFAUT)
                                )
                        )),

                new Question("Le plus souvent, à la fin d'une journée :",
                        Arrays.asList(
                                new AnswerChoice(
                                        "Je suis satisfait.e de ce que j'ai fait",
                                        Arrays.asList(Profile.PROFIL_PAR_DEFAUT)
                                ),
                                new AnswerChoice(
                                        "J'ai l'impression d'avoir perdu du temps sur mon téléphone",
                                        Arrays.asList(Profile.PERTE_TEMPS_ENNUI)
                                ),
                                new AnswerChoice(
                                        "J'ai l'impression de pas avoir fait assez",
                                        Arrays.asList(Profile.MANQUE_PRODUCTIVITE)
                                ),
                                new AnswerChoice(
                                        "J'ai l'impression de pas avoir été efficace / concentré.e",
                                        Arrays.asList(Profile.MANQUE_PRODUCTIVITE)
                                )
                        )),

                new Question("Je trouve que je passe trop de temps sur mon téléphone à :",
                        Arrays.asList(
                                new AnswerChoice(
                                        "Jouer à des jeux",
                                        Arrays.asList(Profile.MANQUE_PRODUCTIVITE)
                                ),
                                new AnswerChoice(
                                        "Scroller sur les réseaux",
                                        Arrays.asList(Profile.DOOMSCROLL)
                                ),
                                new AnswerChoice(
                                        "Surveiller mes notifications de discussion",
                                        Arrays.asList(Profile.FOMO)
                                ),
                                new AnswerChoice(
                                        "Regarder des vidéos",
                                        Arrays.asList(Profile.MANQUE_PRODUCTIVITE)
                                )
                        )),

                new Question("Parmi ces options, je souhaite me focaliser sur :",
                        Arrays.asList(
                                new AnswerChoice(
                                        "Améliorer ma qualité de sommeil",
                                        Arrays.asList(Profile.SOMMEIL)
                                ),
                                new AnswerChoice(
                                        "Arrêter le doomscroll",
                                        Arrays.asList(Profile.DOOMSCROLL)
                                ),
                                new AnswerChoice(
                                        "Moins perdre de temps sur mon téléphone quand je m'ennuie",
                                        Arrays.asList(Profile.PERTE_TEMPS_ENNUI)
                                ),
                                new AnswerChoice(
                                        "Etre moins distrait.e",
                                        Arrays.asList(Profile.MANQUE_PRODUCTIVITE)
                                ),
                                new AnswerChoice(
                                        "Avoir moins d'anxiété de manquer des choses",
                                        Arrays.asList(Profile.FOMO)
                                ),
                                new AnswerChoice(
                                        "Me sentir plus connecté.e à moi-même",
                                        Arrays.asList(Profile.AUTOMATISME)
                                )
                        ))
        ));

        // Stocke le nombre initial de questions fixes
        baseQuestionCount = questions.size();
    }

    public int getBaseQuestionCount(){
        return baseQuestionCount;
    }

    public List<Question> getQuestions(){
        return questions;
    }

    public static QuestionBank getInstance(){
        if (instance == null){
            instance = new QuestionBank();
        }
        return instance;
    }

    // Ajoute une question faisant référence au prénom
    public void addNameQuestion(String beforeName, String userName, String afterName, int index, List<AnswerChoice> answerChoices) {
        if (index < 0) index = 0;
        if (index > questions.size()) index = questions.size();

        // Supprimer la question si elle existe déjà avec un autre prénom
        removeNameQuestions(beforeName + afterName);

        Question nameQuestion = new Question(
                beforeName + userName + afterName,
                answerChoices
        );

        // Ajouter cette question à l'index de notre choix
        questions.add(index, nameQuestion);
    }

    public void removeNameQuestions(String questionWithoutUsername) {

        // On enlève le fait que les chiffres puissent faire une différence dans le String initial
        String output = questionWithoutUsername.replaceAll("[0-9]", "");

        // Utiliser un Iterator pour éviter les ConcurrentModificationException
        Iterator<Question> iterator = questions.iterator();
        while (iterator.hasNext()) {
            Question q = iterator.next();
            String questionText = q.getQuestion().replaceAll("[0-9]", "");
            if (questionText != null && questionText.contains(output)) {
                iterator.remove();
            }
        }
    }

    public Question getAddNameQuestion(String beforeName, String userName, String afterName, List<AnswerChoice> answerChoices){
        Question nameQuestion = new Question(
                beforeName + userName + afterName,
                answerChoices
        );

        return nameQuestion;
    }
}
