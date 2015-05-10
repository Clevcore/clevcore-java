package ar.com.clevcore;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ar.com.clevcore.utils.Utils;

public class UtilsTest {

    List<Object> objectList;
    List<String> propertyList;
    String search;

    @Before
    public void init() {

        objectList = new ArrayList<Object>();
        MedicalStudy medicalStudy = new MedicalStudy();
        medicalStudy.setNameStudy("study");
        medicalStudy.setPatient(new Patient("pablo", "lopez"));

        MedicalStudy medicalStudy2 = new MedicalStudy();
        medicalStudy2.setNameStudy("study2");
        medicalStudy2.setPatient(new Patient("pedro", "moreno"));

        objectList.add(medicalStudy);
        propertyList = new ArrayList<String>();

    }

    @Test
    public void getValueFromNativePropertyTest() {
        String value = (String) Utils.getValueFromProperty(objectList.get(0), "nameStudy");
        assert (value.equals("study"));
    }

    @Test
    public void getValueFromNestedPropertyTest() {
        String value = (String) Utils.getValueFromProperty(objectList.get(0), "patient.surname");
        assert (value.equals("lopez"));
    }

    @Test
    public void searchObjectTestByNoSpecificProperty() {
        search = "study";

        List<Object> resultList = Utils.searchObject(search, objectList, null, false, "dd/MM/YYYY");
        MedicalStudy result = (MedicalStudy) resultList.get(0);

        assert (resultList.size() > 0 && "study".equals(result.getNameStudy()));
    }

    @Test
    public void searchObjectTestByNativeProperty() {

        propertyList.add("nameStudy");
        search = "study";

        List<Object> resultList = Utils.searchObject(search, objectList, propertyList, false, "dd/MM/YYYY");
        MedicalStudy result = (MedicalStudy) resultList.get(0);

        assert (resultList.size() > 0 && "study".equals(result.getNameStudy()));
    }

    @Test
    public void searchObjectTestByNestedProperty() {
        propertyList.add("patient.surname");
        search = "lopez";

        List<Object> resultList = Utils.searchObject(search, objectList, propertyList, false, "dd/MM/YYYY");
        assert (resultList.size() > 0);
        MedicalStudy result = (MedicalStudy) resultList.get(0);

        assert ("lopez".equals(result.getPatient().getSurname()));
    }

    protected class Patient {

        private String name;
        private String surname;

        public Patient(String name, String surname) {
            this.name = name;
            this.surname = surname;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }
    }

    protected class MedicalStudy {
        private long number = 5;
        private String nameStudy;
        private Patient patient;

        public String getNameStudy() {
            return nameStudy;
        }

        public void setNameStudy(String nameStudy) {
            this.nameStudy = nameStudy;
        }

        public Patient getPatient() {
            return patient;
        }

        public void setPatient(Patient patient) {
            this.patient = patient;
        }

        public long getNumber() {
            return number;
        }

        public void setNumber(long number) {
            this.number = number;
        }
    }

}
