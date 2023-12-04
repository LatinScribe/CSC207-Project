// Author: André

package use_case.search;

import interface_adapter.search.SearchPresenter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import entity.University;
import entity.UniversityFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SearchInteractor implements SearchInputBoundary {
    final SearchUserDataAccessInterface searchDataAccessObject;
    final SearchOutputBoundary searchPresenter;
    final UniversityFactory universityFactory;

    public SearchInteractor(SearchUserDataAccessInterface searchUserDataAccessInterface,
                            SearchOutputBoundary searchPresenter, UniversityFactory universityFactory) {
        this.searchDataAccessObject = searchUserDataAccessInterface;
        this.searchPresenter = searchPresenter;
        this.universityFactory = universityFactory;
    }

    @Override
    public void executeSearch(SearchInputData searchInputData) {
        String stringAccumulator = "";
        String input = searchInputData.getSearchCriteria();
        for (int i = 0; i < input.length(); i++) {
            String cursor = input.substring(i, i+1);
            if (cursor.equals(" ")) {
                stringAccumulator += "%20";
            }
            else {
                stringAccumulator += cursor;
            }
        }
        try {
            JSONObject query = searchDataAccessObject.searchQuery("school.name=" + stringAccumulator);
            JSONObject metadata = query.getJSONObject("metadata");
            if (metadata.getInt("total") == 0) {
                searchPresenter.prepareResultsNotFoundView("Results not found!");
            } else {
                List<University> universityList = executeHelper(query.getJSONArray("results"));
                SearchOutputData searchOutputData = new SearchOutputData(universityList);
                searchPresenter.prepareSuccessView(searchOutputData);
            }
        } catch (JSONException e) {
            searchPresenter.prepareResultsNotFoundView("JSON Error! (" + e + ")");
            }
    }

    private List<University> executeHelper(JSONArray results) {
        List<University> universities = new ArrayList<University>();
        for (int i = 0; i < results.length(); i++) {
            // After every object is extracted, temporarily save its value as an object and check if it's null before implementing.
            JSONObject university = (JSONObject) results.get(i);
            Object idCheck = university.get("id");
            Integer id = integerChecker(idCheck);
            Object nameCheck = university.get("school.name");
            String name = stringChecker(nameCheck);
            Object stateCheck = university.get("school.state");
            String state = stringChecker(stateCheck);
            Object cityCheck = university.get("school.city");
            String city = stringChecker(cityCheck);
            Object admRateCheck = university.get("2018.admissions.admission_rate.overall");
            Double admRate = null;
            if (admRateCheck.toString().equals("0") || admRateCheck.toString().equals("1")) {
                Integer transition = integerChecker(admRateCheck);
                admRate = transition.doubleValue();
            }
            else {
                admRate = doubleChecker(admRateCheck);
            }
            Object outTuitCheck = university.get("2018.cost.tuition.out_of_state");
            Integer outTuit = integerChecker(outTuitCheck);
            Object inTuitCheck = university.get("2018.cost.tuition.in_state");
            Integer inTuit = integerChecker(inTuitCheck);
            Object avgSATCheck = university.get("2018.admissions.sat_scores.average.overall");
            Integer avgSAT = integerChecker(avgSATCheck) ;
            Object avgACTCheck = university.get("2018.admissions.act_scores.midpoint.cumulative");
            Integer avgACT = integerChecker(avgACTCheck);
            Object urlCheck = university.get("school.school_url");
            String url = stringChecker(urlCheck);

            University newUniversity = universityFactory.create(id, name, state, city, admRate, inTuit, outTuit, avgSAT, avgACT, url);
            universities.add(newUniversity);
        }
        return universities;

    }

    // The following 3 new methods are meant to check if the object returns a null type (JSONObject.null) or the proper type (Double, String, Integer).
    private Double doubleChecker(Object object) {
        String checker = object.toString();
        if (checker.equals("null")) {
            return null;
        }
        BigDecimal converter = (BigDecimal) object;
        return converter.doubleValue();
    }

    private Integer integerChecker(Object object) {
        String checker = object.toString();
        if (checker.equals("null")) {
            return null;
        }
        return (Integer) object;
    }

    private String stringChecker(Object object) {
        String checker = object.toString();
        if (checker.equals("null")) {
            return null;
        }
        return (String) object;
    }

    public void executeBack(){
        searchPresenter.prepareBackView();
    }


}