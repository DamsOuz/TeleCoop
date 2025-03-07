package com.telecoop.telecoop.ui.quizz;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.telecoop.telecoop.data.Profile;
import com.telecoop.telecoop.data.Question;
import com.telecoop.telecoop.data.QuestionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizzViewModel extends ViewModel {
    private final QuestionRepository questionRepository;
    private List<Question> questions;
    private Map<Profile, Integer> profileScores = new HashMap<>();

    // Initialiser tous les profils à un score de 0
    {
        for (Profile p : Profile.values()) {
            profileScores.put(p, 0);
        }
    }

    // LiveData qui contiendra la question courante
    public MutableLiveData<Question> currentQuestion = new MutableLiveData<>();

    // LiveData pour les profils finaux
    private MutableLiveData<List<Profile>> finalProfiles = new MutableLiveData<>();

    public QuizzViewModel(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public void startQuizz() {
        questions = questionRepository.getQuestions();
        if (questions != null && !questions.isEmpty()) {
            currentQuestion.postValue(questions.get(0));
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }

    // Incrémenter le score des profils en paramètre
    public void incrementProfiles(List<Profile> profiles) {
        for (Profile p : profiles) {
            int oldScore = profileScores.get(p);
            profileScores.put(p, oldScore + 1);
        }
    }

    // Décrémenter le score des profils en paramètre
    public void decrementProfiles(List<Profile> profiles) {
        for (Profile p : profiles) {
            int oldScore = profileScores.get(p);
            profileScores.put(p, oldScore - 1);
        }
    }

    // Retourner les profils avec leurs scores
    public Map<Profile, Integer> getProfileScores() {
        return profileScores;
    }

    // Récupérer le ou les profils avec le score le plus élevé
    public List<Profile> getTopProfiles() {
        // Si la map de scores est vide, renvoyer une liste vide
        if (profileScores == null || profileScores.isEmpty()) {
            return Collections.emptyList();
        }

        // Vérifier si au moins un profil autre que le profil par défaut a un score > 0
        boolean anyNonDefault = false;
        for (Map.Entry<Profile, Integer> entry : profileScores.entrySet()) {
            if (entry.getKey() != Profile.PROFIL_PAR_DEFAUT && entry.getValue() != null && entry.getValue() > 0) {
                anyNonDefault = true;
                break;
            }
        }
        // Si aucun profil non-default n'a été incrémenté, retourner uniquement le profil par défaut
        if (!anyNonDefault) {
            List<Profile> defaultList = new ArrayList<>();
            defaultList.add(Profile.PROFIL_PAR_DEFAUT);
            return defaultList;
        }

        // Construire une liste des entrées en excluant le profil par défaut
        List<Map.Entry<Profile, Integer>> entries = new ArrayList<>();
        for (Map.Entry<Profile, Integer> entry : profileScores.entrySet()) {
            if (entry.getKey() != Profile.PROFIL_PAR_DEFAUT) {
                entries.add(entry);
            }
        }

        // Trier les entrées par score décroissant
        Collections.sort(entries, (e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));

        List<Profile> topProfiles = new ArrayList<>();
        if (!entries.isEmpty()) {
            // Ajouter tous les profils ex aequo pour la première place
            int highestScore = entries.get(0).getValue();
            for (Map.Entry<Profile, Integer> entry : entries) {
                if (entry.getValue() == highestScore) {
                    topProfiles.add(entry.getKey());
                } else {
                    break;
                }
            }
            // Si seulement un profil en première place et qu'il existe des profils avec le score du deuxième rang,
            // on ajoute également tous ceux qui ont ce score
            if (topProfiles.size() == 1 && entries.size() > 1) {
                int secondScore = entries.get(1).getValue();
                for (int i = 1; i < entries.size(); i++) {
                    if (entries.get(i).getValue() == secondScore && secondScore > 0) {
                        topProfiles.add(entries.get(i).getKey());
                    } else {
                        break;
                    }
                }
            }
        }
        return topProfiles;
    }

    public LiveData<List<Profile>> getFinalProfiles() {
        return finalProfiles;
    }

    public void computeFinalProfiles() {
        List<Profile> top = getTopProfiles();
        finalProfiles.setValue(top);

        // Afficher dans les logs
        if (top != null) {
            StringBuilder sb = new StringBuilder("Final profiles: ");
            for (Profile p : top) {
                sb.append(p.name()).append(" ");
            }
            Log.d("FinalProfiles", sb.toString());
        }
    }

}
