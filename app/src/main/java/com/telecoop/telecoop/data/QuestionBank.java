package com.telecoop.telecoop.data;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class QuestionBank {

    public List<Question> getQuestions(){
        return Arrays.asList(
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
